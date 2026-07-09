package com.aurora.mapper;

import com.aurora.model.dto.TalkAdminDTO;
import com.aurora.model.dto.TalkDTO;
import com.aurora.entity.Talk;
import com.aurora.model.vo.ConditionVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TalkMapper extends BaseMapper<Talk> {

    /**
     * 分页查询说说列表
     */
    List<TalkDTO> listTalks(@Param("current") Long current, @Param("size") Long size);

    /**
     * 根据说说id查询说说详情
     */
    TalkDTO getTalkById(@Param("talkId") Integer talkId);

    /**
     * 后台分页查询说说列表
     */
    List<TalkAdminDTO> listTalksAdmin(@Param("current") Long current, @Param("size") Long size, @Param("conditionVO") ConditionVO conditionVO);

    /**
     * 后台根据说说id查询说说详情
     */
    TalkAdminDTO getTalkByIdAdmin(@Param("talkId") Integer talkId);

}
