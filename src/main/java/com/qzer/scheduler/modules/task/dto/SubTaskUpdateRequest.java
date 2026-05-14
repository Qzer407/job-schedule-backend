package com.qzer.scheduler.modules.task.dto;

import lombok.Data;

@Data
public class SubTaskUpdateRequest {

    private String subTaskName;

    private String executorHandler;

    private String executorParam;

    private Integer retryCount;

    private Integer orderIndex;

    private Integer status;

    private String description;
}
