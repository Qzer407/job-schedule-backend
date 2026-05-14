package com.qzer.scheduler.modules.workflow.controller;

import com.qzer.scheduler.common.dto.response.ApiResponse;
import com.qzer.scheduler.modules.workflow.dto.WorkflowTemplateCreateRequest;
import com.qzer.scheduler.modules.workflow.dto.WorkflowTemplateUpdateRequest;
import com.qzer.scheduler.modules.workflow.entity.Workflow;
import com.qzer.scheduler.modules.workflow.entity.WorkflowTemplate;
import com.qzer.scheduler.modules.workflow.service.WorkflowTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/workflow-templates")
@Tag(name = "工作流模板管理", description = "工作流模板管理接口")
@RequiredArgsConstructor
public class WorkflowTemplateController {

    private final WorkflowTemplateService workflowTemplateService;

    @GetMapping
    @Operation(summary = "查询模板列表")
    public ResponseEntity<ApiResponse<List<WorkflowTemplate>>> getTemplates(
            @Parameter(description = "模板类型") @RequestParam(required = false) String templateType) {
        List<WorkflowTemplate> templates = templateType != null
                ? workflowTemplateService.getTemplatesByType(templateType)
                : workflowTemplateService.getAllTemplates();
        return ResponseEntity.ok(ApiResponse.success(templates));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询模板详情")
    public ResponseEntity<ApiResponse<WorkflowTemplate>> getTemplateById(
            @Parameter(description = "模板ID") @PathVariable Long id) {
        WorkflowTemplate template = workflowTemplateService.getTemplateById(id);
        return ResponseEntity.ok(ApiResponse.success(template));
    }

    @PostMapping
    @Operation(summary = "创建模板")
    public ResponseEntity<ApiResponse<WorkflowTemplate>> createTemplate(
            @Valid @RequestBody WorkflowTemplateCreateRequest request) {
        WorkflowTemplate template = workflowTemplateService.createTemplate(request);
        return ResponseEntity.ok(ApiResponse.success(template));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新模板")
    public ResponseEntity<ApiResponse<WorkflowTemplate>> updateTemplate(
            @Parameter(description = "模板ID") @PathVariable Long id,
            @RequestBody WorkflowTemplateUpdateRequest request) {
        WorkflowTemplate template = workflowTemplateService.updateTemplate(id, request);
        return ResponseEntity.ok(ApiResponse.success(template));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除模板")
    public ResponseEntity<ApiResponse<Void>> deleteTemplate(
            @Parameter(description = "模板ID") @PathVariable Long id) {
        workflowTemplateService.deleteTemplate(id);
        return ResponseEntity.ok(ApiResponse.success());
    }

    @PostMapping("/{id}/apply")
    @Operation(summary = "从模板创建工作流")
    public ResponseEntity<ApiResponse<Workflow>> applyTemplate(
            @Parameter(description = "模板ID") @PathVariable Long id) {
        Workflow workflow = workflowTemplateService.createWorkflowFromTemplate(id);
        return ResponseEntity.ok(ApiResponse.success(workflow));
    }
}
