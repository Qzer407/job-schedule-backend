package com.qzer.scheduler.modules.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qzer.scheduler.modules.system.dto.ApiKeyCreateRequest;
import com.qzer.scheduler.modules.system.dto.ApiKeyUpdateRequest;
import com.qzer.scheduler.modules.system.entity.ApiKey;

import java.util.List;

public interface ApiKeyService {
    ApiKey createApiKey(ApiKeyCreateRequest request);
    ApiKey updateApiKey(Long id, ApiKeyUpdateRequest request);
    void deleteApiKey(Long id);
    ApiKey getApiKeyById(Long id);
    ApiKey getApiKeyByKey(String apiKey);
    IPage<ApiKey> listApiKeys(Page<ApiKey> page, Long userId, Integer status);
    List<ApiKey> getUserApiKeys(Long userId);
    List<ApiKey> getAllApiKeys();
    void toggleApiKeyStatus(Long id);
    String regenerateApiKey(Long id);
}
