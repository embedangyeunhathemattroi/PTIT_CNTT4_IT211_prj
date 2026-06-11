package ra.demo.controller;

// Kích hoạt validation cho dữ liệu đầu vào
import jakarta.validation.Valid;

// Lombok tự tạo constructor chứa các field final
import lombok.RequiredArgsConstructor;

// Đối tượng Page dùng cho phân trang
import org.springframework.data.domain.Page;

// Tạo thông tin phân trang
import org.springframework.data.domain.PageRequest;

// Hỗ trợ sắp xếp dữ liệu
import org.springframework.data.domain.Sort;

// Khai báo kiểu dữ liệu Media Type
import org.springframework.http.MediaType;

// Đại diện HTTP Response
import org.springframework.http.ResponseEntity;

// Chứa thông tin user đang đăng nhập
import org.springframework.security.core.Authentication;

// Các annotation REST API
import org.springframework.web.bind.annotation.*;

// Đại diện file upload từ client
import org.springframework.web.multipart.MultipartFile;

// DTO cập nhật trạng thái lịch hẹn
import ra.demo.model.dto.request.AppointmentStatusRequest;

// DTO upload hồ sơ bệnh án
import ra.demo.model.dto.request.MedicalRecordRequest;

// Response chuẩn của hệ thống
import ra.demo.model.dto.response.ApiDataResponse;

// DTO thông tin lịch hẹn
import ra.demo.model.dto.response.AppointmentResponse;

// DTO thông tin hồ sơ bệnh án
import ra.demo.model.dto.response.MedicalRecordResponse;

// Service xử lý nghiệp vụ lịch hẹn
import ra.demo.service.AppointmentService;

// Service xử lý nghiệp vụ hồ sơ bệnh án
import ra.demo.service.MedicalRecordService;

// Đánh dấu đây là REST Controller
@RestController

// URL gốc cho bác sĩ
@RequestMapping("/api/v1/doctor")

// Lombok tự sinh constructor
@RequiredArgsConstructor
public class DoctorAppointmentController {

    // Service quản lý lịch hẹn
    private final AppointmentService appointmentService;

    // Service quản lý hồ sơ bệnh án
    private final MedicalRecordService medicalRecordService;

    // API lấy danh sách lịch hẹn của bác sĩ đang đăng nhập
    // GET /api/v1/doctor/appointments
    @GetMapping("/appointments")
    public ResponseEntity<ApiDataResponse<Page<AppointmentResponse>>> list(

            // Thông tin user đăng nhập
            Authentication a,

            // Trang hiện tại
            @RequestParam(defaultValue = "0")
            int page,

            // Số phần tử mỗi trang
            @RequestParam(defaultValue = "5")
            int size) {

        return ResponseEntity.ok(

                ApiDataResponse.success(

                        // Thông báo
                        "Get doctor appointments successfully",

                        // Lấy danh sách lịch hẹn của bác sĩ
                        appointmentService.doctorAppointments(

                                // Username bác sĩ
                                a.getName(),

                                // Phân trang + sắp xếp
                                PageRequest.of(
                                        page,
                                        size,
                                        Sort.by("appointmentTime")
                                                .descending()
                                )
                        ),

                        // HTTP Status
                        200
                )
        );
    }

    // API cập nhật trạng thái lịch hẹn
    // PUT /api/v1/doctor/appointments/{id}/status
    @PutMapping("/appointments/{id}/status")
    public ResponseEntity<ApiDataResponse<AppointmentResponse>> status(

            // ID lịch hẹn
            @PathVariable Long id,

            // Validate request body
            @Valid

            // Dữ liệu trạng thái mới
            @RequestBody AppointmentStatusRequest r,

            // Thông tin bác sĩ đăng nhập
            Authentication a) {

        return ResponseEntity.ok(

                ApiDataResponse.success(

                        // Thông báo
                        "Update appointment status successfully",

                        // Bác sĩ cập nhật trạng thái lịch hẹn
                        appointmentService.updateStatusByDoctor(
                                id,
                                r,
                                a.getName()
                        ),

                        // HTTP Status
                        200
                )
        );
    }

    // API upload hồ sơ bệnh án
    // POST /api/v1/doctor/records/upload
    @PostMapping(
            value = "/records/upload",

            // Chỉ nhận multipart/form-data
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ApiDataResponse<MedicalRecordResponse>> upload(

            // Validate dữ liệu form
            @Valid

            // Nhận dữ liệu từ form-data
            @ModelAttribute MedicalRecordRequest r,

            // Nhận file upload
            @RequestPart("file")
            MultipartFile file,

            // Thông tin bác sĩ đăng nhập
            Authentication a) {

        return ResponseEntity.ok(

                ApiDataResponse.success(

                        // Thông báo
                        "Upload medical record successfully",

                        // Upload hồ sơ bệnh án
                        medicalRecordService.upload(
                                r,
                                file,
                                a.getName()
                        ),

                        // HTTP Status
                        200
                )
        );
    }

    // API lấy danh sách hồ sơ bệnh án do bác sĩ tạo
    // GET /api/v1/doctor/records
    @GetMapping("/records")
    public ResponseEntity<ApiDataResponse<Page<MedicalRecordResponse>>> records(

            // Thông tin bác sĩ đăng nhập
            Authentication a,

            // Trang hiện tại
            @RequestParam(defaultValue = "0")
            int page,

            // Số phần tử mỗi trang
            @RequestParam(defaultValue = "5")
            int size) {

        return ResponseEntity.ok(

                ApiDataResponse.success(

                        // Thông báo
                        "Get doctor records successfully",

                        // Lấy danh sách hồ sơ của bác sĩ
                        medicalRecordService.doctorRecords(

                                // Username bác sĩ
                                a.getName(),

                                // Phân trang và sắp xếp
                                PageRequest.of(
                                        page,
                                        size,
                                        Sort.by("createdAt")
                                                .descending()
                                )
                        ),

                        // HTTP Status
                        200
                )
        );
    }
}