package com.aurora.quartz;

import cn.hutool.core.date.LocalDateTimeUtil;
import com.alibaba.fastjson.JSON;
import com.aurora.entity.Article;
import com.aurora.entity.Resource;
import com.aurora.entity.RoleResource;
import com.aurora.entity.UniqueView;
import com.aurora.entity.UserAuth;
import com.aurora.mapper.UniqueViewMapper;
import com.aurora.mapper.UserAuthMapper;
import com.aurora.model.dto.ArticleSearchDTO;
import com.aurora.model.dto.UserAreaDTO;
import com.aurora.repository.ElasticsearchMapper;
import com.aurora.service.ArticleService;
import com.aurora.service.JobLogService;
import com.aurora.service.RedisService;
import com.aurora.service.ResourceService;
import com.aurora.service.RoleResourceService;
import com.aurora.util.BeanCopyUtil;
import com.aurora.util.IpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.aurora.constant.CommonConstant.UNKNOWN;
import static com.aurora.constant.RedisConstant.UNIQUE_VISITOR;
import static com.aurora.constant.RedisConstant.USER_AREA;
import static com.aurora.constant.RedisConstant.VISITOR_AREA;

@Slf4j
@Component("auroraQuartz")
public class AuroraQuartz {

    /** 百度 SEO 推送目标地址 */
    private static final String BAIDU_SEO_URL = "https://www.baidu.com";
    /** 文章 URL 路径前缀 */
    private static final String ARTICLE_URL_PATH = "/articles/";
    /** HTTP 请求头：Host */
    private static final String HEADER_HOST = "Host";
    private static final String HEADER_HOST_VALUE = "data.zz.baidu.com";
    /** HTTP 请求头：User-Agent */
    private static final String HEADER_USER_AGENT = "User-Agent";
    private static final String HEADER_USER_AGENT_VALUE = "curl/7.12.1";
    /** HTTP 请求头：Content-Length */
    private static final String HEADER_CONTENT_LENGTH = "Content-Length";
    private static final String HEADER_CONTENT_LENGTH_VALUE = "83";
    /** HTTP 请求头：Content-Type */
    private static final String HEADER_CONTENT_TYPE = "Content-Type";
    private static final String HEADER_CONTENT_TYPE_VALUE = "text/plain";
    /** 默认访问量计数 */
    private static final int DEFAULT_VIEWS_COUNT = 0;
    /** 偏移天数：向前一天 */
    private static final int OFFSET_MINUS_ONE_DAY = -1;
    /** 管理员角色 ID */
    private static final int ADMIN_ROLE_ID = 1;

    @Autowired
    private RedisService redisService;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private JobLogService jobLogService;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private RoleResourceService roleResourceService;

    @Autowired
    private UniqueViewMapper uniqueViewMapper;

    @Autowired
    private UserAuthMapper userAuthMapper;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ElasticsearchMapper elasticsearchMapper;

    @Value("${website.url}")
    private String websiteUrl;

    public void saveUniqueView() {
        Long count = redisService.sSize(UNIQUE_VISITOR);
        UniqueView uniqueView = UniqueView.builder()
                .createTime(LocalDateTimeUtil.offset(LocalDateTime.now(), OFFSET_MINUS_ONE_DAY, ChronoUnit.DAYS))
                .viewsCount(Optional.of(count.intValue()).orElse(DEFAULT_VIEWS_COUNT))
                .build();
        uniqueViewMapper.insert(uniqueView);
    }

    public void clear() {
        redisService.del(UNIQUE_VISITOR);
        redisService.del(VISITOR_AREA);
    }

    public void statisticalUserArea() {
        Map<String, Long> userAreaMap = userAuthMapper.selectList(new LambdaQueryWrapper<UserAuth>().select(UserAuth::getIpSource))
                .stream()
                .map(item -> {
                    if (Objects.nonNull(item) && StringUtils.isNotBlank(item.getIpSource())) {
                        return IpUtil.getIpProvince(item.getIpSource());
                    }
                    return UNKNOWN;
                })
                .collect(Collectors.groupingBy(item -> item, Collectors.counting()));
        List<UserAreaDTO> userAreaList = userAreaMap.entrySet().stream()
                .map(item -> UserAreaDTO.builder()
                        .name(item.getKey())
                        .value(item.getValue())
                        .build())
                .collect(Collectors.toList());
        redisService.set(USER_AREA, JSON.toJSONString(userAreaList));
    }

    public void baiduSeo() {
        List<Integer> ids = articleService.list().stream().map(Article::getId).collect(Collectors.toList());
        HttpHeaders headers = new HttpHeaders();
        headers.add(HEADER_HOST, HEADER_HOST_VALUE);
        headers.add(HEADER_USER_AGENT, HEADER_USER_AGENT_VALUE);
        headers.add(HEADER_CONTENT_LENGTH, HEADER_CONTENT_LENGTH_VALUE);
        headers.add(HEADER_CONTENT_TYPE, HEADER_CONTENT_TYPE_VALUE);
        // 向百度推送文章 URL 用于 SEO 收录
        ids.forEach(item -> {
            String url = websiteUrl + ARTICLE_URL_PATH + item;
            HttpEntity<String> entity = new HttpEntity<>(url, headers);
            restTemplate.postForObject(BAIDU_SEO_URL, entity, String.class);
        });
    }

    public void clearJobLogs() {
        jobLogService.cleanJobLogs();
    }

    public void importSwagger() {
        resourceService.importSwagger();
        List<Integer> resourceIds = resourceService.list().stream().map(Resource::getId).collect(Collectors.toList());
        List<RoleResource> roleResources = new ArrayList<>();
        // 将所有资源绑定到管理员角色
        for (Integer resourceId : resourceIds) {
            roleResources.add(RoleResource.builder()
                    .roleId(ADMIN_ROLE_ID)
                    .resourceId(resourceId)
                    .build());
        }
        roleResourceService.saveBatch(roleResources);
    }

    public void importDataIntoES() {
        elasticsearchMapper.deleteAll();
        List<Article> articles = articleService.list();
        // 全量同步文章数据到 Elasticsearch
        for (Article article : articles) {
            elasticsearchMapper.save(BeanCopyUtil.copyObject(article, ArticleSearchDTO.class));
        }
    }
}
