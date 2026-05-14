package com.qzer.scheduler.modules.workflow.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qzer.scheduler.modules.workflow.dto.WorkflowTemplateCreateRequest;
import com.qzer.scheduler.modules.workflow.dto.WorkflowTemplateUpdateRequest;
import com.qzer.scheduler.modules.workflow.entity.Workflow;
import com.qzer.scheduler.modules.workflow.entity.WorkflowTemplate;

import java.util.List;

public interface WorkflowTemplateService {

    WorkflowTemplate createTemplate(WorkflowTemplateCreateRequest request);

    WorkflowTemplate getTemplateById(Long id);

    List<WorkflowTemplate> getTemplatesByType(String templateType);

    Page<WorkflowTemplate> getTemplatePage(Integer pageNum, Integer pageSize, String templateType);

    WorkflowTemplate updateTemplate(Long id, WorkflowTemplateUpdateRequest request);

    void deleteTemplate(Long id);

    Workflow createWorkflowFromTemplate(Long templateId);

    List<WorkflowTemplate> getAllTemplates();
}
