package com.aurora.mapper;

import com.aurora.model.dto.ArticleAdminDTO;
import com.aurora.model.dto.ArticleCardDTO;
import com.aurora.model.dto.ArticleDTO;
import com.aurora.model.dto.ArticleStatisticsDTO;
import com.aurora.entity.Article;
import com.aurora.model.vo.ConditionVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleMapper extends BaseMapper<Article> {

    /**
     * 查询置顶和推荐文章卡片列表
     */
    List<ArticleCardDTO> listTopAndFeaturedArticles();

    /**
     * 分页查询文章卡片列表
     */
    List<ArticleCardDTO> listArticles(@Param("current") Long current, @Param("size") Long size);

    /**
     * 根据分类id分页查询文章卡片列表
     */
    List<ArticleCardDTO> getArticlesByCategoryId(@Param("current") Long current, @Param("size") Long size, @Param("categoryId") Integer categoryId);

    /**
     * 根据文章id查询文章详情
     */
    ArticleDTO getArticleById(@Param("articleId") Integer articleId);

    /**
     * 查询上一篇文章卡片
     */
    ArticleCardDTO getPreArticleById(@Param("articleId") Integer articleId);

    /**
     * 查询下一篇文章卡片
     */
    ArticleCardDTO getNextArticleById(@Param("articleId") Integer articleId);

    /**
     * 查询第一篇文章卡片
     */
    ArticleCardDTO getFirstArticle();

    /**
     * 查询最后一篇文章卡片
     */
    ArticleCardDTO getLastArticle();

    /**
     * 根据标签id分页查询文章卡片列表
     */
    List<ArticleCardDTO> listArticlesByTagId(@Param("current") Long current, @Param("size") Long size, @Param("tagId") Integer tagId);

    /**
     * 分页查询归档文章列表
     */
    List<ArticleCardDTO> listArchives(@Param("current") Long current, @Param("size") Long size);

    /**
     * 后台统计符合条件的文章数量
     */
    Long countArticleAdmins(@Param("conditionVO") ConditionVO conditionVO);

    /**
     * 后台分页查询文章列表
     */
    List<ArticleAdminDTO> listArticlesAdmin(@Param("current") Long current, @Param("size") Long size, @Param("conditionVO") ConditionVO conditionVO);

    /**
     * 统计文章数据（用于图表展示）
     */
    List<ArticleStatisticsDTO> listArticleStatistics();

}

