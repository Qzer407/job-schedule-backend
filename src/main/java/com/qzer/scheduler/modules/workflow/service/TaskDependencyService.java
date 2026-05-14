package com.qzer.scheduler.modules.workflow.service;

import com.qzer.scheduler.modules.workflow.entity.TaskDependency;

import java.util.List;

public interface TaskDependencyService {

    TaskDependency addDependency(Long workflowId, Long taskId, Long parentTaskId, String dependencyType);

    void removeDependency(Long id);

    List<TaskDependency> getDependenciesByWorkflowId(Long workflowId);

    void batchAddDependencies(Long workflowId, List<TaskDependency> dependencies);
}
