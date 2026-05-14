package com.qzer.scheduler.modules.alarm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_alarm_record")
public class AlarmRecord {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("task_id")
    private Long taskId;

    @TableField("task_name")
    private String taskName;

    @TableField("alarm_type")
    private String alarmType;

    @TableField("alarm_level")
    private String alarmLevel;

    @TableField("alarm_title")
    private String alarmTitle;

    @TableField("alarm_content")
    private String alarmContent;

    @TableField("alarm_channel")
    private String alarmChannel;

    @TableField("alarm_time")
    private LocalDateTime alarmTime;

    @TableField("send_status")
    private Integer sendStatus;

    @TableField("error_msg")
    private String errorMsg;

    @TableField("create_time")
    private LocalDateTime createTime;
}
