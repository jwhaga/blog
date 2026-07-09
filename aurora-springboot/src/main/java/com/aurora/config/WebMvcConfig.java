package com.aurora.config;


import com.aurora.interceptor.PaginationInterceptor;
import com.aurora.interceptor.AccessLimitInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private PaginationInterceptor paginationInterceptor;

    @Autowired
    private AccessLimitInterceptor accessLimitInterceptor;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // CORS: allowedOriginPatterns("*") with allowCredentials(true) is acceptable for dev.
        // For production, restrict to specific origins (e.g. https://yourdomain.com).
        // Note: Spring 6.x does not allow allowCredentials(true) + allowedOrigins("*") simultaneously.
        registry.addMapping("/**")
                .allowCredentials(true)
                // 开发环境使用通配符 "*" 便于调试；生产环境应将 allowedOriginPatterns 改为具体域名（如 https://yourdomain.com）
                .allowedHeaders("*")
                .allowedOriginPatterns("*")
                .allowedMethods("*");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(paginationInterceptor);
        registry.addInterceptor(accessLimitInterceptor);
    }

}
