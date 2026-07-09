package com.aurora.constant;

/**
 * 认证相关常量。
 */
public interface AuthConstant {

    /**
     * 验证码有效期（分钟）
     */
    int CAPTCHA_EXPIRE_MINUTES = 20;

    /**
     * JWT Token 有效期（秒），7 天 = 7 * 24 * 60 * 60
     */
    int TOKEN_EXPIRE_SECONDS = 7 * 24 * 60 * 60;

    /**
     * 请求头中携带 Token 的字段名
     */
    String TOKEN_HEADER = "Authorization";

    /**
     * Token 前缀，与服务端约定以 "Bearer " 开头
     */
    String TOKEN_PREFIX = "Bearer ";

}
