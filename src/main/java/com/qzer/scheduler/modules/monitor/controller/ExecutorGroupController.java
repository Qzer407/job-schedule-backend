
package com.qzer.scheduler.modules.monitor.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qzer.scheduler.common.dto.response.ApiResponse;
import com.qzer.scheduler.modules.monitor.dto.ExecutorGroupCreateRequest;
import com.qzer.scheduler.modules.monitor.dto.ExecutorGroupUpdateRequest;
import com.qzer.scheduler.modules.monitor.entity.ExecutorGroup;
import com.qzer.scheduler.modules.monitor.service.ExecutorGroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/executor-group")
@RequiredArgsConstructor
public class ExecutorGroupController {

    private final ExecutorGroupService executorGroupService;

    @PostMapping
    public ApiResponse&lt;ExecutorGroup&gt; createGroup(@Valid @RequestBody ExecutorGroupCreateRequest request) {
        return ApiResponse.success(executorGroupService.createGroup(request));
    }

    @PutMapping("/{id}")
    public ApiResponse&lt;ExecutorGroup&gt; updateGroup(@PathVariable Long id, @Valid @RequestBody ExecutorGroupUpdateRequest request) {
        return ApiResponse.success(executorGroupService.updateGroup(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse&lt;Void&gt; deleteGroup(@PathVariable Long id) {
        executorGroupService.deleteGroup(id);
        return ApiResponse.success(null);
    }

    @GetMapping("/{id}")
    public ApiResponse&lt;ExecutorGroup&gt; getGroup(@PathVariable Long id) {
        return ApiResponse.success(executorGroupService.getById(id));
    }

    @GetMapping
    public ApiResponse&lt;IPage&lt;ExecutorGroup&gt;&gt; listGroups(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String groupName,
            @RequestParam(required = false) Integer status) {
        Page&lt;ExecutorGroup&gt; page = new Page&lt;&gt;(current, size);
        return ApiResponse.success(executorGroupService.listGroups(page, groupName, status));
    }
}

