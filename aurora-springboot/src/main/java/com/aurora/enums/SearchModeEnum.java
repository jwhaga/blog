package com.aurora.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SearchModeEnum {

    MYSQL("mysql", "mySqlSearchStrategyImpl"),

    ELASTICSEARCH("elasticsearch", "esSearchStrategyImpl");

    private final String mode;

    private final String strategy;

    /**
     * 根据搜索模式获取对应的策略 Bean 名称。
     *
     * @param mode 搜索模式名
     * @return 对应的策略 Bean 名称，未匹配时返回 null
     */
    public static String getStrategy(String mode) {
        for (SearchModeEnum value : SearchModeEnum.values()) {
            if (value.getMode().equals(mode)) {
                return value.getStrategy();
            }
        }
        return null;
    }

}
