package com.aurora.controller;

import com.aurora.annotation.OptLog;
import com.aurora.enums.FilePathEnum;
import com.aurora.model.dto.PageResultDTO;
import com.aurora.model.dto.TalkAdminDTO;
import com.aurora.model.dto.TalkDTO;
import com.aurora.model.vo.ConditionVO;
import com.aurora.model.vo.ResultVO;
import com.aurora.model.vo.TalkVO;
import com.aurora.service.TalkService;
import com.aurora.strategy.context.UploadStrategyContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import java.util.List;

import static com.aurora.constant.OptTypeConstant.DELETE;
import static com.aurora.constant.OptTypeConstant.SAVE_OR_UPDATE;
import static com.aurora.constant.OptTypeConstant.UPLOAD;

@Tag(name = "说说模块")
@RestController
public class TalkController {

    @Autowired
    private TalkService talkService;

    @Autowired
    private UploadStrategyContext uploadStrategyContext;

    @Operation(summary = "查看说说列表")
    @GetMapping("/talks")
    public ResultVO<PageResultDTO<TalkDTO>> listTalks() {
        return ResultVO.ok(talkService.listTalks());
    }

    @Operation(summary = "根据id查看说说")
    @GetMapping("/talks/{talkId}")
    public ResultVO<TalkDTO> getTalkById(
            @Parameter(description = "说说id", required = true)
            @PathVariable("talkId") Integer talkId) {
        return ResultVO.ok(talkService.getTalkById(talkId));
    }

    @OptLog(optType = UPLOAD)
    @Operation(summary = "上传说说图片")
    @PostMapping("/admin/talks/images")
    public ResultVO<String> saveTalkImages(
            @Parameter(description = "说说图片", required = true)
            MultipartFile file) {
        return ResultVO.ok(uploadStrategyContext.executeUploadStrategy(file, FilePathEnum.TALK.getPath()));
    }

    @OptLog(optType = SAVE_OR_UPDATE)
    @Operation(summary = "保存或修改说说")
    @PostMapping("/admin/talks")
    public ResultVO<?> saveOrUpdateTalk(@Valid @RequestBody TalkVO talkVO) {
        talkService.saveOrUpdateTalk(talkVO);
        return ResultVO.ok();
    }

    @OptLog(optType = DELETE)
    @Operation(summary = "删除说说")
    @DeleteMapping("/admin/talks")
    public ResultVO<?> deleteTalks(@RequestBody List<Integer> talkIds) {
        talkService.deleteTalks(talkIds);
        return ResultVO.ok();
    }

    @Operation(summary = "查看后台说说")
    @GetMapping("/admin/talks")
    public ResultVO<PageResultDTO<TalkAdminDTO>> listBackTalks(ConditionVO conditionVO) {
        return ResultVO.ok(talkService.listBackTalks(conditionVO));
    }

    @Operation(summary = "根据id查看后台说说")
    @GetMapping("/admin/talks/{talkId}")
    public ResultVO<TalkAdminDTO> getBackTalkById(
            @Parameter(description = "说说id", required = true)
            @PathVariable("talkId") Integer talkId) {
        return ResultVO.ok(talkService.getBackTalkById(talkId));
    }

}
