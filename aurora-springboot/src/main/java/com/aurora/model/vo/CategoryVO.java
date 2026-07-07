package com.aurora.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "分类")
public class CategoryVO {

    @Schema(description = "分类id")
    private Integer id;

    @NotBlank(message = "分类名不能为空")
    @Schema(description = "分类名", requiredMode = Schema.RequiredMode.REQUIRED)
    private String categoryName;

}
