
package com.qzer.scheduler.modules.monitor.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ExecutorGroupUpdateRequest {

    private String groupName;

    private String description;

    private Integer status;
}

