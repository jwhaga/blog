package com.aurora.mapper;

import com.aurora.entity.JobLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobLogMapper extends BaseMapper<JobLog> {

    /**
     * 查询所有任务日志分组
     */
    List<String> listJobLogGroups();

}
