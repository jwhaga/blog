package com.aurora.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MarkdownTypeEnum {

    NORMAL("", "normalArticleImportStrategyImpl");

    private final String type;

    private final String strategy;

    /**
     * 根据类型名获取对应的策略 Bean 名称。
     *
     * @param name 类型名，为 null 时返回默认策略
     * @return 对应的策略 Bean 名称，未匹配时返回 null
     */
    public static String getMarkdownType(String name) {
        if (name == null) {
            return NORMAL.getStrategy();
        }
        for (MarkdownTypeEnum value : MarkdownTypeEnum.values()) {
            if (value.getType().equalsIgnoreCase(name)) {
                return value.getStrategy();
            }
        }
        return null;
    }
}
