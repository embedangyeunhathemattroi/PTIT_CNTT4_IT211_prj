# HospitalManagementAPI

Project Java Web Service RESTful API theo SRS **Hệ thống quản lý bệnh viện**.

## Chức năng đã triển khai

- FR-01 Login JWT: `POST /api/v1/auth/login`
- FR-02 Refresh Token: `POST /api/v1/auth/refresh`
- FR-03 Logout + blacklist AccessToken + revoke RefreshToken: `POST /api/v1/auth/logout`
- FR-04 Register Patient: `POST /api/v1/auth/register`
- FR-05 Admin quản lý User CRUD, tìm kiếm, phân trang: `/api/v1/admin/users`
- FR-06 Patient đặt lịch khám: `POST /api/v1/patient/appointments`
- FR-07 Patient xem lịch sử khám và hồ sơ bệnh án: `GET /api/v1/patient/appointments`, `GET /api/v1/patient/records`
- FR-08 Admin/Doctor duyệt, từ chối, hủy, hoàn thành lịch: `PUT /appointments/{id}/status`
- FR-09 Doctor upload hồ sơ bệnh án MultipartFile: `POST /api/v1/doctor/records/upload`
- FR-10 Change Password/Forgot Password/Reset Password: `/api/v1/auth/change-password`, `/forgot-password`, `/reset-password`

## Tài khoản mẫu

- Admin: `admin / 123456`
- Doctor: `doctor / 123456`
- Patient: `patient / 123456`

## Công nghệ

Spring Boot, Spring Security, JWT, Refresh Token, Token Blacklist, Spring Data JPA, MySQL, Validation, AOP Logging, DTO Response, Global Exception Handler.

## Chạy project

1. Tạo database MySQL hoặc để `createDatabaseIfNotExist=true` tự tạo.
2. Sửa `spring.datasource.username` và `spring.datasource.password` trong `src/main/resources/application.properties`.
3. Chạy:

```bash
./gradlew bootRun
```

## Ghi chú upload cloud

SRS yêu cầu Cloudinary/AWS S3. Project đã tích hợp Cloudinary Official SDK qua `CloudStorageServiceImpl`; file upload sẽ được đẩy lên Cloudinary và lưu `secure_url` vào DB. Cần cấu hình `CLOUDINARY_CLOUD_NAME`, `CLOUDINARY_API_KEY`, `CLOUDINARY_API_SECRET` trước khi chạy upload thật.

## Chức năng nâng cao theo bảng điểm

### FR-11 - AOP log thời gian thực hiện
Project đã có `LoggingAspect` dùng `@Around` để ghi thời gian thực hiện cho toàn bộ method trong tầng Service.

### FR-12 - Unit Test
Project đã bổ sung tối thiểu 10 unit test cho Service và Controller. Chạy bằng:

```bash
./gradlew test
```

### FR-13 - Redis Token Blacklist
Project đã thay TokenBlacklist lưu DB bằng Redis. Trước khi test login/logout với blacklist token, chạy Redis:

```bash
docker compose -f docker-compose.redis.yml up -d
```
