
package com.qzer.scheduler.modules.monitor.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qzer.scheduler.modules.monitor.entity.AuditLog;
import com.qzer.scheduler.modules.monitor.mapper.AuditLogMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditLogService extends ServiceImpl&lt;AuditLogMapper, AuditLog&gt; {

    public IPage&lt;AuditLog&gt; listAuditLogs(Page&lt;AuditLog&gt; page, String module, Integer status) {
        LambdaQueryWrapper&lt;AuditLog&gt; wrapper = new LambdaQueryWrapper&lt;&gt;();
        if (StringUtils.hasText(module)) {
            wrapper.like(AuditLog::getModule, module);
        }
        if (status != null) {
            wrapper.eq(AuditLog::getStatus, status);
        }
        wrapper.orderByDesc(AuditLog::getCreateTime);
        return page(page, wrapper);
    }
}

