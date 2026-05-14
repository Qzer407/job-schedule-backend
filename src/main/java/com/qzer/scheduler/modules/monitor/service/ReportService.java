
package com.qzer.scheduler.modules.monitor.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qzer.scheduler.modules.monitor.entity.ScheduleLog;
import com.qzer.scheduler.modules.monitor.mapper.ScheduleLogMapper;
import com.qzer.scheduler.modules.task.entity.TaskLog;
import com.qzer.scheduler.modules.task.mapper.TaskLogMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportService {

    private final TaskLogMapper taskLogMapper;

    public Map&lt;String, Object&gt; getExecutionReport() {
        Map&lt;String, Object&gt; report = new HashMap&lt;&gt;();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime dayStart = now.toLocalDate().atStartOfDay();
        LocalDateTime weekStart = dayStart.minusDays(7);
        LocalDateTime monthStart = dayStart.minusDays(30);

        report.put("today", getPeriodStats(dayStart, now));
        report.put("week", getPeriodStats(weekStart, now));
        report.put("month", getPeriodStats(monthStart, now));

        return report;
    }

    private Map&lt;String, Object&gt; getPeriodStats(LocalDateTime start, LocalDateTime end) {
        Map&lt;String, Object&gt; stats = new HashMap&lt;&gt;();

        LambdaQueryWrapper&lt;TaskLog&gt; wrapper = new LambdaQueryWrapper&lt;&gt;();
        wrapper.ge(TaskLog::getTriggerTime, start);
        wrapper.le(TaskLog::getTriggerTime, end);

        long total = taskLogMapper.selectCount(wrapper);
        stats.put("total", total);

        wrapper.clear();
        wrapper.ge(TaskLog::getTriggerTime, start);
        wrapper.le(TaskLog::getTriggerTime, end);
        wrapper.eq(TaskLog::getExecutionStatus, 1);
        long success = taskLogMapper.selectCount(wrapper);
        stats.put("success", success);

        wrapper.clear();
        wrapper.ge(TaskLog::getTriggerTime, start);
        wrapper.le(TaskLog::getTriggerTime, end);
        wrapper.eq(TaskLog::getExecutionStatus, 0);
        long fail = taskLogMapper.selectCount(wrapper);
        stats.put("fail", fail);

        double successRate = total &gt; 0 ? (double) success / total * 100 : 100;
        stats.put("successRate", Math.round(successRate * 100.0) / 100.0);

        return stats;
    }
}

