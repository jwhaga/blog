package com.aurora.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UploadModeEnum {

    OSS("oss", "ossUploadStrategyImpl"),

    MINIO("minio", "minioUploadStrategyImpl");

    private final String mode;

    private final String strategy;

    /**
     * 根据上传模式获取对应的策略 Bean 名称。
     *
     * @param mode 上传模式名
     * @return 对应的策略 Bean 名称，未匹配时返回 null
     */
    public static String getStrategy(String mode) {
        for (UploadModeEnum value : UploadModeEnum.values()) {
            if (value.getMode().equals(mode)) {
                return value.getStrategy();
            }
        }
        return null;
    }

}
