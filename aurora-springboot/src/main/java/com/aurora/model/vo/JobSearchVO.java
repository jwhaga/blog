package com.aurora.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobSearchVO {

    @Schema(description = "任务名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String jobName;

    @Schema(description = "任务组别", requiredMode = Schema.RequiredMode.REQUIRED)
    private String jobGroup;

    @Schema(description = "任务状态", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer status;
}
