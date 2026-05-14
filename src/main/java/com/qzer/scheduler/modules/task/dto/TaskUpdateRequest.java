package com.qzer.scheduler.modules.task.dto;

import lombok.Data;

@Data
public class TaskUpdateRequest {

    private String taskName;

    private String taskGroup;

    private String cronExpression;

    private String executorHandler;

    private String executorParam;

    private Integer shardingTotal;

    private String shardingParam;

    private Integer retryCount;

    private Integer alarmStatus;

    private Integer status;

    private String description;
}
