package com.aurora.service.impl;

import com.aurora.enums.JobStatusEnum;
import com.aurora.exception.BizException;
import com.aurora.exception.TaskException;
import com.aurora.model.dto.JobDTO;
import com.aurora.entity.Job;
import com.aurora.mapper.JobMapper;
import com.aurora.model.dto.PageResultDTO;
import com.aurora.service.JobService;
import com.aurora.util.BeanCopyUtil;
import com.aurora.util.CronUtil;
import com.aurora.util.PageUtil;
import com.aurora.util.ScheduleUtil;
import com.aurora.model.vo.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import jakarta.annotation.PostConstruct;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class JobServiceImpl extends ServiceImpl<JobMapper, Job> implements JobService {

    @Autowired
    private Scheduler scheduler;

    @Autowired
    private JobMapper jobMapper;

    @PostConstruct
    public void init() {
        try {
            scheduler.clear();
            List<Job> jobs = jobMapper.selectList(null);
            for (Job job : jobs) {
                ScheduleUtil.createScheduleJob(scheduler, job);
            }
        } catch (SchedulerException | TaskException e) {
            log.error("定时任务初始化失败", e);
            throw new BizException("定时任务初始化失败");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveJob(JobVO jobVO) {
        checkCronIsValid(jobVO);
        Job job = BeanCopyUtil.copyObject(jobVO, Job.class);
        int row = jobMapper.insert(job);
        if (row > 0) {
            try {
                ScheduleUtil.createScheduleJob(scheduler, job);
            } catch (SchedulerException | TaskException e) {
                log.error("创建定时任务失败", e);
                throw new BizException("创建定时任务失败");
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateJob(JobVO jobVO) {
        checkCronIsValid(jobVO);
        Job temp = jobMapper.selectById(jobVO.getId());
        Job job = BeanCopyUtil.copyObject(jobVO, Job.class);
        int row = jobMapper.updateById(job);
        if (row > 0) updateSchedulerJob(job, temp.getJobGroup());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteJobs(List<Integer> tagIds) {
        List<Job> jobs = jobMapper.selectList(new LambdaQueryWrapper<Job>().in(Job::getId, tagIds));
        int row = jobMapper.delete(new LambdaQueryWrapper<Job>().in(Job::getId, tagIds));
        if (row > 0) {
            jobs.forEach(item -> {
                try {
                    scheduler.deleteJob(ScheduleUtil.getJobKey(item.getId(), item.getJobGroup()));
                } catch (SchedulerException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    @Override
    public JobDTO getJobById(Integer jobId) {
        Job job = jobMapper.selectById(jobId);
        JobDTO jobDTO = BeanCopyUtil.copyObject(job, JobDTO.class);
        Date nextExecution = CronUtil.getNextExecution(jobDTO.getCronExpression());
        jobDTO.setNextValidTime(nextExecution);
        return jobDTO;
    }

    @Override
    public PageResultDTO<JobDTO> listJobs(JobSearchVO jobSearchVO) {
        CompletableFuture<Long> asyncCount = CompletableFuture.supplyAsync(() -> jobMapper.countJobs(jobSearchVO));
        List<JobDTO> jobDTOs = jobMapper.listJobs(PageUtil.getLimitCurrent(), PageUtil.getSize(), jobSearchVO);
        return new PageResultDTO<>(jobDTOs, asyncCount.join());
    }

    @Override
    public void updateJobStatus(JobStatusVO jobStatusVO) {
        Job job = jobMapper.selectById(jobStatusVO.getId());
        if (job.getStatus().equals(jobStatusVO.getStatus())) {
            return;
        }
        Integer status = jobStatusVO.getStatus();
        Integer jobId = job.getId();
        String jobGroup = job.getJobGroup();
        LambdaUpdateWrapper<Job> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Job::getId, jobStatusVO.getId()).set(Job::getStatus, status);
        int row = jobMapper.update(null, updateWrapper);
        if (row > 0) {
            try {
                if (JobStatusEnum.NORMAL.getValue().equals(status)) {
                    scheduler.resumeJob(ScheduleUtil.getJobKey(jobId, jobGroup));
                } else if (JobStatusEnum.PAUSE.getValue().equals(status)) {
                    scheduler.pauseJob(ScheduleUtil.getJobKey(jobId, jobGroup));
                }
            } catch (SchedulerException e) {
                log.error("更新定时任务状态失败", e);
                throw new BizException("更新定时任务状态失败");
            }
        }
    }

    @Override
    public void runJob(JobRunVO jobRunVO) {
        Integer jobId = jobRunVO.getId();
        String jobGroup = jobRunVO.getJobGroup();
        try {
            scheduler.triggerJob(ScheduleUtil.getJobKey(jobId, jobGroup));
        } catch (SchedulerException e) {
            log.error("执行定时任务失败", e);
            throw new BizException("执行定时任务失败");
        }
    }

    @Override
    public List<String> listJobGroups() {
        return jobMapper.listJobGroups();
    }

    private void checkCronIsValid(JobVO jobVO) {
        boolean valid = CronUtil.isValid(jobVO.getCronExpression());
        Assert.isTrue(valid, "Cron表达式无效!");
    }

    public void updateSchedulerJob(Job job, String jobGroup) {
        Integer jobId = job.getId();
        JobKey jobKey = ScheduleUtil.getJobKey(jobId, jobGroup);
        try {
            if (scheduler.checkExists(jobKey)) {
                scheduler.deleteJob(jobKey);
            }
            ScheduleUtil.createScheduleJob(scheduler, job);
        } catch (SchedulerException | TaskException e) {
            log.error("更新定时任务失败", e);
            throw new BizException("更新定时任务失败");
        }
    }

}
