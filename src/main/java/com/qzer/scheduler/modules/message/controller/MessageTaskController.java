
package com.qzer.scheduler.modules.message.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qzer.scheduler.modules.message.dto.AsyncTaskRequest;
import com.qzer.scheduler.modules.message.dto.DelayTaskRequest;
import com.qzer.scheduler.modules.message.entity.MessageTask;
import com.qzer.scheduler.modules.message.service.MessageTaskService;
import com.qzer.scheduler.common.utils.ResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/message")
@RequiredArgsConstructor
public class MessageTaskController {

    private final MessageTaskService messageTaskService;

    @PostMapping("/task")
    public Map&lt;String, Object&gt; submitAsyncTask(@RequestBody AsyncTaskRequest request) {
        String messageId = messageTaskService.submitAsyncTask(request);
        Map&lt;String, Object&gt; result = new HashMap&lt;&gt;();
        result.put("messageId", messageId);
        return ResponseUtils.success(result);
    }

    @PostMapping("/task/delay")
    public Map&lt;String, Object&gt; submitDelayTask(@RequestBody DelayTaskRequest request) {
        String messageId = messageTaskService.submitDelayTask(request);
        Map&lt;String, Object&gt; result = new HashMap&lt;&gt;();
        result.put("messageId", messageId);
        return ResponseUtils.success(result);
    }

    @GetMapping("/task")
    public Map&lt;String, Object&gt; getTaskList(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status) {
        Page&lt;MessageTask&gt; page = messageTaskService.getTaskList(current, size, status);
        return ResponseUtils.success(page);
    }

    @GetMapping("/task/{id}")
    public Map&lt;String, Object&gt; getTaskById(@PathVariable Long id) {
        MessageTask task = messageTaskService.getTaskById(id);
        return ResponseUtils.success(task);
    }

    @DeleteMapping("/task/{id}")
    public Map&lt;String, Object&gt; cancelTask(@PathVariable Long id) {
        messageTaskService.cancelTask(id);
        return ResponseUtils.success(null);
    }

    @PostMapping("/task/{id}/retry")
    public Map&lt;String, Object&gt; retryTask(@PathVariable Long id) {
        messageTaskService.retryTask(id);
        return ResponseUtils.success(null);
    }
}

