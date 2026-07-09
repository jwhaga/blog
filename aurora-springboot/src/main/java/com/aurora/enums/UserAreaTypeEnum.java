package com.aurora.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserAreaTypeEnum {

    USER(1, "用户"),

    VISITOR(2, "游客");

    private final Integer type;

    private final String desc;

    /**
     * 根据类型值获取对应的枚举实例。
     *
     * @param type 用户区域类型值
     * @return 对应的枚举实例，未匹配时返回 null
     */
    public static UserAreaTypeEnum getUserAreaType(Integer type) {
        for (UserAreaTypeEnum value : UserAreaTypeEnum.values()) {
            if (value.getType().equals(type)) {
                return value;
            }
        }
        return null;
    }

}
