package com.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Aspect
@Component
public class ActionLoggingAspect {

    // log before controller methods
    @Before("execution(* com.controller..*(..))")
    public void logBefore(JoinPoint jp) {
        String username = getUsername();
        log.info("➡️ BEFORE: User [{}] calls: {} with args: {}",
                username,
                jp.getSignature().toShortString(),
                Arrays.toString(jp.getArgs()));
    }

    // log after controller methods
    @AfterReturning(pointcut = "execution(* com.controller..*(..))", returning = "result")
    public void logAfter(JoinPoint jp, Object result) {
        String username = getUsername();
        log.info("✔️ AFTER RETURN: User [{}] finished: {} returned: {}",
                username,
                jp.getSignature().toShortString(),
                result);
    }

    // log exceptions from controller methods
    @AfterThrowing(pointcut = "execution(* com.controller..*(..))", throwing = "ex")
    public void logException(JoinPoint jp, Throwable ex) {
        String username = getUsername();
        log.error("❌ Controller error: user [{}], method [{}], message: {}",
                username,
                jp.getSignature().toShortString(),
                ex.getMessage(),
                ex);
    }

    // log for performance of service methods (measure execution time)
    @Around("execution(* com.service..*(..))")
    public Object measureTime(ProceedingJoinPoint pjp) throws Throwable {
        long start = System.currentTimeMillis();
        String username = getUsername();

        try {
            Object returned = pjp.proceed();
            long diff = System.currentTimeMillis() - start;

            log.info("⏱️ PERFORMANCE: [{}] executed {} in {} ms",
                    username,
                    pjp.getSignature().toShortString(),
                    diff);

            return returned;
        } catch (Throwable ex) {
            log.error("🔥 SERVICE ERROR by [{}] in {} | Message: {}",
                    username,
                    pjp.getSignature().toShortString(),
                    ex.getMessage(),
                    ex);
            throw ex;
        }
    }

    private String getUsername() {
        return "SYSTEM";
    }
}
