package com.qzer.scheduler.modules.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_tenant")
public class Tenant {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("tenant_code")
    private String tenantCode;

    @TableField("tenant_name")
    private String tenantName;

    @TableField("contact_name")
    private String contactName;

    @TableField("contact_phone")
    private String contactPhone;

    @TableField("contact_email")
    private String contactEmail;

    @TableField("status")
    private Integer status;

    @TableField("expire_time")
    private LocalDateTime expireTime;

    @TableField("max_users")
    private Integer maxUsers;

    @TableField("max_tasks")
    private Integer maxTasks;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;
}
