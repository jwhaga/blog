package com.aurora.mapper;

import com.aurora.entity.Menu;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuMapper extends BaseMapper<Menu> {

    /**
     * 根据用户信息id查询菜单列表
     */
    List<Menu> listMenusByUserInfoId(Integer userInfoId);

}
