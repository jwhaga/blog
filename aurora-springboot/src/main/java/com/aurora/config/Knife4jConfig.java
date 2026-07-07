package com.aurora.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

@Configuration
public class Knife4jConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("aurora文档")
                        .description("aurora")
                        .contact(new Contact()
                                .name("花未眠")
                                .email("1909925152@qq.com"))
                        .termsOfService("https://www.linhaojun.top/api")
                        .version("1.0"))
                .servers(Collections.singletonList(
                        new Server()
                                .url("https://www.linhaojun.top")
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
