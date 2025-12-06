package com.microservices.identity_service.utils;


import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    // 1. Define a Pointcut for the user-service package
    @Around("execution(* com.example.userservice.service.*.*(..))")
    public Object logAroundServiceMethods(ProceedingJoinPoint joinPoint) throws Throwable {

        // Get Method Details Dynamically
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        // 1. Log Method Entry
        log.info(">>>> {}.{}() START with arguments: {}", className, methodName, args);

        long startTime = System.currentTimeMillis();
        Object result = null;

        try {
            // 2. Execute the actual method
            result = joinPoint.proceed();
        } catch (IllegalArgumentException e) {
            // 3. Log Exception (if any)
            log.error("!!!! {}.{}() Illegal argument: {} in {}", className, methodName, args, e.getMessage());
            throw e;
        }

        long executionTime = System.currentTimeMillis() - startTime;

        // 4. Log Method Exit
        log.info("<<<< {}.{}() END with result: {} in {}ms", className, methodName, result, executionTime);

        return result;
    }
}
