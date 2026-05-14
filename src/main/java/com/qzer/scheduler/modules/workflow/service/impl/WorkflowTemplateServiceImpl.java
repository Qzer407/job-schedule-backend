package com.qzer.scheduler.modules.workflow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qzer.scheduler.modules.workflow.dto.WorkflowTemplateCreateRequest;
import com.qzer.scheduler.modules.workflow.dto.WorkflowTemplateUpdateRequest;
import com.qzer.scheduler.modules.workflow.entity.Workflow;
import com.qzer.scheduler.modules.workflow.entity.WorkflowTemplate;
import com.qzer.scheduler.modules.workflow.mapper.WorkflowMapper;
import com.qzer.scheduler.modules.workflow.mapper.WorkflowTemplateMapper;
import com.qzer.scheduler.modules.workflow.service.WorkflowTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkflowTemplateServiceImpl implements WorkflowTemplateService {

    private final WorkflowTemplateMapper workflowTemplateMapper;
    private final WorkflowMapper workflowMapper;

    @Override
    @Transactional
    public WorkflowTemplate createTemplate(WorkflowTemplateCreateRequest request) {
        WorkflowTemplate template = new WorkflowTemplate();
        BeanUtils.copyProperties(request, template);
        template.setCreateTime(LocalDateTime.now());
        template.setUpdateTime(LocalDateTime.now());

        workflowTemplateMapper.insert(template);
        return template;
    }

    @Override
    public WorkflowTemplate getTemplateById(Long id) {
        return workflowTemplateMapper.selectById(id);
    }

    @Override
    public List<WorkflowTemplate> getTemplatesByType(String templateType) {
        return workflowTemplateMapper.selectByType(templateType);
    }

    @Override
    public Page<WorkflowTemplate> getTemplatePage(Integer pageNum, Integer pageSize, String templateType) {
        Page<WorkflowTemplate> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<WorkflowTemplate> wrapper = new LambdaQueryWrapper<>();
        if (templateType != null && !templateType.isEmpty()) {
            wrapper.eq(WorkflowTemplate::getTemplateType, templateType);
        }
        wrapper.orderByDesc(WorkflowTemplate::getCreateTime);
        return workflowTemplateMapper.selectPage(page, wrapper);
    }

    @Override
    @Transactional
    public WorkflowTemplate updateTemplate(Long id, WorkflowTemplateUpdateRequest request) {
        WorkflowTemplate template = workflowTemplateMapper.selectById(id);
        if (template == null) {
            throw new RuntimeException("模板不存在");
        }

        if (request.getTemplateName() != null) {
            template.setTemplateName(request.getTemplateName());
        }
        if (request.getTemplateDesc() != null) {
            template.setTemplateDesc(request.getTemplateDesc());
        }
        if (request.getTemplateType() != null) {
            template.setTemplateType(request.getTemplateType());
        }
        if (request.getTemplateConfig() != null) {
            template.setTemplateConfig(request.getTemplateConfig());
        }
        if (request.getStatus() != null) {
            template.setStatus(request.getStatus());
        }
        template.setUpdateTime(LocalDateTime.now());

        workflowTemplateMapper.updateById(template);
        return template;
    }

    @Override
    @Transactional
    public void deleteTemplate(Long id) {
        WorkflowTemplate template = workflowTemplateMapper.selectById(id);
        if (template == null) {
            throw new RuntimeException("模板不存在");
        }
        workflowTemplateMapper.deleteById(id);
    }

    @Override
    @Transactional
    public Workflow createWorkflowFromTemplate(Long templateId) {
        WorkflowTemplate template = workflowTemplateMapper.selectById(templateId);
        if (template == null) {
            throw new RuntimeException("模板不存在");
        }

        Workflow workflow = new Workflow();
        workflow.setWorkflowName(template.getTemplateName());
        workflow.setWorkflowDesc(template.getTemplateDesc());
        workflow.setWorkflowConfig(template.getTemplateConfig());
        workflow.setStatus(0);
        workflow.setCreateTime(LocalDateTime.now());
        workflow.setUpdateTime(LocalDateTime.now());

        workflowMapper.insert(workflow);
        return workflow;
    }

    @Override
    public List<WorkflowTemplate> getAllTemplates() {
        LambdaQueryWrapper<WorkflowTemplate> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(WorkflowTemplate::getCreateTime);
        return workflowTemplateMapper.selectList(wrapper);
    }
}
