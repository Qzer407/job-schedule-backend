package com.qzer.scheduler.modules.workflow.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_workflow")
public class Workflow {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("workflow_name")
    private String workflowName;

    @TableField("workflow_desc")
    private String workflowDesc;

    @TableField("status")
    private Integer status;

    @TableField("workflow_config")
    private String workflowConfig;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;
}
