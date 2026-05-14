
package com.qzer.scheduler.modules.message.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
public class AsyncTaskRequest {

    private Long taskId;

    private String taskName;

    @NotBlank(message = "消息载荷不能为空")
    private String payload;

    private Integer maxRetry = 3;
}

