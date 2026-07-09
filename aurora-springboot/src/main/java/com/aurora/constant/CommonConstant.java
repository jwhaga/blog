package com.aurora.constant;

/**
 * 系统通用常量定义。
 * 使用 interface 而非 final class，字段默认 public static final。
 */
public interface CommonConstant {

    /**
     * 数据库主键默认起始值
     */
    int PRIMARY_ID_START = 1;

    /**
     * 博客作者的用户信息 ID（初始化数据中固定为 1）
     */
    int BLOGGER_USER_INFO_ID = 1;

    /**
     * 网站配置的默认记录 ID（初始化数据中固定为 1）
     */
    int DEFAULT_CONFIG_ID = 1;

    /**
     * 关于页的默认记录 ID（初始化数据中固定为 1）
     */
    int DEFAULT_ABOUT_ID = 1;

    /**
     * 表示"是/启用"的整数值（数据库中用 tinyint(1) 表示布尔语义）
     */
    int TRUE = 1;

    /**
     * 表示"否/禁用"的整数值（数据库中用 tinyint(1) 表示布尔语义）
     */
    int FALSE = 0;

    /**
     * 搜索引擎高亮前置标签
     */
    String PRE_TAG = "<mark>";

    /**
     * 搜索引擎高亮后置标签
     */
    String POST_TAG = "</mark>";

    /**
     * 分页查询中当前页码的参数名
     */
    String CURRENT_PARAM = "current";

    /**
     * 分页查询中每页条数的参数名
     */
    String SIZE_PARAM = "size";

    /**
     * 默认每页条数
     */
    String DEFAULT_SIZE = "10";

    /**
     * 新用户默认昵称前缀
     */
    String DEFAULT_NICKNAME = "用户";

    /**
     * 前端路由组件名，标识布局类型路由
     */
    String LAYOUT_COMPONENT = "Layout";

    /**
     * 未知地区/来源的占位文案
     */
    String UNKNOWN = "未知";

    /**
     * 统一 JSON 响应内容类型
     */
    String APPLICATION_JSON = "application/json;charset=utf-8";

    /**
     * 邮件主题：验证码
     */
    String CAPTCHA_SUBJECT = "验证码";

    /**
     * 邮件主题：审核提醒
     */
    String CHECK_REMIND_SUBJECT = "审核提醒";

    /**
     * 邮件主题：评论提醒
     */
    String COMMENT_REMIND_SUBJECT = "评论提醒";

    /**
     * 邮件主题：@提及提醒
     */
    String MENTION_REMIND_SUBJECT = "@提醒";

}
