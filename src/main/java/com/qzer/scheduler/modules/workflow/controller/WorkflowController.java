package com.qzer.scheduler.modules.workflow.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qzer.scheduler.common.dto.response.ApiResponse;
import com.qzer.scheduler.modules.workflow.entity.TaskDependency;
import com.qzer.scheduler.modules.workflow.entity.Workflow;
import com.qzer.scheduler.modules.workflow.service.TaskDependencyService;
import com.qzer.scheduler.modules.workflow.service.WorkflowService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/workflows")
@RequiredArgsConstructor
public class WorkflowController {

    private final WorkflowService workflowService;
    private final TaskDependencyService taskDependencyService;

    @PostMapping
    public ApiResponse<Workflow> createWorkflow(@RequestBody Workflow workflow) {
        Workflow result = workflowService.createWorkflow(workflow);
        return ApiResponse.success(result);
    }

    @PutMapping("/{id}")
    public ApiResponse<Workflow> updateWorkflow(@PathVariable Long id, @RequestBody Workflow workflow) {
        Workflow result = workflowService.updateWorkflow(id, workflow);
        return ApiResponse.success(result);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteWorkflow(@PathVariable Long id) {
        workflowService.deleteWorkflow(id);
        return ApiResponse.success(null);
    }

    @GetMapping("/{id}")
    public ApiResponse<Workflow> getWorkflowById(@PathVariable Long id) {
        Workflow workflow = workflowService.getWorkflowById(id);
        return ApiResponse.success(workflow);
    }

    @GetMapping
    public ApiResponse<IPage<Workflow>> listWorkflows(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        Page<Workflow> page = new Page<>(pageNum, pageSize);
        IPage<Workflow> result = workflowService.listWorkflows(page, keyword, status);
        return ApiResponse.success(result);
    }

    @PostMapping("/{id}/execute")
    public ApiResponse<Void> executeWorkflow(@PathVariable Long id) {
        workflowService.executeWorkflow(id);
        return ApiResponse.success(null);
    }

    @PostMapping("/{id}/pause")
    public ApiResponse<Void> pauseWorkflow(@PathVariable Long id) {
        workflowService.pauseWorkflow(id);
        return ApiResponse.success(null);
    }

    @PostMapping("/{id}/terminate")
    public ApiResponse<Void> terminateWorkflow(@PathVariable Long id) {
        workflowService.terminateWorkflow(id);
        return ApiResponse.success(null);
    }

    @GetMapping("/{id}/dependencies")
    public ApiResponse<List<TaskDependency>> getWorkflowDependencies(@PathVariable Long id) {
        List<TaskDependency> dependencies = taskDependencyService.getDependenciesByWorkflowId(id);
        return ApiResponse.success(dependencies);
    }

    @PostMapping("/{id}/dependencies")
    public ApiResponse<TaskDependency> addTaskDependency(
            @PathVariable Long id,
            @RequestParam Long taskId,
            @RequestParam(required = false) Long parentTaskId,
            @RequestParam(defaultValue = "SUCCESS") String dependencyType) {
        TaskDependency result = taskDependencyService.addDependency(id, taskId, parentTaskId, dependencyType);
        return ApiResponse.success(result);
    }

    @GetMapping("/templates")
    public ApiResponse<List<Workflow>> getWorkflowTemplates() {
        List<Workflow> templates = workflowService.getWorkflowTemplates();
        return ApiResponse.success(templates);
    }
}
