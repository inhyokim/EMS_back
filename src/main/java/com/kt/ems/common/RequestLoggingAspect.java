package com.kt.ems.common;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class RequestLoggingAspect {
    private static final Logger log = LoggerFactory.getLogger(RequestLoggingAspect.class);
    
    @Around("execution(* com.kt.ems.web..*(..))")
    public Object logAround(ProceedingJoinPoint pjp) throws Throwable {
        long t = System.currentTimeMillis();
        String sig = pjp.getSignature().toShortString();
        log.info("→ {}", sig);
        try {
            Object r = pjp.proceed();
            long took = System.currentTimeMillis() - t;
            if (r instanceof ResponseEntity<?> re) {
                log.info("← {} (status={}) {}ms", sig, re.getStatusCode().value(), took);
            } else {
                log.info("← {} {}ms", sig, took);
            }
            return r;
        } catch (Exception e) {
            log.error("× {} : {}", sig, e.toString());
            throw e;
        }
    }
}
