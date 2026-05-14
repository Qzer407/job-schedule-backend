package com.qzer.scheduler.modules.workflow.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qzer.scheduler.modules.workflow.entity.Workflow;

import java.util.List;

public interface WorkflowService {

    Workflow createWorkflow(Workflow workflow);

    Workflow updateWorkflow(Long id, Workflow workflow);

    void deleteWorkflow(Long id);

    Workflow getWorkflowById(Long id);

    IPage<Workflow> listWorkflows(Page<Workflow> page, String keyword, Integer status);

    void executeWorkflow(Long id);

    void pauseWorkflow(Long id);

    void terminateWorkflow(Long id);

    List<Workflow> getWorkflowTemplates();
}
