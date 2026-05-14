package com.qzer.scheduler.modules.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qzer.scheduler.modules.system.entity.ApiLog;

import java.time.LocalDateTime;

public interface ApiLogService {
    void logApiCall(Long apiKeyId, Long userId, String endpoint, String method, 
                    String requestParams, Integer responseStatus, Long costTime, 
                    String ip, String userAgent);
    ApiLog getApiLogById(Long id);
    IPage<ApiLog> listApiLogs(Page<ApiLog> page, Long apiKeyId, Long userId, 
                             String endpoint, String method, LocalDateTime startTime, 
                             LocalDateTime endTime);
    void deleteOldLogs(LocalDateTime before);
}
