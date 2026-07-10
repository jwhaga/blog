package com.aurora.controller;


import com.aurora.annotation.OptLog;
import com.aurora.model.dto.PageResultDTO;
import com.aurora.model.dto.TagAdminDTO;
import com.aurora.model.dto.TagDTO;
import com.aurora.model.vo.ConditionVO;
import com.aurora.model.vo.ResultVO;
import com.aurora.model.vo.TagVO;
import com.aurora.service.TagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import java.util.List;

import static com.aurora.constant.OptTypeConstant.DELETE;
import static com.aurora.constant.OptTypeConstant.SAVE_OR_UPDATE;

@Tag(name = "标签模块")
@RestController
public class TagController {


    @Autowired
    private TagService tagService;

    @Operation(summary = "获取所有标签")
    @GetMapping("/tags/all")
    public ResultVO<List<TagDTO>> getAllTags() {
        return ResultVO.ok(tagService.listTags());
    }

    @Operation(summary = "获取前十个标签")
    @GetMapping("/tags/topTen")
    public ResultVO<List<TagDTO>> getTopTenTags() {
        return ResultVO.ok(tagService.listTopTenTags());
    }

    @Operation(summary = "查询后台标签列表")
    @GetMapping("/admin/tags")
    public ResultVO<PageResultDTO<TagAdminDTO>> listTagsAdmin(ConditionVO conditionVO) {
        return ResultVO.ok(tagService.listTagsAdmin(conditionVO));
    }

    @Operation(summary = "搜索文章标签")
    @GetMapping("/admin/tags/search")
    public ResultVO<List<TagAdminDTO>> listTagsAdminBySearch(ConditionVO condition) {
        return ResultVO.ok(tagService.listTagsAdminBySearch(condition));
    }

    @OptLog(optType = SAVE_OR_UPDATE)
    @Operation(summary = "添加或修改标签")
    @PostMapping("/admin/tags")
    public ResultVO<?> saveOrUpdateTag(@Valid @RequestBody TagVO tagVO) {
        tagService.saveOrUpdateTag(tagVO);
        return ResultVO.ok();
    }

    @OptLog(optType = DELETE)
    @Operation(summary = "删除标签")
    @DeleteMapping("/admin/tags")
    public ResultVO<?> deleteTag(@RequestBody List<Integer> tagIdList) {
        tagService.deleteTag(tagIdList);
        return ResultVO.ok();
    }
}
