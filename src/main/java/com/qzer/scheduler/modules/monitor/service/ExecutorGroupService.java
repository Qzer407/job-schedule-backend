
package com.qzer.scheduler.modules.monitor.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qzer.scheduler.modules.monitor.dto.ExecutorGroupCreateRequest;
import com.qzer.scheduler.modules.monitor.dto.ExecutorGroupUpdateRequest;
import com.qzer.scheduler.modules.monitor.entity.ExecutorGroup;
import com.qzer.scheduler.modules.monitor.mapper.ExecutorGroupMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExecutorGroupService extends ServiceImpl&lt;ExecutorGroupMapper, ExecutorGroup&gt; {

    public ExecutorGroup createGroup(ExecutorGroupCreateRequest request) {
        ExecutorGroup group = new ExecutorGroup();
        group.setGroupName(request.getGroupName());
        group.setGroupCode(request.getGroupCode());
        group.setDescription(request.getDescription());
        group.setStatus(1);
        save(group);
        log.info("创建执行器分组成功: {}", request.getGroupName());
        return group;
    }

    @Transactional
    public ExecutorGroup updateGroup(Long id, ExecutorGroupUpdateRequest request) {
        ExecutorGroup group = getById(id);
        if (group == null) {
            throw new RuntimeException("执行器分组不存在");
        }
        if (StringUtils.hasText(request.getGroupName())) {
            group.setGroupName(request.getGroupName());
        }
        if (StringUtils.hasText(request.getDescription())) {
            group.setDescription(request.getDescription());
        }
        if (request.getStatus() != null) {
            group.setStatus(request.getStatus());
        }
        group.setUpdateTime(LocalDateTime.now());
        updateById(group);
        log.info("更新执行器分组成功: {}", id);
        return group;
    }

    public void deleteGroup(Long id) {
        ExecutorGroup group = getById(id);
        if (group == null) {
            throw new RuntimeException("执行器分组不存在");
        }
        removeById(id);
        log.info("删除执行器分组成功: {}", id);
    }

    public IPage&lt;ExecutorGroup&gt; listGroups(Page&lt;ExecutorGroup&gt; page, String groupName, Integer status) {
        LambdaQueryWrapper&lt;ExecutorGroup&gt; wrapper = new LambdaQueryWrapper&lt;&gt;();
        if (StringUtils.hasText(groupName)) {
            wrapper.like(ExecutorGroup::getGroupName, groupName);
        }
        if (status != null) {
            wrapper.eq(ExecutorGroup::getStatus, status);
        }
        wrapper.orderByDesc(ExecutorGroup::getCreateTime);
        return page(page, wrapper);
    }
}

