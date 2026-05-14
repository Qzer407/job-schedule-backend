package com.qzer.scheduler.modules.task.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qzer.scheduler.modules.task.entity.TaskLog;

public interface TaskLogService {

    Long saveLog(TaskLog taskLog);

    TaskLog getLogById(Long id);

    IPage<TaskLog> listLogs(Page<TaskLog> page, Long taskId, Integer status);

    void updateLogStatus(Long logId, Integer status, String errorMsg, Long duration);
}
