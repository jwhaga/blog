package com.aurora.model.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "文章")
public class ArticleVO {

    @Schema(description = "文章id")
    private Integer id;

    @NotBlank(message = "文章标题不能为空")
    @Schema(description = "文章标题", requiredMode = Schema.RequiredMode.REQUIRED)
    private String articleTitle;

    @NotBlank(message = "文章内容不能为空")
    @Schema(description = "文章内容", requiredMode = Schema.RequiredMode.REQUIRED)
    private String articleContent;

    @Schema(description = "文章摘要")
    private String articleAbstract;

    @Schema(description = "文章缩略图")
    private String articleCover;

    @Schema(name = "category", description = "文章分类")
    private String categoryName;

    @Schema(name = "tagNameList", description = "文章标签")
    private List<String> tagNames;

    @Schema(description = "是否置顶")
    private Integer isTop;

    @Schema(description = "是否推荐")
    private Integer isFeatured;

    @Schema(description = "文章状态")
    private Integer status;

    @Schema(description = "文章类型")
    private Integer type;

    @Schema(description = "原文链接")
    private String originalUrl;

    @Schema(description = "文章访问密码")
    private String password;
}
