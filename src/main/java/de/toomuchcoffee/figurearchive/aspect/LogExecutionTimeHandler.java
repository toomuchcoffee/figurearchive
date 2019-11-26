package de.toomuchcoffee.figurearchive.aspect;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Aspect
@Component
public class LogExecutionTimeHandler {

    @Around("@annotation(de.toomuchcoffee.figurearchive.aspect.LogExecutionTime)")
    public Object aroundLoggable(ProceedingJoinPoint joinPoint) throws Throwable {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        Object proceed = joinPoint.proceed();

        stopWatch.stop();

        log.info(stopWatch.getTime(TimeUnit.MILLISECONDS) + "ms for " + joinPoint.getSignature());
        return proceed;
    }
}