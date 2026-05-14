package com.qzer.scheduler.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    
    SUCCESS("200", "操作成功"),
    
    PARAM_ERROR("400", "参数错误"),
    PARAM_MISSING("400", "缺少必要参数"),
    
    UNAUTHORIZED("401", "未授权"),
    TOKEN_INVALID("401", "Token无效或已过期"),
    TOKEN_EXPIRED("401", "Token已过期"),
    
    FORBIDDEN("403", "禁止访问"),
    ACCESS_DENIED("403", "无权限访问"),
    
    NOT_FOUND("404", "资源不存在"),
    TASK_NOT_FOUND("404", "任务不存在"),
    WORKFLOW_NOT_FOUND("404", "工作流不存在"),
    USER_NOT_FOUND("404", "用户不存在"),
    
    CONFLICT("409", "资源冲突"),
    USERNAME_EXISTS("409", "用户名已存在"),
    CHANNEL_TYPE_EXISTS("409", "渠道类型已存在"),
    
    SERVER_ERROR("500", "服务器内部错误"),
    XXL_JOB_ERROR("500", "XXL-JOB调用失败"),
    
    TASK_START_FAILED("500", "任务启动失败"),
    TASK_STOP_FAILED("500", "任务停止失败"),
    TASK_TRIGGER_FAILED("500", "任务触发失败"),
    
    ALARM_SEND_FAILED("500", "告警发送失败"),
    WORKFLOW_EXECUTE_FAILED("500", "工作流执行失败"),
    
    PASSWORD_ERROR("400", "密码错误"),
    OLD_PASSWORD_ERROR("400", "旧密码错误"),
    USER_DISABLED("403", "用户已被禁用");

    private final String code;
    private final String message;
}
