package com.qzer.scheduler.modules.task.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_task_info")
public class TaskInfo {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("task_name")
    private String taskName;

    @TableField("task_group")
    private String taskGroup;

    @TableField("cron_expression")
    private String cronExpression;

    @TableField("executor_handler")
    private String executorHandler;

    @TableField("executor_param")
    private String executorParam;

    @TableField("sharding_total")
    private Integer shardingTotal;

    @TableField("sharding_param")
    private String shardingParam;

    @TableField("retry_count")
    private Integer retryCount;

    @TableField("alarm_status")
    private Integer alarmStatus;

    @TableField("status")
    private Integer status;

    @TableField("description")
    private String description;

    @TableField("timeout")
    private Integer timeout;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;
}
