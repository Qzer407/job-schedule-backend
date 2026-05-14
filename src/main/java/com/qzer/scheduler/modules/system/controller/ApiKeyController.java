package com.qzer.scheduler.modules.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qzer.scheduler.common.dto.response.ApiResponse;
import com.qzer.scheduler.common.utils.ExcelExportUtil;
import com.qzer.scheduler.modules.system.dto.ApiKeyCreateRequest;
import com.qzer.scheduler.modules.system.dto.ApiKeyUpdateRequest;
import com.qzer.scheduler.modules.system.entity.ApiKey;
import com.qzer.scheduler.modules.system.service.ApiKeyService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/system/apikey")
@RequiredArgsConstructor
public class ApiKeyController {

    private final ApiKeyService apiKeyService;
    private final ExcelExportUtil excelExportUtil;

    @PostMapping
    public ApiResponse<ApiKey> createApiKey(@RequestBody ApiKeyCreateRequest request) {
        return ApiResponse.success(apiKeyService.createApiKey(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<ApiKey> updateApiKey(@PathVariable Long id, @RequestBody ApiKeyUpdateRequest request) {
        return ApiResponse.success(apiKeyService.updateApiKey(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteApiKey(@PathVariable Long id) {
        apiKeyService.deleteApiKey(id);
        return ApiResponse.success(null);
    }

    @GetMapping("/{id}")
    public ApiResponse<ApiKey> getApiKeyById(@PathVariable Long id) {
        return ApiResponse.success(apiKeyService.getApiKeyById(id));
    }

    @GetMapping("/page")
    public ApiResponse<IPage<ApiKey>> listApiKeys(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Integer status) {
        Page<ApiKey> page = new Page<>(current, size);
        return ApiResponse.success(apiKeyService.listApiKeys(page, userId, status));
    }

    @GetMapping("/user/{userId}")
    public ApiResponse<List<ApiKey>> getUserApiKeys(@PathVariable Long userId) {
        return ApiResponse.success(apiKeyService.getUserApiKeys(userId));
    }

    @PutMapping("/{id}/toggle")
    public ApiResponse<Void> toggleApiKeyStatus(@PathVariable Long id) {
        apiKeyService.toggleApiKeyStatus(id);
        return ApiResponse.success(null);
    }

    @PostMapping("/{id}/regenerate")
    public ApiResponse<String> regenerateApiKey(@PathVariable Long id) {
        return ApiResponse.success(apiKeyService.regenerateApiKey(id));
    }

    @GetMapping("/export")
    public void exportApiKeys(HttpServletResponse response, @RequestParam(required = false) Long userId) throws IOException {
        List<ApiKey> apiKeys = userId != null ? apiKeyService.getUserApiKeys(userId) : apiKeyService.getAllApiKeys();
        String[] headers = {"ID", "用户ID", "API Key", "API Secret", "密钥名称", "权限", 
                          "限流次数", "过期时间", "状态", "创建时间", "更新时间"};
        String[] fieldNames = {"id", "userId", "apiKey", "apiSecret", "keyName", "permissions", 
                            "rateLimit", "expireTime", "status", "createTime", "updateTime"};
        excelExportUtil.exportExcel(response, apiKeys, "api_keys", "API密钥列表", headers, fieldNames);
    }
}
