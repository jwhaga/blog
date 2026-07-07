package com.aurora.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "查询条件")
public class ConditionVO {

    @Schema(description = "页码")
    private Long current;

    @Schema(description = "条数")
    private Long size;

    @Schema(description = "搜索内容")
    private String keywords;

    @Schema(description = "分类id")
    private Integer categoryId;

    @Schema(description = "标签id")
    private Integer tagId;

    @Schema(description = "相册id")
    private Integer albumId;

    @Schema(description = "登录类型")
    private Integer loginType;

    @Schema(description = "类型")
    private Integer type;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    private LocalDateTime endTime;

    @Schema(description = "是否删除")
    private Integer isDelete;

    @Schema(description = "是否审核")
    private Integer isReview;

    @Schema(description = "是否置顶")
    private Integer isTop;

    @Schema(description = "是否推荐")
    private Integer isFeatured;


}
