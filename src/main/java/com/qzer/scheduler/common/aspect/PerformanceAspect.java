package com.qzer.scheduler.common.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class PerformanceAspect {

    @Pointcut("execution(* com.qzer.scheduler.modules..controller..*(..))")
    public void controllerPointcut() {
    }

    @Pointcut("execution(* com.qzer.scheduler.modules..service..*(..))")
    public void servicePointcut() {
    }

    @Around("controllerPointcut()")
    public Object monitorControllerPerformance(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        
        try {
            Object result = joinPoint.proceed();
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            if (duration > 1000) {
                log.warn("Controller method [{}.{}] took {}ms (slow query)", className, methodName, duration);
            } else {
                log.info("Controller method [{}.{}] took {}ms", className, methodName, duration);
            }
            return result;
        } catch (Throwable e) {
            long endTime = System.currentTimeMillis();
            log.error("Controller method [{}.{}] failed in {}ms", className, methodName, endTime - startTime, e);
            throw e;
        }
    }

    @Around("servicePointcut()")
    public Object monitorServicePerformance(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        
        try {
            Object result = joinPoint.proceed();
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            if (duration > 500) {
                log.warn("Service method [{}.{}] took {}ms", className, methodName, duration);
            }
            return result;
        } catch (Throwable e) {
            throw e;
        }
    }
}
