package ra.demo.controller;

// Lombok tự tạo constructor chứa các field final
import lombok.RequiredArgsConstructor;

// Đối tượng phân trang của Spring Data
import org.springframework.data.domain.Page;

// Tạo cấu hình phân trang
import org.springframework.data.domain.PageRequest;

// Hỗ trợ sắp xếp dữ liệu
import org.springframework.data.domain.Sort;

// Đại diện HTTP Response
import org.springframework.http.ResponseEntity;

// Các annotation REST Controller
import org.springframework.web.bind.annotation.*;

// Response chuẩn của hệ thống
import ra.demo.model.dto.response.ApiDataResponse;

// DTO chứa thông tin hồ sơ bệnh án
import ra.demo.model.dto.response.MedicalRecordResponse;

// Service xử lý nghiệp vụ Medical Record
import ra.demo.service.MedicalRecordService;

// Đánh dấu đây là REST API Controller
@RestController

// URL gốc của controller
@RequestMapping("/api/v1/admin/records")

// Tự động tạo constructor cho các field final
@RequiredArgsConstructor
public class AdminMedicalRecordController {

    // Inject MedicalRecordService
    private final MedicalRecordService service;

    // API lấy danh sách hồ sơ bệnh án
    // GET /api/v1/admin/records
    @GetMapping
    public ResponseEntity<ApiDataResponse<Page<MedicalRecordResponse>>> list(

            // Trang hiện tại (mặc định = 0)
            @RequestParam(defaultValue = "0")
            int page,

            // Số lượng bản ghi mỗi trang (mặc định = 5)
            @RequestParam(defaultValue = "5")
            int size) {

        return ResponseEntity.ok(

                // Tạo response thành công
                ApiDataResponse.success(

                        // Thông báo trả về
                        "Get medical records successfully",

                        // Gọi service lấy danh sách hồ sơ
                        service.findAll(

                                // Tạo thông tin phân trang
                                PageRequest.of(

                                        // Trang hiện tại
                                        page,

                                        // Kích thước trang
                                        size,

                                        // Sắp xếp theo createdAt giảm dần
                                        Sort.by("createdAt")
                                                .descending()
                                )
                        ),

                        // HTTP Status Code
                        200
                )
        );
    }

    // API xem chi tiết hồ sơ bệnh án
    // GET /api/v1/admin/records/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ApiDataResponse<MedicalRecordResponse>> detail(

            // Lấy id từ URL
            @PathVariable Long id) {

        return ResponseEntity.ok(

                // Tạo response thành công
                ApiDataResponse.success(

                        // Thông báo
                        "Get medical record successfully",

                        // Gọi service tìm hồ sơ theo id
                        service.findById(id),

                        // HTTP Status
                        200
                )
        );
    }
}