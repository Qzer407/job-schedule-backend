package com.qzer.scheduler.modules.task.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SubTaskCreateRequest {

    private Long parentTaskId;

    @NotBlank(message = "子任务名称不能为空")
    private String subTaskName;

    @NotBlank(message = "执行器不能为空")
    private String executorHandler;

    private String executorParam;

    private Integer retryCount = 3;

    private Integer orderIndex = 0;

    private Integer status = 1;

    private String description;
}
