package ra.demo.aspect;

// Lombok tự tạo biến log để ghi log
import lombok.extern.slf4j.Slf4j;

// Chứa thông tin về method đang được gọi
import org.aspectj.lang.JoinPoint;

// Dùng cho @Around để can thiệp trước và sau khi method chạy
import org.aspectj.lang.ProceedingJoinPoint;

// Chạy sau khi method thực thi thành công và trả về kết quả
import org.aspectj.lang.annotation.AfterReturning;

// Chạy khi method phát sinh exception
import org.aspectj.lang.annotation.AfterThrowing;

// Chạy bao quanh method (trước và sau)
import org.aspectj.lang.annotation.Around;

// Đánh dấu đây là Aspect
import org.aspectj.lang.annotation.Aspect;

// Đăng ký bean vào Spring Container
import org.springframework.stereotype.Component;

// Khai báo lớp Aspect
@Aspect

// Spring quản lý bean này
@Component

// Lombok sinh ra:
// private static final Logger log = LoggerFactory.getLogger(...)
@Slf4j
public class LoggingAspect {

    // Áp dụng cho tất cả method trong package service và các package con
    @Around("execution(* ra.demo.service..*(..))")
    public Object logExecutionTime(ProceedingJoinPoint pjp) throws Throwable {

        // Lưu thời gian bắt đầu thực thi method
        long start = System.currentTimeMillis();

        try {

            // Thực hiện method gốc
            Object result = pjp.proceed();

            // Tính thời gian thực thi
            long duration = System.currentTimeMillis() - start;

            // Ghi log thời gian chạy
            log.info(
                    "[AOP][TIME] {} executed in {} ms",
                    pjp.getSignature().toShortString(),
                    duration
            );

            // Trả về kết quả của method
            return result;

        } catch (Throwable ex) {

            // Tính thời gian trước khi lỗi xảy ra
            long duration = System.currentTimeMillis() - start;

            // Ghi log lỗi cùng thời gian thực thi
            log.error(
                    "[AOP][TIME][FAILED] {} failed after {} ms",
                    pjp.getSignature().toShortString(),
                    duration
            );

            // Ném lại exception để hệ thống xử lý tiếp
            throw ex;
        }
    }

    // Chạy sau khi tạo lịch hẹn thành công
    @AfterReturning(
            pointcut = "execution(* ra.demo.service.impl.AppointmentServiceImpl.create(..))",
            returning = "result"
    )
    public void logAppointmentCreated(
            JoinPoint jp,
            Object result
    ) {

        // Ghi log người tạo và kết quả trả về
        log.info(
                "[AOP][SUCCESS] Appointment created by {} -> {}",

                // Nếu có tham số thứ 2 thì lấy làm người tạo
                jp.getArgs().length > 1
                        ? jp.getArgs()[1]
                        : "unknown",

                result
        );
    }

    // Chạy sau khi upload hồ sơ bệnh án thành công
    @AfterReturning(
            pointcut = "execution(* ra.demo.service.impl.MedicalRecordServiceImpl.upload(..))",
            returning = "result"
    )
    public void logMedicalRecordUploaded(
            JoinPoint jp,
            Object result
    ) {

        // Ghi log bác sĩ upload và dữ liệu trả về
        log.info(
                "[AOP][SUCCESS] Medical record uploaded by doctor={} -> {}",

                // Nếu tồn tại tham số thứ 3 thì lấy làm doctor
                jp.getArgs().length > 2
                        ? jp.getArgs()[2]
                        : "unknown",

                result
        );
    }

    // Chạy sau khi đăng nhập thành công
    @AfterReturning(
            pointcut = "execution(* ra.demo.service.impl.AuthServiceImpl.login(..))",
            returning = "result"
    )
    public void logLoginSuccess(
            JoinPoint jp,
            Object result
    ) {

        // Ghi log login thành công
        log.info("[AOP][SUCCESS] Login completed");
    }

    // Chạy sau khi tạo user thành công
    @AfterReturning(
            pointcut = "execution(* ra.demo.service.impl.UserServiceImpl.create(..))",
            returning = "result"
    )
    public void logUserCreated(
            JoinPoint jp,
            Object result
    ) {

        // Ghi log thông tin user vừa tạo
        log.info(
                "[AOP][SUCCESS] User created -> {}",
                result
        );
    }

    // Chạy sau khi vô hiệu hóa user thành công
    @AfterReturning(
            pointcut = "execution(* ra.demo.service.impl.UserServiceImpl.deactivate(..))"
    )
    public void logUserDeactivated(
            JoinPoint jp
    ) {

        // Ghi log id user bị khóa
        log.info(
                "[AOP][SUCCESS] User deactivated id={}",

                // Lấy tham số đầu tiên (id)
                jp.getArgs().length > 0
                        ? jp.getArgs()[0]
                        : "unknown"
        );
    }

    // Bắt tất cả exception phát sinh trong service
    @AfterThrowing(
            pointcut = "execution(* ra.demo.service..*(..))",
            throwing = "ex"
    )
    public void logError(
            JoinPoint jp,
            Throwable ex
    ) {

        // Ghi log tên method và nội dung lỗi
        log.error(
                "[AOP][ERROR] Exception at {}: {}",
                jp.getSignature().toShortString(),
                ex.getMessage()
        );
    }
}