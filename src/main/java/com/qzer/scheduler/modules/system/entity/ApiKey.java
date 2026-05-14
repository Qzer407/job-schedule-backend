package com.qzer.scheduler.modules.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_api_key")
public class ApiKey {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField("api_key")
    private String apiKey;

    @TableField("api_secret")
    private String apiSecret;

    @TableField("key_name")
    private String keyName;

    @TableField("permissions")
    private String permissions;

    @TableField("rate_limit")
    private Integer rateLimit;

    @TableField("expire_time")
    private LocalDateTime expireTime;

    @TableField("status")
    private Integer status;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;
}
