package com.qzer.scheduler.modules.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qzer.scheduler.modules.system.dto.TenantCreateRequest;
import com.qzer.scheduler.modules.system.dto.TenantUpdateRequest;
import com.qzer.scheduler.modules.system.entity.Tenant;

import java.util.List;

public interface TenantService {
    Tenant createTenant(TenantCreateRequest request);
    Tenant updateTenant(Long id, TenantUpdateRequest request);
    void deleteTenant(Long id);
    Tenant getTenantById(Long id);
    IPage<Tenant> listTenants(Page<Tenant> page, String tenantName, Integer status);
    List<Tenant> getAllTenants();
}
