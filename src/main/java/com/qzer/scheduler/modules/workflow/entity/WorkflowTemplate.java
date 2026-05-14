package com.qzer.scheduler.modules.workflow.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_workflow_template")
public class WorkflowTemplate {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("template_name")
    private String templateName;

    @TableField("template_desc")
    private String templateDesc;

    @TableField("template_type")
    private String templateType;

    @TableField("template_config")
    private String templateConfig;

    @TableField("status")
    private Integer status;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;
}
