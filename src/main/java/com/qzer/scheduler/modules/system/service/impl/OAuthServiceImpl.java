package com.qzer.scheduler.modules.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qzer.scheduler.modules.system.dto.OAuthClientCreateRequest;
import com.qzer.scheduler.modules.system.dto.OAuthClientUpdateRequest;
import com.qzer.scheduler.modules.system.entity.OAuthClient;
import com.qzer.scheduler.modules.system.entity.OAuthUser;
import com.qzer.scheduler.modules.system.mapper.OAuthClientMapper;
import com.qzer.scheduler.modules.system.mapper.OAuthUserMapper;
import com.qzer.scheduler.modules.system.service.OAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuthServiceImpl implements OAuthService {

    private final OAuthClientMapper oAuthClientMapper;
    private final OAuthUserMapper oAuthUserMapper;

    @Override
    @Transactional
    public OAuthClient createClient(OAuthClientCreateRequest request) {
        OAuthClient client = new OAuthClient();
        client.setClientId(request.getClientId());
        client.setClientName(request.getClientName());
        client.setProvider(request.getProvider());
        client.setClientSecret(request.getClientSecret());
        client.setRedirectUri(request.getRedirectUri());
        client.setScope(request.getScope());
        client.setStatus(request.getStatus() != null ? request.getStatus() : 1);
        client.setCreateTime(LocalDateTime.now());
        client.setUpdateTime(LocalDateTime.now());
        oAuthClientMapper.insert(client);
        log.info("创建OAuth客户端成功: {}", client.getId());
        return client;
    }

    @Override
    @Transactional
    public OAuthClient updateClient(Long id, OAuthClientUpdateRequest request) {
        OAuthClient client = oAuthClientMapper.selectById(id);
        if (client == null) {
            throw new RuntimeException("OAuth客户端不存在");
        }
        if (StringUtils.hasText(request.getClientName())) {
            client.setClientName(request.getClientName());
        }
        if (StringUtils.hasText(request.getClientSecret())) {
            client.setClientSecret(request.getClientSecret());
        }
        if (StringUtils.hasText(request.getRedirectUri())) {
            client.setRedirectUri(request.getRedirectUri());
        }
        if (StringUtils.hasText(request.getScope())) {
            client.setScope(request.getScope());
        }
        if (request.getStatus() != null) {
            client.setStatus(request.getStatus());
        }
        client.setUpdateTime(LocalDateTime.now());
        oAuthClientMapper.updateById(client);
        log.info("更新OAuth客户端成功: {}", id);
        return client;
    }

    @Override
    @Transactional
    public void deleteClient(Long id) {
        oAuthClientMapper.deleteById(id);
        log.info("删除OAuth客户端成功: {}", id);
    }

    @Override
    public OAuthClient getClientById(Long id) {
        return oAuthClientMapper.selectById(id);
    }

    @Override
    public OAuthClient getClientByClientId(String clientId) {
        LambdaQueryWrapper<OAuthClient> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OAuthClient::getClientId, clientId);
        return oAuthClientMapper.selectOne(wrapper);
    }

    @Override
    public IPage<OAuthClient> listClients(Page<OAuthClient> page, String clientName, Integer status) {
        LambdaQueryWrapper<OAuthClient> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(clientName)) {
            wrapper.like(OAuthClient::getClientName, clientName);
        }
        if (status != null) {
            wrapper.eq(OAuthClient::getStatus, status);
        }
        wrapper.orderByDesc(OAuthClient::getCreateTime);
        return oAuthClientMapper.selectPage(page, wrapper);
    }

    @Override
    public List<OAuthClient> getAllClients() {
        LambdaQueryWrapper<OAuthClient> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OAuthClient::getStatus, 1);
        wrapper.orderByDesc(OAuthClient::getCreateTime);
        return oAuthClientMapper.selectList(wrapper);
    }

    @Override
    @Transactional
    public OAuthUser bindUser(Long userId, String provider, String openid, String nickname, String avatar, String email) {
        LambdaQueryWrapper<OAuthUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OAuthUser::getUserId, userId);
        wrapper.eq(OAuthUser::getProvider, provider);
        OAuthUser existing = oAuthUserMapper.selectOne(wrapper);
        
        if (existing != null) {
            existing.setOpenid(openid);
            existing.setNickname(nickname);
            existing.setAvatar(avatar);
            existing.setEmail(email);
            existing.setUpdateTime(LocalDateTime.now());
            oAuthUserMapper.updateById(existing);
            log.info("更新OAuth绑定成功，用户ID: {}, 提供商: {}", userId, provider);
            return existing;
        }
        
        OAuthUser oAuthUser = new OAuthUser();
        oAuthUser.setUserId(userId);
        oAuthUser.setProvider(provider);
        oAuthUser.setOpenid(openid);
        oAuthUser.setNickname(nickname);
        oAuthUser.setAvatar(avatar);
        oAuthUser.setEmail(email);
        oAuthUser.setCreateTime(LocalDateTime.now());
        oAuthUser.setUpdateTime(LocalDateTime.now());
        oAuthUserMapper.insert(oAuthUser);
        log.info("绑定OAuth用户成功，用户ID: {}, 提供商: {}", userId, provider);
        return oAuthUser;
    }

    @Override
    @Transactional
    public void unbindUser(Long userId, String provider) {
        LambdaQueryWrapper<OAuthUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OAuthUser::getUserId, userId);
        wrapper.eq(OAuthUser::getProvider, provider);
        oAuthUserMapper.delete(wrapper);
        log.info("解绑OAuth用户成功，用户ID: {}, 提供商: {}", userId, provider);
    }

    @Override
    public List<OAuthUser> getUserBindings(Long userId) {
        LambdaQueryWrapper<OAuthUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OAuthUser::getUserId, userId);
        return oAuthUserMapper.selectList(wrapper);
    }
}
