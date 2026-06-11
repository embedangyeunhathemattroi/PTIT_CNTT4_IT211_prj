# FIX SUMMARY - HospitalManagementAPI

## Các điểm đã sửa theo SRS

1. **UC-05 Cloud Storage**
   - Thay `CloudStorageServiceImpl` từ lưu local disk sang upload Cloudinary bằng Official SDK.
   - Thêm dependency `com.cloudinary:cloudinary-http44:1.39.0` trong `build.gradle`.
   - Thêm cấu hình môi trường trong `application.properties`:
     - `cloudinary.cloud-name`
     - `cloudinary.api-key`
     - `cloudinary.api-secret`
     - `cloudinary.folder`

2. **FR-09 / UC-05 HTTP Status**
   - Giữ `POST /api/v1/doctor/records/upload` trả `200 OK` theo đúng bước 7 của UC-05 trong SRS.
   - Body `ApiDataResponse.status` giữ `200`.

3. **409 Conflict khi trùng username/email**
   - `UserServiceImpl.create()` ném `ConflictException` khi username/email đã tồn tại.
   - `UserServiceImpl.update()` ném `ConflictException` khi email đã tồn tại.
   - `AuthServiceImpl.register()` ném `ConflictException` khi username/email đã tồn tại.
   - `ApiAdviceController` đã có handler map `ConflictException` sang HTTP `409 Conflict`.

4. **AOP Logging mở rộng**
   - Giữ log tạo appointment.
   - Thêm `@AfterReturning` cho upload medical record.
   - Thêm log login thành công.
   - Thêm log tạo user và deactivate user.
   - Giữ `@AfterThrowing` bắt exception toàn bộ service.

5. **File size validation 10MB**
   - Đã có trong `application.properties`:
     - `spring.servlet.multipart.max-file-size=10MB`
     - `spring.servlet.multipart.max-request-size=10MB`
   - Đã có validate code trong `MedicalRecordServiceImpl.validateFile()`.

## Lưu ý khi chạy thật

Trước khi test API upload Cloudinary, cần cấu hình biến môi trường hoặc điền trực tiếp trong `application.properties`:

```properties
cloudinary.cloud-name=your_cloud_name
cloudinary.api-key=your_api_key
cloudinary.api-secret=your_api_secret
cloudinary.folder=hospital-management/medical-records
```

Nếu để trống Cloudinary credentials, API upload sẽ trả lỗi storage/cloud upload failed.


## Bổ sung sau khi rà soát lần cuối

1. **Inactive account trả 403**
   - Thêm `ForbiddenException`.
   - `AuthServiceImpl.login()` và `refresh()` ném `ForbiddenException` khi tài khoản inactive.
   - `ApiAdviceController` map `ForbiddenException` về HTTP `403 Forbidden`.

2. **Doctor ownership check khi duyệt/từ chối lịch**
   - Thêm `AppointmentService.updateStatusByDoctor(id, request, doctorUsername)`.
   - Doctor chỉ được cập nhật appointment của chính mình.
   - Nếu cố cập nhật lịch của bác sĩ khác, hệ thống trả `403 Forbidden`.

3. **MedicalRecordResponse đã có fileUrl**
   - `MedicalRecordResponse` có trường `fileUrl`.
   - `ResponseMapper.toRecord()` trả `fileUrl`, nên Patient xem hồ sơ bệnh án sẽ thấy link file cloud.
