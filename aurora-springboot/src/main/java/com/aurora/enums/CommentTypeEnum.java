package com.aurora.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 评论类型枚举。
 * type 值对应数据库 t_comment.type 字段。
 */
@Getter
@AllArgsConstructor
public enum CommentTypeEnum {

    ARTICLE(1, "文章", "/articles/"),

    MESSAGE(2, "留言", "/message/"),

    ABOUT(3, "关于我", "/about/"),

    LINK(4, "友链", "/friends/"),

    TALK(5, "说说", "/talks/");

    private final Integer type;
    private final String desc;
    private final String path;

    /**
     * 根据类型值获取对应的评论路径。
     * @param type 评论类型值
     * @return 对应的路径，未匹配时返回 null
     */
    public static String getCommentPath(Integer type) {
        for (CommentTypeEnum value : CommentTypeEnum.values()) {
            if (value.getType().equals(type)) {
                return value.getPath();
            }
        }
        return null;
    }

    /**
     * 根据类型值获取对应的枚举实例。
     * @param type 评论类型值
     * @return 对应的枚举实例，未匹配时返回 null
     */
    public static CommentTypeEnum getCommentEnum(Integer type) {
        for (CommentTypeEnum value : CommentTypeEnum.values()) {
            if (value.getType().equals(type)) {
                return value;
            }
        }
        return null;
    }

}
