
package com.qzer.scheduler.modules.monitor.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("t_audit_log")
public class AuditLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField("username")
    private String username;

    @TableField("operation")
    private String operation;

    @TableField("module")
    private String module;

    @TableField("description")
    private String description;

    @TableField("request_method")
    private String requestMethod;

    @TableField("request_url")
    private String requestUrl;

    @TableField("request_params")
    private String requestParams;

    @TableField("response_result")
    private String responseResult;

    @TableField("ip_address")
    private String ipAddress;

    @TableField("status")
    private Integer status;

    @TableField("cost_time")
    private Long costTime;

    @TableField("error_message")
    private String errorMessage;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}

