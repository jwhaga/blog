package com.aurora.mapper;

import com.aurora.model.dto.CommentAdminDTO;
import com.aurora.model.dto.CommentCountDTO;
import com.aurora.model.dto.CommentDTO;
import com.aurora.model.dto.ReplyDTO;
import com.aurora.entity.Comment;
import com.aurora.model.vo.CommentVO;
import com.aurora.model.vo.ConditionVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentMapper extends BaseMapper<Comment> {

    /**
     * 分页查询评论列表
     */
    List<CommentDTO> listComments(@Param("current") Long current, @Param("size") Long size, @Param("commentVO") CommentVO commentVO);

    /**
     * 根据评论id列表查询回复列表
     */
    List<ReplyDTO> listReplies(@Param("commentIds") List<Integer> commentIdList);

    /**
     * 查询点赞量前六的评论
     */
    List<CommentDTO> listTopSixComments();

    /**
     * 后台统计符合条件的评论数量
     */
    Long countComments(@Param("conditionVO") ConditionVO conditionVO);

    /**
     * 后台分页查询评论列表
     */
    List<CommentAdminDTO> listCommentsAdmin(@Param("current") Long current, @Param("size") Long size, @Param("conditionVO") ConditionVO conditionVO);

    /**
     * 根据类型和话题id列表批量统计评论数量
     */
    List<CommentCountDTO> listCommentCountByTypeAndTopicIds(@Param("type") Integer type, @Param("topicIds") List<Integer> topicIds);

    /**
     * 根据类型和话题id统计评论数量
     */
    CommentCountDTO listCommentCountByTypeAndTopicId(@Param("type") Integer type, @Param("topicId") Integer topicId);

}
