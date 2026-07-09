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

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("aurora文档")
                        .description("aurora")
                        .contact(new Contact()
                                .name("花未眠")
                                .email("1909925152@qq.com"))
                        .termsOfService(websiteUrl + "/api")
                        .version("1.0"))
                .servers(Collections.singletonList(
                        new Server()
                                .url(websiteUrl)
                                .description("线上服务地址")));
    }

    @Bean
    public GroupedOpenApi controllerApi() {
        return GroupedOpenApi.builder()
                .group("default")
                .packagesToScan("com.aurora.controller")
                .pathsToMatch("/**")
                .build();
    }

}

