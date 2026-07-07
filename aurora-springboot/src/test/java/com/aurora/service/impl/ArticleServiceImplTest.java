package com.aurora.service.impl;

import com.aurora.entity.Article;
import com.aurora.entity.Category;
import com.aurora.exception.BizException;
import com.aurora.mapper.ArticleMapper;
import com.aurora.mapper.ArticleTagMapper;
import com.aurora.mapper.CategoryMapper;
import com.aurora.mapper.TagMapper;
import com.aurora.model.dto.*;
import com.aurora.model.vo.ArticleVO;
import com.aurora.model.vo.ConditionVO;
import com.aurora.service.ArticleTagService;
import com.aurora.service.RedisService;
import com.aurora.service.TagService;
import com.aurora.strategy.context.SearchStrategyContext;
import com.aurora.strategy.context.UploadStrategyContext;
import com.aurora.util.PageUtil;
import com.aurora.util.UserUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.aurora.constant.RedisConstant.ARTICLE_ACCESS;
import static com.aurora.constant.RedisConstant.ARTICLE_VIEWS_COUNT;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ArticleServiceImpl 单元测试")
class ArticleServiceImplTest {

    @InjectMocks
    private ArticleServiceImpl articleService;

    @Mock
    private ArticleMapper articleMapper;
    @Mock
    private ArticleTagMapper articleTagMapper;
    @Mock
    private CategoryMapper categoryMapper;
    @Mock
    private TagMapper tagMapper;
    @Mock
    private TagService tagService;
    @Mock
    private ArticleTagService articleTagService;
    @Mock
    private RedisService redisService;
    @Mock
    private RabbitTemplate rabbitTemplate;
    @Mock
    private UploadStrategyContext uploadStrategyContext;
    @Mock
    private SearchStrategyContext searchStrategyContext;

    private static final Integer TEST_ARTICLE_ID = 1;
    private static final Integer TEST_USER_INFO_ID = 100;

    @BeforeEach
    void setUp() {
        PageUtil.setCurrentPage(new Page<>(1, 10));
        initBaseMapper();
    }

    private void initBaseMapper() {
        ReflectionTestUtils.setField(articleService, "baseMapper", articleMapper);
    }

    @Nested
    @DisplayName("获取置顶和推荐文章")
    class ListTopAndFeatured {

        @Test
        @DisplayName("应返回置顶和推荐文章列表")
        void shouldReturnTopAndFeatured() {
            ArticleCardDTO topArticle = ArticleCardDTO.builder().id(1).articleTitle("Top Article").build();
            ArticleCardDTO featured1 = ArticleCardDTO.builder().id(2).articleTitle("Featured 1").build();
            List<ArticleCardDTO> mockList = new ArrayList<>(List.of(topArticle, featured1));
            when(articleMapper.listTopAndFeaturedArticles()).thenReturn(mockList);

            TopAndFeaturedArticlesDTO result = articleService.listTopAndFeaturedArticles();

            assertNotNull(result);
            assertEquals("Top Article", result.getTopArticle().getArticleTitle());
            assertEquals(1, result.getFeaturedArticles().size());
            assertEquals("Featured 1", result.getFeaturedArticles().get(0).getArticleTitle());
        }

        @Test
        @DisplayName("无文章时应返回空对象")
        void shouldReturnEmptyWhenNoArticles() {
            when(articleMapper.listTopAndFeaturedArticles()).thenReturn(new ArrayList<>());

            TopAndFeaturedArticlesDTO result = articleService.listTopAndFeaturedArticles();

            assertNotNull(result);
            assertNull(result.getTopArticle());
            assertNull(result.getFeaturedArticles());
        }
    }

    @Nested
    @DisplayName("分页查询文章列表")
    class ListArticles {

        @Test
        @DisplayName("应返回分页文章列表")
        void shouldReturnPaginatedArticles() {
            ArticleCardDTO article1 = ArticleCardDTO.builder().id(1).articleTitle("Article 1").build();
            ArticleCardDTO article2 = ArticleCardDTO.builder().id(2).articleTitle("Article 2").build();
            List<ArticleCardDTO> mockArticles = List.of(article1, article2);
            when(articleMapper.selectCount(any())).thenReturn(2L);
            when(articleMapper.listArticles(anyLong(), anyLong())).thenReturn(mockArticles);

            PageResultDTO<ArticleCardDTO> result = articleService.listArticles();

            assertNotNull(result);
            assertEquals(2, result.getCount());
            assertEquals(2, result.getRecords().size());
            assertEquals("Article 1", result.getRecords().get(0).getArticleTitle());
        }
    }

    @Nested
    @DisplayName("获取文章详情")
    class GetArticleById {

        @Test
        @DisplayName("存在且公开的文章应返回详情")
        void shouldReturnArticleDetailWhenExists() {
            Article mockArticle = Article.builder().id(TEST_ARTICLE_ID).status(1).articleTitle("Test Article").build();
            when(articleMapper.selectOne(any())).thenReturn(mockArticle);
            doReturn(1D).when(redisService).zIncr(eq(ARTICLE_VIEWS_COUNT), eq(TEST_ARTICLE_ID), eq(1D));

            ArticleDTO mockDetail = ArticleDTO.builder()
                    .id(TEST_ARTICLE_ID)
                    .articleTitle("Test Article")
                    .viewCount(0)
                    .build();
            when(articleMapper.getArticleById(TEST_ARTICLE_ID)).thenReturn(mockDetail);
            when(articleMapper.getPreArticleById(TEST_ARTICLE_ID)).thenReturn(null);
            when(articleMapper.getLastArticle()).thenReturn(null);
            when(articleMapper.getNextArticleById(TEST_ARTICLE_ID)).thenReturn(null);
            when(articleMapper.getFirstArticle()).thenReturn(null);
            when(redisService.zScore(ARTICLE_VIEWS_COUNT, TEST_ARTICLE_ID)).thenReturn(null);

            ArticleDTO result = articleService.getArticleById(TEST_ARTICLE_ID);

            assertNotNull(result);
            assertEquals("Test Article", result.getArticleTitle());
            verify(redisService).zIncr(eq(ARTICLE_VIEWS_COUNT), eq(TEST_ARTICLE_ID), eq(1D));
        }

