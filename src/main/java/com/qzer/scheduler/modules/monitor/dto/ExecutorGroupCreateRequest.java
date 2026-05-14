
package com.qzer.scheduler.modules.monitor.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ExecutorGroupCreateRequest {

    @NotBlank(message = "分组名称不能为空")
    private String groupName;

    @NotBlank(message = "分组编码不能为空")
    private String groupCode;

    private String description;
}

