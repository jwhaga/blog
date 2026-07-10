package com.aurora.controller;

import com.aurora.annotation.OptLog;
import com.aurora.enums.FilePathEnum;
import com.aurora.model.dto.ArchiveDTO;
import com.aurora.model.dto.ArticleAdminDTO;
import com.aurora.model.dto.ArticleAdminViewDTO;
import com.aurora.model.dto.ArticleCardDTO;
import com.aurora.model.dto.ArticleDTO;
import com.aurora.model.dto.ArticleSearchDTO;
import com.aurora.model.dto.PageResultDTO;
import com.aurora.model.dto.TopAndFeaturedArticlesDTO;
import com.aurora.model.vo.ArticlePasswordVO;
import com.aurora.model.vo.ArticleTopFeaturedVO;
import com.aurora.model.vo.ArticleVO;
import com.aurora.model.vo.ConditionVO;
import com.aurora.model.vo.DeleteVO;
import com.aurora.model.vo.ResultVO;
import com.aurora.service.ArticleService;
import com.aurora.strategy.context.ArticleImportStrategyContext;
import com.aurora.strategy.context.UploadStrategyContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;

import java.util.List;

import static com.aurora.constant.OptTypeConstant.DELETE;
import static com.aurora.constant.OptTypeConstant.EXPORT;
import static com.aurora.constant.OptTypeConstant.SAVE_OR_UPDATE;
import static com.aurora.constant.OptTypeConstant.UPDATE;
import static com.aurora.constant.OptTypeConstant.UPLOAD;

@Tag(name = "文章模块")
@RestController
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private UploadStrategyContext uploadStrategyContext;

    @Autowired
    private ArticleImportStrategyContext articleImportStrategyContext;

    @Operation(summary = "获取置顶和推荐文章")
    @GetMapping("/articles/topAndFeatured")
    public ResultVO<TopAndFeaturedArticlesDTO> listTopAndFeaturedArticles() {
        return ResultVO.ok(articleService.listTopAndFeaturedArticles());
    }

    @Operation(summary = "获取所有文章")
    @GetMapping("/articles/all")
    public ResultVO<PageResultDTO<ArticleCardDTO>> listArticles() {
        return ResultVO.ok(articleService.listArticles());
    }

    @Operation(summary = "根据分类id获取文章")
    @GetMapping("/articles/categoryId")
    public ResultVO<PageResultDTO<ArticleCardDTO>> getArticlesByCategoryId(@RequestParam Integer categoryId) {
        return ResultVO.ok(articleService.listArticlesByCategoryId(categoryId));
    }

    @Operation(summary = "根据id获取文章")
    @GetMapping("/articles/{articleId}")
    public ResultVO<ArticleDTO> getArticleById(@PathVariable("articleId") Integer articleId) {
        return ResultVO.ok(articleService.getArticleById(articleId));
    }

    @Operation(summary = "校验文章访问密码")
    @PostMapping("/articles/access")
    public ResultVO<?> accessArticle(@Valid @RequestBody ArticlePasswordVO articlePasswordVO) {
        articleService.accessArticle(articlePasswordVO);
        return ResultVO.ok();
    }

    @Operation(summary = "根据标签id获取文章")
    @GetMapping("/articles/tagId")
    public ResultVO<PageResultDTO<ArticleCardDTO>> listArticlesByTagId(@RequestParam Integer tagId) {
        return ResultVO.ok(articleService.listArticlesByTagId(tagId));
    }

    @Operation(summary = "获取所有文章归档")
    @GetMapping("/archives/all")
    public ResultVO<PageResultDTO<ArchiveDTO>> listArchives() {
        return ResultVO.ok(articleService.listArchives());
    }

    @Operation(summary = "获取后台文章")
    @GetMapping("/admin/articles")
    public ResultVO<PageResultDTO<ArticleAdminDTO>> listArticlesAdmin(ConditionVO conditionVO) {
        return ResultVO.ok(articleService.listArticlesAdmin(conditionVO));
    }

    @OptLog(optType = SAVE_OR_UPDATE)
    @Operation(summary = "保存和修改文章")
    @PostMapping("/admin/articles")
    public ResultVO<?> saveOrUpdateArticle(@Valid @RequestBody ArticleVO articleVO) {
        articleService.saveOrUpdateArticle(articleVO);
        return ResultVO.ok();
    }

    @OptLog(optType = UPDATE)
    @Operation(summary = "修改文章是否置顶和推荐")
    @PutMapping("/admin/articles/topAndFeatured")
    public ResultVO<?> updateArticleTopAndFeatured(@Valid @RequestBody ArticleTopFeaturedVO articleTopFeaturedVO) {
        articleService.updateArticleTopAndFeatured(articleTopFeaturedVO);
        return ResultVO.ok();
    }

    @Operation(summary = "删除或者恢复文章")
    @PutMapping("/admin/articles")
    public ResultVO<?> updateArticleDelete(@Valid @RequestBody DeleteVO deleteVO) {
        articleService.updateArticleDelete(deleteVO);
        return ResultVO.ok();
    }

    @OptLog(optType = DELETE)
    @Operation(summary = "物理删除文章")
    @DeleteMapping("/admin/articles/delete")
    public ResultVO<?> deleteArticles(@RequestBody List<Integer> articleIds) {
        articleService.deleteArticles(articleIds);
        return ResultVO.ok();
    }

    @OptLog(optType = UPLOAD)
    @Operation(summary = "上传文章图片")
    @PostMapping("/admin/articles/images")
    public ResultVO<String> saveArticleImages(
            @Parameter(description = "文章图片", required = true)
            MultipartFile file) {
        return ResultVO.ok(uploadStrategyContext.executeUploadStrategy(file, FilePathEnum.ARTICLE.getPath()));
    }

    @Operation(summary = "根据id查看后台文章")
    @GetMapping("/admin/articles/{articleId}")
    public ResultVO<ArticleAdminViewDTO> getArticleBackById(
            @Parameter(description = "文章id", required = true)
            @PathVariable("articleId") Integer articleId) {
        return ResultVO.ok(articleService.getArticleByIdAdmin(articleId));
    }

    @OptLog(optType = UPLOAD)
    @Operation(summary = "导入文章")
    @PostMapping("/admin/articles/import")
    public ResultVO<?> importArticles(MultipartFile file, @RequestParam(required = false) String type) {
        articleImportStrategyContext.importArticles(file, type);
        return ResultVO.ok();
    }

    @OptLog(optType = EXPORT)
    @Operation(summary = "导出文章")
    @PostMapping("/admin/articles/export")
    public ResultVO<List<String>> exportArticles(
            @RequestBody
            @Parameter(description = "文章id", required = true)
            List<Integer> articleIds) {
        return ResultVO.ok(articleService.exportArticles(articleIds));
    }

    @Operation(summary = "搜索文章")
    @GetMapping("/articles/search")
    public ResultVO<List<ArticleSearchDTO>> listArticlesBySearch(ConditionVO condition) {
        return ResultVO.ok(articleService.listArticlesBySearch(condition));
    }

}
