package com.qzer.scheduler.modules.workflow.service;

public interface WorkflowExecutorService {

    void executeWorkflow(Long workflowId);

    void pauseWorkflow(Long workflowId);

    void resumeWorkflow(Long workflowId);

    void terminateWorkflow(Long workflowId);

    Integer getWorkflowStatus(Long workflowId);
}
