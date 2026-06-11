package ra.demo.controller;

// Kích hoạt validation cho dữ liệu request
import jakarta.validation.Valid;

// Lombok tự sinh constructor cho các field final
import lombok.RequiredArgsConstructor;

// Đối tượng phân trang của Spring Data
import org.springframework.data.domain.Page;

// Tạo cấu hình phân trang
import org.springframework.data.domain.PageRequest;

// Hỗ trợ sắp xếp dữ liệu
import org.springframework.data.domain.Sort;

// Chứa các HTTP Status
import org.springframework.http.HttpStatus;

// Đại diện HTTP Response
import org.springframework.http.ResponseEntity;

// Chứa thông tin người dùng đang đăng nhập
import org.springframework.security.core.Authentication;

// Các annotation REST API
import org.springframework.web.bind.annotation.*;

// DTO tạo lịch hẹn
import ra.demo.model.dto.request.AppointmentRequest;

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

// URL gốc dành cho bệnh nhân
@RequestMapping("/api/v1/patient")

// Lombok tự tạo constructor chứa các field final
@RequiredArgsConstructor
public class PatientAppointmentController {

    // Service quản lý lịch hẹn
    private final AppointmentService appointmentService;

    // Service quản lý hồ sơ bệnh án
    private final MedicalRecordService medicalRecordService;

    // API đặt lịch khám
    // POST /api/v1/patient/appointments
    @PostMapping("/appointments")
    public ResponseEntity<ApiDataResponse<AppointmentResponse>> create(

            // Validate dữ liệu request
            @Valid

            // Nhận dữ liệu JSON từ client
            @RequestBody AppointmentRequest r,

            // Thông tin bệnh nhân đang đăng nhập
            Authentication a) {

        return ResponseEntity

                // Trả về HTTP 201 Created
                .status(HttpStatus.CREATED)

                .body(

                        ApiDataResponse.success(

                                // Thông báo thành công
                                "Create appointment successfully",

                                // Gọi service tạo lịch hẹn
                                appointmentService.create(
                                        r,
                                        a.getName()
                                ),

                                // HTTP Status
                                201
                        )
                );
    }

    // API xem lịch sử lịch hẹn
    // GET /api/v1/patient/appointments
    @GetMapping("/appointments")
    public ResponseEntity<ApiDataResponse<Page<AppointmentResponse>>> history(

            // Thông tin bệnh nhân đang đăng nhập
            Authentication a,

            // Trang hiện tại
            @RequestParam(defaultValue = "0")
            int page,

            // Số bản ghi mỗi trang
            @RequestParam(defaultValue = "5")
            int size) {

        return ResponseEntity.ok(

                ApiDataResponse.success(

                        // Thông báo
                        "Get appointment history successfully",

                        // Lấy lịch sử lịch hẹn của bệnh nhân
                        appointmentService.patientHistory(

                                // Username hiện tại
                                a.getName(),

                                // Cấu hình phân trang
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

    // API xem lịch sử hồ sơ bệnh án
    // GET /api/v1/patient/records
    @GetMapping("/records")
    public ResponseEntity<ApiDataResponse<Page<MedicalRecordResponse>>> records(

            // Thông tin bệnh nhân đang đăng nhập
            Authentication a,

            // Trang hiện tại
            @RequestParam(defaultValue = "0")
            int page,

            // Số lượng bản ghi mỗi trang
            @RequestParam(defaultValue = "5")
            int size) {

        return ResponseEntity.ok(

                ApiDataResponse.success(

                        // Thông báo
                        "Get medical record history successfully",

                        // Lấy danh sách hồ sơ bệnh án của bệnh nhân
                        medicalRecordService.patientRecords(

                                // Username hiện tại
                                a.getName(),

                                // Cấu hình phân trang
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