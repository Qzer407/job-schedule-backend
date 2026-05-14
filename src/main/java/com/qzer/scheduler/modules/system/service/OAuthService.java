package com.qzer.scheduler.modules.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qzer.scheduler.modules.system.dto.OAuthClientCreateRequest;
import com.qzer.scheduler.modules.system.dto.OAuthClientUpdateRequest;
import com.qzer.scheduler.modules.system.entity.OAuthClient;
import com.qzer.scheduler.modules.system.entity.OAuthUser;

import java.util.List;

public interface OAuthService {
    OAuthClient createClient(OAuthClientCreateRequest request);
    OAuthClient updateClient(Long id, OAuthClientUpdateRequest request);
    void deleteClient(Long id);
    OAuthClient getClientById(Long id);
    OAuthClient getClientByClientId(String clientId);
    IPage<OAuthClient> listClients(Page<OAuthClient> page, String clientName, Integer status);
    List<OAuthClient> getAllClients();
    OAuthUser bindUser(Long userId, String provider, String openid, String nickname, String avatar, String email);
    void unbindUser(Long userId, String provider);
    List<OAuthUser> getUserBindings(Long userId);
}
