package com.qzer.scheduler.modules.workflow.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class WorkflowTemplateCreateRequest {

    @NotBlank(message = "模板名称不能为空")
    private String templateName;

    private String templateDesc;

    private String templateType;

    @NotBlank(message = "模板配置不能为空")
    private String templateConfig;

    private Integer status = 1;
}
