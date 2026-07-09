package com.aurora.mapper;

import com.aurora.model.dto.JobDTO;
import com.aurora.entity.Job;
import com.aurora.model.vo.JobSearchVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobMapper extends BaseMapper<Job> {

    /**
     * 统计符合条件的定时任务数量
     */
    Long countJobs(@Param("jobSearchVO") JobSearchVO jobSearchVO);

    /**
     * 分页查询定时任务列表
     */
    List<JobDTO> listJobs(@Param("current") Long current, @Param("size") Long size, @Param("jobSearchVO")JobSearchVO jobSearchVO);

    /**
     * 查询所有任务分组
     */
    List<String> listJobGroups();

}
