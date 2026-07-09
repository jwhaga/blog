package com.aurora.constant;

/**
 * Quartz 调度任务相关常量。
 * Misfire 策略值与 Quartz 的 Trigger.MISFIRE_INSTRUCTION_* 常量对应。
 */
public interface ScheduleConstant {

    /**
     * 默认 Misfire 策略：由调度器决定
     */
    int MISFIRE_DEFAULT = 0;

    /**
     * 忽略所有错过的触发
     */
    int MISFIRE_IGNORE_MISFIRES = 1;

    /**
     * 立即触发一次，然后按正常调度继续
     */
    int MISFIRE_FIRE_AND_PROCEED = 2;

    /**
     * 不做任何事，等待下次正常触发
     */
    int MISFIRE_DO_NOTHING = 3;

    /**
     * JobDataMap 中存储任务类名的 key
     */
    String TASK_CLASS_NAME = "TASK_CLASS_NAME";

    /**
     * JobDataMap 中存储任务参数的 key
     */
    String TASK_PROPERTIES = "TASK_PROPERTIES";

}