        @Test
        @DisplayName("不存在的文章应返回 null")
        void shouldReturnNullWhenNotExists() {
            when(articleMapper.selectOne(any())).thenReturn(null);

            ArticleDTO result = articleService.getArticleById(TEST_ARTICLE_ID);

            assertNull(result);
        }

        @Test
        @DisplayName("密码文章无权限时应抛出异常")
        void shouldThrowWhenNoAccessToPasswordArticle() {
            Article mockArticle = Article.builder().id(TEST_ARTICLE_ID).status(2).articleTitle("Protected").build();
            when(articleMapper.selectOne(any())).thenReturn(mockArticle);

            try (MockedStatic<UserUtil> userUtilMock = mockStatic(UserUtil.class)) {
                UserDetailsDTO mockUser = UserDetailsDTO.builder().id(1).build();
                userUtilMock.when(UserUtil::getUserDetailsDTO).thenReturn(mockUser);

                when(redisService.sIsMember(ARTICLE_ACCESS + 1, TEST_ARTICLE_ID)).thenReturn(false);

                assertThrows(BizException.class, () -> articleService.getArticleById(TEST_ARTICLE_ID));
            }
        }
    }

    @Nested
    @DisplayName("保存或更新文章")
    class SaveOrUpdateArticle {

        @Test
        @DisplayName("新文章应保存到数据库并发送 MQ 通知")
        void shouldSaveNewArticleAndSendMQ() {
            ArticleVO articleVO = ArticleVO.builder()
                    .articleTitle("New Article")
                    .articleContent("Content")
                    .categoryName("Tech")
                    .tagNames(new ArrayList<>(List.of("Java", "Spring")))
                    .status(1)
                    .build();

            when(categoryMapper.selectOne(any())).thenReturn(null);
            Category newCategory = Category.builder().id(1).categoryName("Tech").build();
            when(categoryMapper.insert(any(Category.class))).thenReturn(1);

            try (MockedStatic<UserUtil> userUtilMock = mockStatic(UserUtil.class)) {
                UserDetailsDTO mockUser = UserDetailsDTO.builder().userInfoId(TEST_USER_INFO_ID).build();
                userUtilMock.when(UserUtil::getUserDetailsDTO).thenReturn(mockUser);

                articleService.saveOrUpdateArticle(articleVO);

                verify(categoryMapper).insert(any(Category.class));
                // MQ 通知应在文章状态为 1 (PUBLIC) 时发送
                verify(rabbitTemplate).convertAndSend(anyString(), eq("*"), any(Object.class));
            }
        }

        @Test
        @DisplayName("草稿文章不应发送 MQ 通知")
        void shouldNotSendMQForDraft() {
            ArticleVO articleVO = ArticleVO.builder()
                    .articleTitle("Draft Article")
                    .articleContent("Draft Content")
                    .categoryName("Tech")
                    .tagNames(new ArrayList<>(List.of("Java")))
                    .status(0)
                    .build();

            when(categoryMapper.selectOne(any())).thenReturn(null);

            try (MockedStatic<UserUtil> userUtilMock = mockStatic(UserUtil.class)) {
                UserDetailsDTO mockUser = UserDetailsDTO.builder().userInfoId(TEST_USER_INFO_ID).build();
                userUtilMock.when(UserUtil::getUserDetailsDTO).thenReturn(mockUser);

                articleService.saveOrUpdateArticle(articleVO);

                verify(rabbitTemplate, never()).convertAndSend(anyString(), anyString(), any(Object.class));
            }
        }
    }

    @Nested
    @DisplayName("搜索文章")
    class SearchArticles {

        @Test
        @DisplayName("应委托搜索策略执行搜索")
        void shouldDelegateToSearchStrategy() {
            ConditionVO condition = new ConditionVO();
            condition.setKeywords("spring");
            ArticleSearchDTO searchResult = ArticleSearchDTO.builder()
                    .id(TEST_ARTICLE_ID)
                    .articleTitle("Spring Boot Guide")
                    .build();
            when(searchStrategyContext.executeSearchStrategy("spring")).thenReturn(List.of(searchResult));

            List<ArticleSearchDTO> results = articleService.listArticlesBySearch(condition);

            assertNotNull(results);
            assertEquals(1, results.size());
            assertEquals("Spring Boot Guide", results.get(0).getArticleTitle());
            verify(searchStrategyContext).executeSearchStrategy("spring");
        }

        @Test
        @DisplayName("空关键词应返回空结果")
        void shouldReturnEmptyWhenNoKeywords() {
            ConditionVO condition = new ConditionVO();
            condition.setKeywords("");
            when(searchStrategyContext.executeSearchStrategy("")).thenReturn(new ArrayList<>());

            List<ArticleSearchDTO> results = articleService.listArticlesBySearch(condition);

            assertNotNull(results);
            assertTrue(results.isEmpty());
        }
    }

}
