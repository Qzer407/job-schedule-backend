
package com.qzer.scheduler.common.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qzer.scheduler.modules.monitor.entity.AuditLog;
import com.qzer.scheduler.modules.monitor.service.AuditLogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class AuditLogAspect {

    private final AuditLogService auditLogService;
    private final ObjectMapper objectMapper;

    @Pointcut("execution(* com.qzer.scheduler.modules..controller..*(..))")
    public void controllerPointcut() {
    }

    @Around("controllerPointcut()")
    public Object audit(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = null;
        Exception exception = null;

        try {
            result = joinPoint.proceed();
            return result;
        } catch (Exception e) {
            exception = e;
            throw e;
        } finally {
            long costTime = System.currentTimeMillis() - startTime;
            saveAuditLog(joinPoint, result, exception, costTime);
        }
    }

    private void saveAuditLog(ProceedingJoinPoint joinPoint, Object result, Exception exception, long costTime) {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes == null) {
                return;
            }
            HttpServletRequest request = attributes.getRequest();

            AuditLog auditLog = new AuditLog();
            auditLog.setRequestMethod(request.getMethod());
            auditLog.setRequestUrl(request.getRequestURI());
            auditLog.setIpAddress(getClientIp(request));
            auditLog.setModule(getModule(request.getRequestURI()));
            auditLog.setOperation(joinPoint.getSignature().getName());
            auditLog.setCostTime(costTime);
            auditLog.setStatus(exception == null ? 1 : 0);

            if (exception != null) {
                auditLog.setErrorMessage(exception.getMessage());
            }

            try {
                auditLog.setRequestParams(objectMapper.writeValueAsString(joinPoint.getArgs()));
            } catch (Exception e) {
                auditLog.setRequestParams("[]");
            }

            auditLogService.save(auditLog);
        } catch (Exception e) {
            log.error("保存审计日志失败", e);
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    private String getModule(String requestURI) {
        if (requestURI.contains("/task")) {
            return "任务管理";
        } else if (requestURI.contains("/workflow")) {
            return "工作流";
        } else if (requestURI.contains("/alarm")) {
            return "告警管理";
        } else if (requestURI.contains("/auth")) {
            return "用户认证";
        } else if (requestURI.contains("/monitor")) {
            return "监控中心";
        } else if (requestURI.contains("/executor-group")) {
            return "执行器分组";
        }
        return "其他";
    }
}

