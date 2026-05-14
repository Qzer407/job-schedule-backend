package com.qzer.scheduler.modules.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_oauth_client")
public class OAuthClient {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("client_id")
    private String clientId;

    @TableField("client_name")
    private String clientName;

    @TableField("provider")
    private String provider;

    @TableField("client_secret")
    private String clientSecret;

    @TableField("redirect_uri")
    private String redirectUri;

    @TableField("scope")
    private String scope;

    @TableField("status")
    private Integer status;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;
}
