package com.capture.api.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ServiceLogAspect {

    final static Logger logger = LoggerFactory.getLogger(ServiceLogAspect.class);

    /**
     * AOP通知：
     * 1. 前置通知
     * 2. 后置通知
     * 3. 环绕通知
     * 4. 异常通知
     * 5. 最终通知
     */

    @Around("execution(* com.capture.*.service.impl..*.*(..))")
    public Object recordTimeOfService(ProceedingJoinPoint joinPoint)
                                            throws Throwable {

        logger.info("==== 开始执行 {}.{}====",
                joinPoint.getTarget().getClass(),
                joinPoint.getSignature().getName());

        long start = System.currentTimeMillis();

        Object result = joinPoint.proceed();

        long end = System.currentTimeMillis();
        long takeTime = end - start;

        if (takeTime > 3000) {
            logger.error("当前执行耗时：{}", takeTime);
        } else if (takeTime > 2000) {
            logger.warn("当前执行耗时：{}", takeTime);
        } else {
            logger.info("当前执行耗时：{}", takeTime);
        }

        return result;
    }

}
