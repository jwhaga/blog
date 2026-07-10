package com.aurora.consumer;

import com.alibaba.fastjson.JSON;
import com.aurora.entity.Article;
import com.aurora.entity.UserInfo;
import com.aurora.model.dto.EmailDTO;
import com.aurora.service.ArticleService;
import com.aurora.service.UserInfoService;
import com.aurora.util.EmailUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.aurora.constant.CommonConstant.TRUE;
import static com.aurora.constant.RabbitMQConstant.SUBSCRIBE_QUEUE;

@Component
@RabbitListener(queues = SUBSCRIBE_QUEUE)
public class SubscribeConsumer {

    /** 邮件主题：文章订阅 */
    private static final String SUBJECT = "文章订阅";
    /** 邮件模板：通用模板 */
    private static final String TEMPLATE_COMMON = "common.html";
    /** 文章 URL 路径前缀 */
    private static final String ARTICLE_URL_PATH = "/articles/";
    /** 邮件链接样式 */
    private static final String LINK_STYLE = "text-decoration:none;color:#12addb";
    /** 邮件链接文案 */
    private static final String LINK_TEXT = "点击查看";
    /** 新文章通知文案前缀 */
    private static final String NEW_ARTICLE_PREFIX = "花未眠的个人博客发布了新的文章，";
    /** 文章更新通知文案前缀模板 */
    private static final String UPDATE_ARTICLE_PREFIX = "花未眠的个人博客对《%s》进行了更新，";

    @Value("${website.url}")
    private String websiteUrl;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private EmailUtil emailUtil;

    @RabbitHandler
    public void process(byte[] data) {
        if (data == null || data.length == 0) {
            return;
        }
        Integer articleId = JSON.parseObject(new String(data), Integer.class);
        if (articleId == null) {
            return;
        }
        Article article = articleService.getOne(new LambdaQueryWrapper<Article>().eq(Article::getId, articleId));
        if (article == null) {
            return;
        }
        List<UserInfo> users = userInfoService.list(new LambdaQueryWrapper<UserInfo>().eq(UserInfo::getIsSubscribe, TRUE));
        List<String> emails = users.stream().map(UserInfo::getEmail).collect(Collectors.toList());
        String url = websiteUrl + ARTICLE_URL_PATH + articleId;
        for (String email : emails) {
            EmailDTO emailDTO = new EmailDTO();
            Map<String, Object> map = new HashMap<>();
            emailDTO.setEmail(email);
            emailDTO.setSubject(SUBJECT);
            emailDTO.setTemplate(TEMPLATE_COMMON);
            // 根据是否为更新构建不同的邮件内容
            if (article.getUpdateTime() == null) {
                map.put("content", NEW_ARTICLE_PREFIX + buildLink(url));
            } else {
                map.put("content", String.format(UPDATE_ARTICLE_PREFIX, article.getArticleTitle()) + buildLink(url));
            }
            emailDTO.setCommentMap(map);
            emailUtil.sendHtmlMail(emailDTO);
        }
    }

    /**
     * 构建邮件内容中的超链接 HTML 片段
     *
     * @param url 链接地址
     * @return HTML a 标签字符串
     */
    private String buildLink(String url) {
        return "<a style=\"" + LINK_STYLE + "\" href=\"" + url + "\">" + LINK_TEXT + "</a>";
    }

}
