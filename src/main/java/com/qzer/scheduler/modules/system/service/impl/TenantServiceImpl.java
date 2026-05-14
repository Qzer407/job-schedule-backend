package com.qzer.scheduler.modules.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qzer.scheduler.modules.system.dto.TenantCreateRequest;
import com.qzer.scheduler.modules.system.dto.TenantUpdateRequest;
import com.qzer.scheduler.modules.system.entity.Tenant;
import com.qzer.scheduler.modules.system.mapper.TenantMapper;
import com.qzer.scheduler.modules.system.service.TenantService;
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
public class TenantServiceImpl implements TenantService {

    private final TenantMapper tenantMapper;

    @Override
    @Transactional
    public Tenant createTenant(TenantCreateRequest request) {
        Tenant tenant = new Tenant();
        tenant.setTenantCode(request.getTenantCode());
        tenant.setTenantName(request.getTenantName());
        tenant.setContactName(request.getContactName());
        tenant.setContactPhone(request.getContactPhone());
        tenant.setContactEmail(request.getContactEmail());
        tenant.setStatus(request.getStatus() != null ? request.getStatus() : 1);
        tenant.setExpireTime(request.getExpireTime());
        tenant.setMaxUsers(request.getMaxUsers() != null ? request.getMaxUsers() : 10);
        tenant.setMaxTasks(request.getMaxTasks() != null ? request.getMaxTasks() : 100);
        tenant.setCreateTime(LocalDateTime.now());
        tenant.setUpdateTime(LocalDateTime.now());
        tenantMapper.insert(tenant);
        log.info("创建租户成功: {}", tenant.getId());
        return tenant;
    }

    @Override
    @Transactional
    public Tenant updateTenant(Long id, TenantUpdateRequest request) {
        Tenant tenant = tenantMapper.selectById(id);
        if (tenant == null) {
            throw new RuntimeException("租户不存在");
        }
        if (StringUtils.hasText(request.getTenantName())) {
            tenant.setTenantName(request.getTenantName());
        }
        if (StringUtils.hasText(request.getContactName())) {
            tenant.setContactName(request.getContactName());
        }
        if (StringUtils.hasText(request.getContactPhone())) {
            tenant.setContactPhone(request.getContactPhone());
        }
        if (StringUtils.hasText(request.getContactEmail())) {
            tenant.setContactEmail(request.getContactEmail());
        }
        if (request.getStatus() != null) {
            tenant.setStatus(request.getStatus());
        }
        if (request.getExpireTime() != null) {
            tenant.setExpireTime(request.getExpireTime());
        }
        if (request.getMaxUsers() != null) {
            tenant.setMaxUsers(request.getMaxUsers());
        }
        if (request.getMaxTasks() != null) {
            tenant.setMaxTasks(request.getMaxTasks());
        }
        tenant.setUpdateTime(LocalDateTime.now());
        tenantMapper.updateById(tenant);
        log.info("更新租户成功: {}", id);
        return tenant;
    }

    @Override
    @Transactional
    public void deleteTenant(Long id) {
        tenantMapper.deleteById(id);
        log.info("删除租户成功: {}", id);
    }

    @Override
    public Tenant getTenantById(Long id) {
        return tenantMapper.selectById(id);
    }

    @Override
    public IPage<Tenant> listTenants(Page<Tenant> page, String tenantName, Integer status) {
        LambdaQueryWrapper<Tenant> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(tenantName)) {
            wrapper.like(Tenant::getTenantName, tenantName);
        }
        if (status != null) {
            wrapper.eq(Tenant::getStatus, status);
        }
        wrapper.orderByDesc(Tenant::getCreateTime);
        return tenantMapper.selectPage(page, wrapper);
    }

    @Override
    public List<Tenant> getAllTenants() {
        LambdaQueryWrapper<Tenant> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Tenant::getStatus, 1);
        wrapper.orderByDesc(Tenant::getCreateTime);
        return tenantMapper.selectList(wrapper);
    }
}
