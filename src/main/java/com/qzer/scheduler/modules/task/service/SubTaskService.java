package com.qzer.scheduler.modules.task.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qzer.scheduler.modules.task.dto.SubTaskCreateRequest;
import com.qzer.scheduler.modules.task.dto.SubTaskUpdateRequest;
import com.qzer.scheduler.modules.task.entity.SubTask;

import java.util.List;

public interface SubTaskService {

    SubTask createSubTask(Long parentTaskId, SubTaskCreateRequest request);

    SubTask getSubTaskById(Long id);

    List<SubTask> getSubTasksByParentId(Long parentTaskId);

    Page<SubTask> getSubTaskPage(Long parentTaskId, Integer pageNum, Integer pageSize);

    SubTask updateSubTask(Long id, SubTaskUpdateRequest request);

    void deleteSubTask(Long id);

    void deleteByParentTaskId(Long parentTaskId);

    List<SubTask> batchCreate(Long parentTaskId, List<SubTaskCreateRequest> requests);

    void executeSubTasks(Long parentTaskId);
}
