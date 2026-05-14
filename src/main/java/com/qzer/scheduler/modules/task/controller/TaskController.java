package com.qzer.scheduler.modules.task.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qzer.scheduler.common.dto.response.ApiResponse;
import com.qzer.scheduler.modules.task.dto.TaskCreateRequest;
import com.qzer.scheduler.modules.task.dto.TaskUpdateRequest;
import com.qzer.scheduler.modules.task.entity.TaskInfo;
import com.qzer.scheduler.modules.task.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public ApiResponse<TaskInfo> createTask(@Valid @RequestBody TaskCreateRequest request) {
        TaskInfo task = taskService.createTask(request);
        return ApiResponse.success(task);
    }

    @GetMapping
    public ApiResponse<IPage<TaskInfo>> listTasks(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestParam(required = false) String taskName,
            @RequestParam(required = false) String taskGroup,
            @RequestParam(required = false) Integer status) {
        Page<TaskInfo> page = new Page<>(pageNum, pageSize);
        IPage<TaskInfo> result = taskService.listTasks(page, taskName, taskGroup, status);
        return ApiResponse.success(result);
    }

    @GetMapping("/{id}")
    public ApiResponse<TaskInfo> getTaskById(@PathVariable Long id) {
        TaskInfo task = taskService.getTaskById(id);
        if (task == null) {
            return ApiResponse.error(404, "任务不存在");
        }
        return ApiResponse.success(task);
    }

    @PutMapping("/{id}")
    public ApiResponse<TaskInfo> updateTask(@PathVariable Long id, @RequestBody TaskUpdateRequest request) {
        TaskInfo task = taskService.updateTask(id, request);
        return ApiResponse.success(task);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ApiResponse.success(null);
    }

    @PostMapping("/{id}/start")
    public ApiResponse<Void> startTask(@PathVariable Long id) {
        taskService.startTask(id);
        return ApiResponse.success(null);
    }

    @PostMapping("/{id}/stop")
    public ApiResponse<Void> stopTask(@PathVariable Long id) {
        taskService.stopTask(id);
        return ApiResponse.success(null);
    }

    @PostMapping("/{id}/trigger")
    public ApiResponse<Void> triggerTask(@PathVariable Long id, @RequestBody(required = false) Map<String, String> body) {
        String executorParam = body != null ? body.get("executorParam") : null;
        taskService.triggerTask(id, executorParam);
        return ApiResponse.success(null);
    }
}
