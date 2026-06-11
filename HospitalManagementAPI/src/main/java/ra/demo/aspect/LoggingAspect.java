package ra.demo.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Around("execution(* ra.demo.service..*(..))")
    public Object logExecutionTime(ProceedingJoinPoint pjp) throws Throwable {
        long start = System.currentTimeMillis();
        try {
            Object result = pjp.proceed();
            long duration = System.currentTimeMillis() - start;
            log.info("[AOP][TIME] {} executed in {} ms", pjp.getSignature().toShortString(), duration);
            return result;
        } catch (Throwable ex) {
            long duration = System.currentTimeMillis() - start;
            log.error("[AOP][TIME][FAILED] {} failed after {} ms", pjp.getSignature().toShortString(), duration);
            throw ex;
        }
    }

    @AfterReturning(pointcut = "execution(* ra.demo.service.impl.AppointmentServiceImpl.create(..))", returning = "result")
    public void logAppointmentCreated(JoinPoint jp, Object result) {
        log.info("[AOP][SUCCESS] Appointment created by {} -> {}", jp.getArgs().length > 1 ? jp.getArgs()[1] : "unknown", result);
    }

    @AfterReturning(pointcut = "execution(* ra.demo.service.impl.MedicalRecordServiceImpl.upload(..))", returning = "result")
    public void logMedicalRecordUploaded(JoinPoint jp, Object result) {
        log.info("[AOP][SUCCESS] Medical record uploaded by doctor={} -> {}", jp.getArgs().length > 2 ? jp.getArgs()[2] : "unknown", result);
    }

    @AfterReturning(pointcut = "execution(* ra.demo.service.impl.AuthServiceImpl.login(..))", returning = "result")
    public void logLoginSuccess(JoinPoint jp, Object result) {
        log.info("[AOP][SUCCESS] Login completed");
    }

    @AfterReturning(pointcut = "execution(* ra.demo.service.impl.UserServiceImpl.create(..))", returning = "result")
    public void logUserCreated(JoinPoint jp, Object result) {
        log.info("[AOP][SUCCESS] User created -> {}", result);
    }

    @AfterReturning(pointcut = "execution(* ra.demo.service.impl.UserServiceImpl.deactivate(..))")
    public void logUserDeactivated(JoinPoint jp) {
        log.info("[AOP][SUCCESS] User deactivated id={}", jp.getArgs().length > 0 ? jp.getArgs()[0] : "unknown");
    }

    @AfterThrowing(pointcut = "execution(* ra.demo.service..*(..))", throwing = "ex")
    public void logError(JoinPoint jp, Throwable ex) {
        log.error("[AOP][ERROR] Exception at {}: {}", jp.getSignature().toShortString(), ex.getMessage());
    }
}
