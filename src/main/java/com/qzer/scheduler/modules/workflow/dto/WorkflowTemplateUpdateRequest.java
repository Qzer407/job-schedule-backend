package com.qzer.scheduler.modules.workflow.dto;

import lombok.Data;

@Data
public class WorkflowTemplateUpdateRequest {

    private String templateName;

    private String templateDesc;

    private String templateType;

    private String templateConfig;

    private Integer status;
}
