package com.aurora.mapper;

import com.aurora.model.dto.UserAdminDTO;
import com.aurora.entity.UserAuth;
import com.aurora.model.vo.ConditionVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserAuthMapper extends BaseMapper<UserAuth> {

    /**
     * 后台分页查询用户列表
     */
    List<UserAdminDTO> listUsers(@Param("current") Long current, @Param("size") Long size, @Param("conditionVO") ConditionVO conditionVO);

    /**
     * 后台统计符合条件的用户数量
     */
    Long countUser(@Param("conditionVO") ConditionVO conditionVO);

}
