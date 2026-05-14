package com.qzer.scheduler.modules.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qzer.scheduler.common.dto.response.ApiResponse;
import com.qzer.scheduler.modules.system.dto.OAuthClientCreateRequest;
import com.qzer.scheduler.modules.system.dto.OAuthClientUpdateRequest;
import com.qzer.scheduler.modules.system.entity.OAuthClient;
import com.qzer.scheduler.modules.system.entity.OAuthUser;
import com.qzer.scheduler.modules.system.service.OAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/system/oauth")
@RequiredArgsConstructor
public class OAuthController {

    private final OAuthService oAuthService;

    @PostMapping("/client")
    public ApiResponse<OAuthClient> createClient(@RequestBody OAuthClientCreateRequest request) {
        return ApiResponse.success(oAuthService.createClient(request));
    }

    @PutMapping("/client/{id}")
    public ApiResponse<OAuthClient> updateClient(@PathVariable Long id, @RequestBody OAuthClientUpdateRequest request) {
        return ApiResponse.success(oAuthService.updateClient(id, request));
    }

    @DeleteMapping("/client/{id}")
    public ApiResponse<Void> deleteClient(@PathVariable Long id) {
        oAuthService.deleteClient(id);
        return ApiResponse.success(null);
    }

    @GetMapping("/client/{id}")
    public ApiResponse<OAuthClient> getClientById(@PathVariable Long id) {
        return ApiResponse.success(oAuthService.getClientById(id));
    }

    @GetMapping("/client/page")
    public ApiResponse<IPage<OAuthClient>> listClients(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String clientName,
            @RequestParam(required = false) Integer status) {
        Page<OAuthClient> page = new Page<>(current, size);
        return ApiResponse.success(oAuthService.listClients(page, clientName, status));
    }

    @GetMapping("/client/all")
    public ApiResponse<List<OAuthClient>> getAllClients() {
        return ApiResponse.success(oAuthService.getAllClients());
    }

    @PostMapping("/user/{userId}/bind")
    public ApiResponse<OAuthUser> bindUser(
            @PathVariable Long userId,
            @RequestParam String provider,
            @RequestParam String openid,
            @RequestParam(required = false) String nickname,
            @RequestParam(required = false) String avatar,
            @RequestParam(required = false) String email) {
        return ApiResponse.success(oAuthService.bindUser(userId, provider, openid, nickname, avatar, email));
    }

    @DeleteMapping("/user/{userId}/bind/{provider}")
    public ApiResponse<Void> unbindUser(@PathVariable Long userId, @PathVariable String provider) {
        oAuthService.unbindUser(userId, provider);
        return ApiResponse.success(null);
    }

    @GetMapping("/user/{userId}/bindings")
    public ApiResponse<List<OAuthUser>> getUserBindings(@PathVariable Long userId) {
        return ApiResponse.success(oAuthService.getUserBindings(userId));
    }
}
