package com.aurora.controller;

import com.aurora.annotation.OptLog;
import com.aurora.model.dto.FriendLinkAdminDTO;
import com.aurora.model.dto.FriendLinkDTO;
import com.aurora.model.dto.PageResultDTO;
import com.aurora.model.vo.ConditionVO;
import com.aurora.model.vo.FriendLinkVO;
import com.aurora.model.vo.ResultVO;
import com.aurora.service.FriendLinkService;
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

@Tag(name = "友链模块")
@RestController
public class FriendLinkController {

    @Autowired
    private FriendLinkService friendLinkService;

    @Operation(summary = "查看友链列表")
    @GetMapping("/links")
    public ResultVO<List<FriendLinkDTO>> listFriendLinks() {
        return ResultVO.ok(friendLinkService.listFriendLinks());
    }

    @Operation(summary = "查看后台友链列表")
    @GetMapping("/admin/links")
    public ResultVO<PageResultDTO<FriendLinkAdminDTO>> listFriendLinkDTO(ConditionVO conditionVO) {
        return ResultVO.ok(friendLinkService.listFriendLinksAdmin(conditionVO));
    }

    @OptLog(optType = SAVE_OR_UPDATE)
    @Operation(summary = "保存或修改友链")
    @PostMapping("/admin/links")
    public ResultVO<?> saveOrUpdateFriendLink(@Valid @RequestBody FriendLinkVO friendLinkVO) {
        friendLinkService.saveOrUpdateFriendLink(friendLinkVO);
        return ResultVO.ok();
    }

    @OptLog(optType = DELETE)
    @Operation(summary = "删除友链")
    @DeleteMapping("/admin/links")
    public ResultVO<?> deleteFriendLink(@RequestBody List<Integer> linkIdList) {
        friendLinkService.removeByIds(linkIdList);
        return ResultVO.ok();
    }
}
