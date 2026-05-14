
package com.qzer.scheduler.modules.message.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qzer.scheduler.modules.message.dto.AsyncTaskRequest;
import com.qzer.scheduler.modules.message.dto.DelayTaskRequest;
import com.qzer.scheduler.modules.message.entity.MessageTask;

public interface MessageTaskService {

    String submitAsyncTask(AsyncTaskRequest request);

    String submitDelayTask(DelayTaskRequest request);

    Page&lt;MessageTask&gt; getTaskList(Integer current, Integer size, Integer status);

    MessageTask getTaskById(Long id);

    void cancelTask(Long id);

    void retryTask(Long id);
}

