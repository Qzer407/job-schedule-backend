package com.qzer.scheduler.modules.task.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TaskCreateRequest {

    @NotBlank(message = "任务名称不能为空")
    private String taskName;

    @NotBlank(message = "任务分组不能为空")
    private String taskGroup;

    @NotBlank(message = "Cron表达式不能为空")
    private String cronExpression;

    @NotBlank(message = "执行器处理器不能为空")
    private String executorHandler;

    private String executorParam;

    private Integer shardingTotal = 1;

    private String shardingParam;

    private Integer retryCount = 3;

    private Integer alarmStatus = 1;

    private String description;
}
