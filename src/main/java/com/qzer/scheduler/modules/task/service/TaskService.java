package com.qzer.scheduler.modules.task.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qzer.scheduler.modules.task.dto.TaskCreateRequest;
import com.qzer.scheduler.modules.task.dto.TaskUpdateRequest;
import com.qzer.scheduler.modules.task.entity.TaskInfo;

public interface TaskService {

    TaskInfo createTask(TaskCreateRequest request);

    TaskInfo updateTask(Long id, TaskUpdateRequest request);

    void deleteTask(Long id);

    TaskInfo getTaskById(Long id);

    IPage<TaskInfo> listTasks(Page<TaskInfo> page, String taskName, String taskGroup, Integer status);

    void startTask(Long id);

    void stopTask(Long id);

    void triggerTask(Long id, String executorParam);
}
