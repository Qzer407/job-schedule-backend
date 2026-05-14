package com.qzer.scheduler.modules.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qzer.scheduler.modules.system.entity.ApiLog;
import com.qzer.scheduler.modules.system.mapper.ApiLogMapper;
import com.qzer.scheduler.modules.system.service.ApiLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApiLogServiceImpl implements ApiLogService {

    private final ApiLogMapper apiLogMapper;

    @Override
    @Transactional
    public void logApiCall(Long apiKeyId, Long userId, String endpoint, String method, 
                          String requestParams, Integer responseStatus, Long costTime, 
                          String ip, String userAgent) {
        ApiLog apiLog = new ApiLog();
        apiLog.setApiKeyId(apiKeyId);
        apiLog.setUserId(userId);
        apiLog.setEndpoint(endpoint);
        apiLog.setMethod(method);
        apiLog.setRequestParams(requestParams);
        apiLog.setResponseStatus(responseStatus);
        apiLog.setCostTime(costTime);
        apiLog.setIp(ip);
        apiLog.setUserAgent(userAgent);
        apiLog.setCreateTime(LocalDateTime.now());
        apiLogMapper.insert(apiLog);
    }

    @Override
    public ApiLog getApiLogById(Long id) {
        return apiLogMapper.selectById(id);
    }

    @Override
    public IPage<ApiLog> listApiLogs(Page<ApiLog> page, Long apiKeyId, Long userId, 
                                    String endpoint, String method, LocalDateTime startTime, 
                                    LocalDateTime endTime) {
        LambdaQueryWrapper<ApiLog> wrapper = new LambdaQueryWrapper<>();
        if (apiKeyId != null) {
            wrapper.eq(ApiLog::getApiKeyId, apiKeyId);
        }
        if (userId != null) {
            wrapper.eq(ApiLog::getUserId, userId);
        }
        if (StringUtils.hasText(endpoint)) {
            wrapper.like(ApiLog::getEndpoint, endpoint);
        }
        if (StringUtils.hasText(method)) {
            wrapper.eq(ApiLog::getMethod, method);
        }
        if (startTime != null) {
            wrapper.ge(ApiLog::getCreateTime, startTime);
        }
        if (endTime != null) {
            wrapper.le(ApiLog::getCreateTime, endTime);
        }
        wrapper.orderByDesc(ApiLog::getCreateTime);
        return apiLogMapper.selectPage(page, wrapper);
    }

    @Override
    @Transactional
    public void deleteOldLogs(LocalDateTime before) {
        LambdaQueryWrapper<ApiLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.lt(ApiLog::getCreateTime, before);
        int count = apiLogMapper.delete(wrapper);
        log.info("删除旧API日志成功，数量: {}", count);
    }
}
