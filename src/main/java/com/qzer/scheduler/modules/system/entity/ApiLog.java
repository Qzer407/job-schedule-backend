package com.qzer.scheduler.modules.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_api_log")
public class ApiLog {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("api_key_id")
    private Long apiKeyId;

    @TableField("user_id")
    private Long userId;

    @TableField("endpoint")
    private String endpoint;

    @TableField("method")
    private String method;

    @TableField("request_params")
    private String requestParams;

    @TableField("response_status")
    private Integer responseStatus;

    @TableField("cost_time")
    private Long costTime;

    @TableField("ip")
    private String ip;

    @TableField("user_agent")
    private String userAgent;

    @TableField("create_time")
    private LocalDateTime createTime;
}
