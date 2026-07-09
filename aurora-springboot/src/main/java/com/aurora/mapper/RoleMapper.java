package com.aurora.mapper;

import com.aurora.model.dto.ResourceRoleDTO;
import com.aurora.model.dto.RoleDTO;
import com.aurora.entity.Role;
import com.aurora.model.vo.ConditionVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleMapper extends BaseMapper<Role> {

    /**
     * 查询资源对应的角色列表
     */
    List<ResourceRoleDTO> listResourceRoles();

    /**
     * 根据用户信息id查询角色标签列表
     */
    List<String> listRolesByUserInfoId(@Param("userInfoId") Integer userInfoId);

    /**
     * 后台分页查询角色列表
     */
    List<RoleDTO> listRoles(@Param("current") Long current, @Param("size") Long size, @Param("conditionVO") ConditionVO conditionVO);

}
