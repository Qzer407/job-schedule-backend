package com.qzer.scheduler.modules.task.controller;

import com.qzer.scheduler.common.dto.response.ApiResponse;
import com.qzer.scheduler.modules.task.dto.SubTaskCreateRequest;
import com.qzer.scheduler.modules.task.dto.SubTaskUpdateRequest;
import com.qzer.scheduler.modules.task.entity.SubTask;
import com.qzer.scheduler.modules.task.service.SubTaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/subtasks")
@Tag(name = "子任务管理", description = "子任务管理接口")
@RequiredArgsConstructor
public class SubTaskController {

    private final SubTaskService subTaskService;

    @GetMapping("/parent/{parentTaskId}")
    @Operation(summary = "查询子任务列表")
    public ResponseEntity<ApiResponse<List<SubTask>>> getSubTasksByParentId(
            @Parameter(description = "父任务ID") @PathVariable Long parentTaskId) {
        List<SubTask> subTasks = subTaskService.getSubTasksByParentId(parentTaskId);
        return ResponseEntity.ok(ApiResponse.success(subTasks));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询子任务详情")
    public ResponseEntity<ApiResponse<SubTask>> getSubTaskById(
            @Parameter(description = "子任务ID") @PathVariable Long id) {
        SubTask subTask = subTaskService.getSubTaskById(id);
        return ResponseEntity.ok(ApiResponse.success(subTask));
    }

    @PostMapping("/parent/{parentTaskId}")
    @Operation(summary = "创建子任务")
    public ResponseEntity<ApiResponse<SubTask>> createSubTask(
            @Parameter(description = "父任务ID") @PathVariable Long parentTaskId,
            @Valid @RequestBody SubTaskCreateRequest request) {
        SubTask subTask = subTaskService.createSubTask(parentTaskId, request);
        return ResponseEntity.ok(ApiResponse.success(subTask));
    }

    @PostMapping("/parent/{parentTaskId}/batch")
    @Operation(summary = "批量创建子任务")
    public ResponseEntity<ApiResponse<List<SubTask>>> batchCreateSubTasks(
            @Parameter(description = "父任务ID") @PathVariable Long parentTaskId,
            @RequestBody List<@Valid SubTaskCreateRequest> requests) {
        List<SubTask> subTasks = subTaskService.batchCreate(parentTaskId, requests);
        return ResponseEntity.ok(ApiResponse.success(subTasks));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新子任务")
    public ResponseEntity<ApiResponse<SubTask>> updateSubTask(
            @Parameter(description = "子任务ID") @PathVariable Long id,
            @RequestBody SubTaskUpdateRequest request) {
        SubTask subTask = subTaskService.updateSubTask(id, request);
        return ResponseEntity.ok(ApiResponse.success(subTask));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除子任务")
    public ResponseEntity<ApiResponse<Void>> deleteSubTask(
            @Parameter(description = "子任务ID") @PathVariable Long id) {
        subTaskService.deleteSubTask(id);
        return ResponseEntity.ok(ApiResponse.success());
    }

    @DeleteMapping("/parent/{parentTaskId}")
    @Operation(summary = "删除父任务下所有子任务")
    public ResponseEntity<ApiResponse<Void>> deleteByParentTaskId(
            @Parameter(description = "父任务ID") @PathVariable Long parentTaskId) {
        subTaskService.deleteByParentTaskId(parentTaskId);
        return ResponseEntity.ok(ApiResponse.success());
    }

    @PostMapping("/parent/{parentTaskId}/execute")
    @Operation(summary = "执行父任务下所有子任务")
    public ResponseEntity<ApiResponse<Void>> executeSubTasks(
            @Parameter(description = "父任务ID") @PathVariable Long parentTaskId) {
        subTaskService.executeSubTasks(parentTaskId);
        return ResponseEntity.ok(ApiResponse.success());
    }
}
