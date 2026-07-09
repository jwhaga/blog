package com.aurora.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

@Configuration
public class Knife4jConfig {

    // 网站部署地址，从 application-*.yml 的 website.url 注入
    // 部署前改为你的实际域名（如 https://www.yourdomain.com）
    @Value("${website.url:http://localhost:8080}")
    private String websiteUrl;

    // API 文档元信息常量
    private static final String DOC_TITLE = "aurora文档";
    private static final String DOC_DESCRIPTION = "aurora";
    private static final String CONTACT_NAME = "花未眠";
    private static final String CONTACT_EMAIL = "1909925152@qq.com";
    private static final String DOC_VERSION = "1.0";
    private static final String DEFAULT_GROUP = "default";
    private static final String CONTROLLER_PACKAGE = "com.aurora.controller";
    private static final String ALL_PATHS = "/**";

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title(DOC_TITLE)
                        .description(DOC_DESCRIPTION)
                        .contact(new Contact()
                                .name(CONTACT_NAME)
                                .email(CONTACT_EMAIL))
                        .termsOfService(websiteUrl + "/api")
                        .version(DOC_VERSION))
                .servers(Collections.singletonList(
                        new Server()
                                .url(websiteUrl)
                                .description("线上服务地址")));
    }

    @Bean
    public GroupedOpenApi controllerApi() {
        return GroupedOpenApi.builder()
                .group(DEFAULT_GROUP)
                .packagesToScan(CONTROLLER_PACKAGE)
                .pathsToMatch(ALL_PATHS)
                .build();
    }

}
