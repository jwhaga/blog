package com.aurora.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 系统状态码枚举。
 * 前两位近似 HTTP 状态码语义：2xx 成功，4xx 客户端错误，5xx 服务端错误。
 * 第三位为业务子码。
 */
@Getter
@AllArgsConstructor
public enum StatusCodeEnum {

    SUCCESS(20000, "操作成功"),

    NO_LOGIN(40001, "用户未登录"),

    AUTHORIZED(40300, "没有操作权限"),

    NOT_FOUND(40400, "资源不存在"),

    METHOD_NOT_ALLOWED(40500, "请求方法不支持"),

    SYSTEM_ERROR(50000, "系统异常"),

    FAIL(51000, "操作失败"),

    VALID_ERROR(52000, "参数格式不正确"),

    USERNAME_EXIST(52001, "用户名已存在"),

    USERNAME_NOT_EXIST(52002, "用户名不存在"),

    ARTICLE_ACCESS_FAIL(52003, "文章密码认证未通过"),

    QQ_LOGIN_ERROR(53001, "qq登录错误");

    private final Integer code;

    private final String desc;

}
