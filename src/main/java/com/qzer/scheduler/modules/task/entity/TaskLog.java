package com.qzer.scheduler.modules.task.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_task_log")
public class TaskLog {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("task_id")
    private Long taskId;

    @TableField("task_name")
    private String taskName;

    @TableField("executor_address")
    private String executorAddress;

    @TableField("sharding_index")
    private Integer shardingIndex;

    @TableField("sharding_param")
    private String shardingParam;

    @TableField("trigger_time")
    private LocalDateTime triggerTime;

    @TableField("start_time")
    private LocalDateTime startTime;

    @TableField("end_time")
    private LocalDateTime endTime;

    @TableField("duration")
    private Long duration;

    @TableField("execution_status")
    private Integer executionStatus;

    @TableField("execution_log")
    private String executionLog;

    @TableField("error_msg")
    private String errorMsg;

    @TableField("create_time")
    private LocalDateTime createTime;
}
