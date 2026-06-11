package ra.demo.controller;

// Kiểm tra dữ liệu đầu vào bằng annotation validation
import jakarta.validation.Valid;

// Lombok tự sinh constructor chứa các thuộc tính final
import lombok.RequiredArgsConstructor;

// Hỗ trợ phân trang dữ liệu
import org.springframework.data.domain.Page;

// Tạo đối tượng phân trang
import org.springframework.data.domain.PageRequest;

// Hỗ trợ sắp xếp dữ liệu
import org.springframework.data.domain.Sort;

// Đại diện cho HTTP Response
import org.springframework.http.ResponseEntity;

// Các annotation REST API
import org.springframework.web.bind.annotation.*;

// DTO nhận request cập nhật trạng thái lịch hẹn
import ra.demo.model.dto.request.AppointmentStatusRequest;

// DTO response chuẩn của hệ thống
import ra.demo.model.dto.response.ApiDataResponse;

// DTO trả về thông tin lịch hẹn
import ra.demo.model.dto.response.AppointmentResponse;

// Service xử lý nghiệp vụ lịch hẹn
import ra.demo.service.AppointmentService;

// Đánh dấu đây là REST Controller
@RestController

// URL gốc của controller
@RequestMapping("/api/v1/admin/appointments")

// Tự động sinh constructor cho các field final
@RequiredArgsConstructor
public class AdminAppointmentController {

    // Inject AppointmentService
    private final AppointmentService service;

    // API lấy danh sách lịch hẹn
    // GET /api/v1/admin/appointments
    @GetMapping
    public ResponseEntity<ApiDataResponse<Page<AppointmentResponse>>> list(

            // Trang hiện tại, mặc định = 0
            @RequestParam(defaultValue = "0") int page,

            // Số phần tử mỗi trang, mặc định = 5
            @RequestParam(defaultValue = "5") int size) {

        return ResponseEntity.ok(

                // Tạo response thành công
                ApiDataResponse.success(

                        // Thông báo
                        "Get appointments successfully",

                        // Gọi service lấy danh sách lịch hẹn
                        service.findAll(

                                // Tạo cấu hình phân trang
                                PageRequest.of(

                                        // Trang hiện tại
                                        page,

                                        // Kích thước trang
                                        size,

                                        // Sắp xếp theo appointmentTime giảm dần
                                        Sort.by("appointmentTime")
                                                .descending()
                                )
                        ),

                        // Mã trạng thái
                        200
                )
        );
    }

    // API cập nhật trạng thái lịch hẹn
    // PUT /api/v1/admin/appointments/{id}/status
    @PutMapping("/{id}/status")
    public ResponseEntity<ApiDataResponse<AppointmentResponse>> status(

            // Lấy id từ URL
            @PathVariable Long id,

            // Validate request body
            @Valid

            // Nhận dữ liệu JSON từ request
            @RequestBody AppointmentStatusRequest r) {

        return ResponseEntity.ok(

                // Response thành công
                ApiDataResponse.success(

                        // Thông báo
                        "Update appointment status successfully",

                        // Gọi service cập nhật trạng thái
                        service.updateStatus(id, r),

                        // HTTP Status
                        200
                )
        );
    }
}