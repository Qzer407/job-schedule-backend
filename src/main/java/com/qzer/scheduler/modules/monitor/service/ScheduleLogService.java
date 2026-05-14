
package com.qzer.scheduler.modules.monitor.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qzer.scheduler.modules.monitor.entity.ScheduleLog;
import com.qzer.scheduler.modules.monitor.mapper.ScheduleLogMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduleLogService extends ServiceImpl&lt;ScheduleLogMapper, ScheduleLog&gt; {

    public IPage&lt;ScheduleLog&gt; listScheduleLogs(Page&lt;ScheduleLog&gt; page, Long taskId, Integer status) {
        LambdaQueryWrapper&lt;ScheduleLog&gt; wrapper = new LambdaQueryWrapper&lt;&gt;();
        if (taskId != null) {
            wrapper.eq(ScheduleLog::getTaskId, taskId);
        }
        if (status != null) {
            wrapper.eq(ScheduleLog::getStatus, status);
        }
        wrapper.orderByDesc(ScheduleLog::getScheduleTime);
        return page(page, wrapper);
    }
}

