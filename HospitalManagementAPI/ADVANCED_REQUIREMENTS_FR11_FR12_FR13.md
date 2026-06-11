# Bổ sung chức năng nâng cao theo bảng điểm

## FR-11 - Ghi log thời gian thực hiện cho tất cả chức năng

Đã bổ sung `LoggingAspect` dùng `@Around("execution(* ra.demo.service..*(..))")` để đo thời gian chạy cho toàn bộ method trong tầng Service.

Log mẫu:

```text
[AOP][TIME] UserServiceImpl.create(..) executed in 25 ms
[AOP][TIME] AppointmentServiceImpl.create(..) executed in 12 ms
[AOP][TIME][FAILED] MedicalRecordServiceImpl.upload(..) failed after 40 ms
```

Vẫn giữ các log nghiệp vụ bằng `@AfterReturning` và log lỗi bằng `@AfterThrowing`.

## FR-12 - Unit Test tối thiểu 10 test

Đã bổ sung 14 test case:

- `UserServiceImplTest`: 4 test
- `AppointmentServiceImplTest`: 4 test
- `MedicalRecordServiceImplTest`: 2 test
- `AdminUserControllerTest`: 3 test
- `AuthControllerTest`: 2 test
- `PatientAppointmentControllerTest`: 1 test

Các test dùng JUnit 5, Mockito, Spring Boot Test dependency.

Chạy test:

```bash
./gradlew test
```

## FR-13 - Redis thay TokenBlacklist Database

Đã thay cơ chế blacklist token từ MySQL sang Redis:

- Thêm dependency: `spring-boot-starter-data-redis`
- Thêm service: `TokenBlacklistService`
- `AuthServiceImpl.logout()` lưu access token vào Redis với TTL theo thời gian hết hạn JWT
- `JwtAuthFilter` kiểm tra token blacklist bằng Redis trước khi xác thực request
- Đã bỏ sử dụng `TokenBlacklistRepository` và entity `TokenBlacklist`

Chạy Redis nhanh bằng Docker:

```bash
docker compose -f docker-compose.redis.yml up -d
```

Cấu hình Redis trong `application.properties`:

```properties
spring.data.redis.host=${REDIS_HOST:localhost}
spring.data.redis.port=${REDIS_PORT:6379}
spring.data.redis.timeout=2000ms
```
