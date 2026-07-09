package com.aurora.mapper;

import com.aurora.model.dto.TagAdminDTO;
import com.aurora.model.dto.TagDTO;
import com.aurora.entity.Tag;
import com.aurora.model.vo.ConditionVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TagMapper extends BaseMapper<Tag> {

    /**
     * 查询标签列表
     */
    List<TagDTO> listTags();

    /**
     * 查询文章量前十的标签
     */
    List<TagDTO> listTopTenTags();

    /**
     * 根据文章id查询标签名称列表
     */
    List<String> listTagNamesByArticleId(Integer articleId);

    /**
     * 后台分页查询标签列表
     */
    List<TagAdminDTO> listTagsAdmin(@Param("current") Long current, @Param("size") Long size, @Param("conditionVO") ConditionVO conditionVO);

}
