package com.qzer.scheduler.modules.alarm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_alarm_config")
public class AlarmConfig {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("channel_type")
    private String channelType;

    @TableField("channel_name")
    private String channelName;

    @TableField("channel_config")
    private String channelConfig;

    @TableField("enabled")
    private Boolean enabled;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;
}
