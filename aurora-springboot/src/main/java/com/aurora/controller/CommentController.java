package com.aurora.controller;

import com.aurora.annotation.AccessLimit;
import com.aurora.annotation.OptLog;
import com.aurora.model.dto.CommentAdminDTO;
import com.aurora.model.dto.CommentDTO;
import com.aurora.model.dto.PageResultDTO;
import com.aurora.model.dto.ReplyDTO;
import com.aurora.model.vo.CommentVO;
import com.aurora.model.vo.ConditionVO;
import com.aurora.model.vo.ResultVO;
import com.aurora.model.vo.ReviewVO;
import com.aurora.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import java.util.List;

import static com.aurora.constant.OptTypeConstant.DELETE;
import static com.aurora.constant.OptTypeConstant.SAVE;
import static com.aurora.constant.OptTypeConstant.UPDATE;

@Tag(name = "评论模块")
@RestController
public class CommentController {

    /** 评论接口限流时间窗口（秒） */
    private static final int COMMENT_LIMIT_SECONDS = 60;
    /** 评论接口限流最大请求次数 */
    private static final int COMMENT_LIMIT_MAX_COUNT = 3;

    @Autowired
    private CommentService commentService;

    @AccessLimit(seconds = COMMENT_LIMIT_SECONDS, maxCount = COMMENT_LIMIT_MAX_COUNT)
    @OptLog(optType = SAVE)
    @Operation(summary = "添加评论")
    @PostMapping("/comments/save")
    public ResultVO<?> saveComment(@Valid @RequestBody CommentVO commentVO) {
        commentService.saveComment(commentVO);
        return ResultVO.ok();
    }

    @Operation(summary = "获取评论")
    @GetMapping("/comments")
    public ResultVO<PageResultDTO<CommentDTO>> getComments(CommentVO commentVO) {
        return ResultVO.ok(commentService.listComments(commentVO));
    }

    @Operation(summary = "根据commentId获取回复")
    @GetMapping("/comments/{commentId}/replies")
    public ResultVO<List<ReplyDTO>> listRepliesByCommentId(@PathVariable("commentId") Integer commentId) {
        return ResultVO.ok(commentService.listRepliesByCommentId(commentId));
    }

    @Operation(summary = "获取前六个评论")
    @GetMapping("/comments/topSix")
    public ResultVO<List<CommentDTO>> listTopSixComments() {
        return ResultVO.ok(commentService.listTopSixComments());
    }

    @Operation(summary = "查询后台评论")
    @GetMapping("/admin/comments")
    public ResultVO<PageResultDTO<CommentAdminDTO>> listCommentBackDTO(ConditionVO conditionVO) {
        return ResultVO.ok(commentService.listCommentsAdmin(conditionVO));
    }

    @OptLog(optType = UPDATE)
    @Operation(summary = "审核评论")
    @PutMapping("/admin/comments/review")
    public ResultVO<?> updateCommentsReview(@Valid @RequestBody ReviewVO reviewVO) {
        commentService.updateCommentsReview(reviewVO);
        return ResultVO.ok();
    }

    @OptLog(optType = DELETE)
    @Operation(summary = "删除评论")
    @DeleteMapping("/admin/comments")
    public ResultVO<?> deleteComments(@RequestBody List<Integer> commentIdList) {
        commentService.removeByIds(commentIdList);
        return ResultVO.ok();
    }

}
