package com.qzer.scheduler.modules.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qzer.scheduler.modules.system.dto.ApiKeyCreateRequest;
import com.qzer.scheduler.modules.system.dto.ApiKeyUpdateRequest;
import com.qzer.scheduler.modules.system.entity.ApiKey;
import com.qzer.scheduler.modules.system.mapper.ApiKeyMapper;
import com.qzer.scheduler.modules.system.service.ApiKeyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApiKeyServiceImpl implements ApiKeyService {

    private final ApiKeyMapper apiKeyMapper;
    private static final SecureRandom secureRandom = new SecureRandom();

    @Override
    @Transactional
    public ApiKey createApiKey(ApiKeyCreateRequest request) {
        ApiKey apiKey = new ApiKey();
        apiKey.setUserId(request.getUserId());
        apiKey.setApiKey(generateApiKey());
        apiKey.setApiSecret(generateApiSecret());
        apiKey.setKeyName(request.getKeyName());
        apiKey.setPermissions(request.getPermissions());
        apiKey.setRateLimit(request.getRateLimit() != null ? request.getRateLimit() : 1000);
        apiKey.setExpireTime(request.getExpireTime());
        apiKey.setStatus(1);
        apiKey.setCreateTime(LocalDateTime.now());
        apiKey.setUpdateTime(LocalDateTime.now());
        apiKeyMapper.insert(apiKey);
        log.info("创建API密钥成功: {}", apiKey.getId());
        return apiKey;
    }

    @Override
    @Transactional
    public ApiKey updateApiKey(Long id, ApiKeyUpdateRequest request) {
        ApiKey apiKey = apiKeyMapper.selectById(id);
        if (apiKey == null) {
            throw new RuntimeException("API密钥不存在");
        }
        if (StringUtils.hasText(request.getKeyName())) {
            apiKey.setKeyName(request.getKeyName());
        }
        if (StringUtils.hasText(request.getPermissions())) {
            apiKey.setPermissions(request.getPermissions());
        }
        if (request.getRateLimit() != null) {
            apiKey.setRateLimit(request.getRateLimit());
        }
        if (request.getExpireTime() != null) {
            apiKey.setExpireTime(request.getExpireTime());
        }
        if (request.getStatus() != null) {
            apiKey.setStatus(request.getStatus());
        }
        apiKey.setUpdateTime(LocalDateTime.now());
        apiKeyMapper.updateById(apiKey);
        log.info("更新API密钥成功: {}", id);
        return apiKey;
    }

    @Override
    @Transactional
    public void deleteApiKey(Long id) {
        apiKeyMapper.deleteById(id);
        log.info("删除API密钥成功: {}", id);
    }

    @Override
    public ApiKey getApiKeyById(Long id) {
        return apiKeyMapper.selectById(id);
    }

    @Override
    public ApiKey getApiKeyByKey(String apiKey) {
        LambdaQueryWrapper<ApiKey> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ApiKey::getApiKey, apiKey);
        wrapper.eq(ApiKey::getStatus, 1);
        return apiKeyMapper.selectOne(wrapper);
    }

    @Override
    public IPage<ApiKey> listApiKeys(Page<ApiKey> page, Long userId, Integer status) {
        LambdaQueryWrapper<ApiKey> wrapper = new LambdaQueryWrapper<>();
        if (userId != null) {
            wrapper.eq(ApiKey::getUserId, userId);
        }
        if (status != null) {
            wrapper.eq(ApiKey::getStatus, status);
        }
        wrapper.orderByDesc(ApiKey::getCreateTime);
        return apiKeyMapper.selectPage(page, wrapper);
    }

    @Override
    public List<ApiKey> getUserApiKeys(Long userId) {
        LambdaQueryWrapper<ApiKey> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ApiKey::getUserId, userId);
        wrapper.orderByDesc(ApiKey::getCreateTime);
        return apiKeyMapper.selectList(wrapper);
    }

    @Override
    public List<ApiKey> getAllApiKeys() {
        LambdaQueryWrapper<ApiKey> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(ApiKey::getCreateTime);
        return apiKeyMapper.selectList(wrapper);
    }

    @Override
    @Transactional
    public void toggleApiKeyStatus(Long id) {
        ApiKey apiKey = apiKeyMapper.selectById(id);
        if (apiKey == null) {
            throw new RuntimeException("API密钥不存在");
        }
        apiKey.setStatus(apiKey.getStatus() == 1 ? 0 : 1);
        apiKey.setUpdateTime(LocalDateTime.now());
        apiKeyMapper.updateById(apiKey);
        log.info("切换API密钥状态成功，ID: {}, 新状态: {}", id, apiKey.getStatus());
    }

    @Override
    @Transactional
    public String regenerateApiKey(Long id) {
        ApiKey apiKey = apiKeyMapper.selectById(id);
        if (apiKey == null) {
            throw new RuntimeException("API密钥不存在");
        }
        String newApiKey = generateApiKey();
        String newApiSecret = generateApiSecret();
        apiKey.setApiKey(newApiKey);
        apiKey.setApiSecret(newApiSecret);
        apiKey.setUpdateTime(LocalDateTime.now());
        apiKeyMapper.updateById(apiKey);
        log.info("重新生成API密钥成功: {}", id);
        return newApiSecret;
    }

    private String generateApiKey() {
        return "ak_" + UUID.randomUUID().toString().replace("-", "");
    }

    private String generateApiSecret() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return "sk_" + Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
