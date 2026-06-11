package ra.demo.controller;

// Kích hoạt validation cho dữ liệu request
import jakarta.validation.Valid;

// Lombok tự tạo constructor cho các field final
import lombok.RequiredArgsConstructor;

// Đối tượng phân trang của Spring Data
import org.springframework.data.domain.Page;

// Tạo thông tin phân trang
import org.springframework.data.domain.PageRequest;

// Hỗ trợ sắp xếp dữ liệu
import org.springframework.data.domain.Sort;

// Chứa các HTTP Status
import org.springframework.http.HttpStatus;

// Đại diện HTTP Response
import org.springframework.http.ResponseEntity;

// Các annotation REST API
import org.springframework.web.bind.annotation.*;

// DTO nhận dữ liệu tạo/cập nhật User
import ra.demo.model.dto.request.UserRequest;

// DTO response chuẩn của hệ thống
import ra.demo.model.dto.response.ApiDataResponse;

// DTO trả về thông tin User
import ra.demo.model.dto.response.UserResponse;

// Service xử lý nghiệp vụ User
import ra.demo.service.UserService;

// Đánh dấu đây là REST Controller
@RestController

// URL gốc của controller
@RequestMapping("/api/v1/admin/users")

// Lombok sinh constructor chứa field final
@RequiredArgsConstructor
public class AdminUserController {

    // Inject UserService
    private final UserService service;

    // API lấy danh sách user
    // GET /api/v1/admin/users
    @GetMapping
    public ResponseEntity<ApiDataResponse<Page<UserResponse>>> list(

            // Từ khóa tìm kiếm
            @RequestParam(defaultValue = "")
            String keyword,

            // Trang hiện tại
            @RequestParam(defaultValue = "0")
            int page,

            // Số lượng bản ghi mỗi trang
            @RequestParam(defaultValue = "5")
            int size,

            // Trường dùng để sắp xếp
            @RequestParam(defaultValue = "id")
            String sort) {

        return ResponseEntity.ok(

                // Tạo response thành công
                ApiDataResponse.success(

                        // Thông báo
                        "Get users successfully",

                        // Gọi service lấy danh sách user
                        service.findAll(

                                // Từ khóa tìm kiếm
                                keyword,

                                // Cấu hình phân trang
                                PageRequest.of(

                                        // Trang hiện tại
                                        page,

                                        // Kích thước trang
                                        size,

                                        // Sắp xếp giảm dần
                                        Sort.by(sort)
                                                .descending()
                                )
                        ),

                        // HTTP Status
                        200
                )
        );
    }

    // API lấy chi tiết user theo id
    // GET /api/v1/admin/users/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ApiDataResponse<UserResponse>> detail(

            // Lấy id từ URL
            @PathVariable Long id) {

        return ResponseEntity.ok(

                // Response thành công
                ApiDataResponse.success(

                        // Thông báo
                        "Get user successfully",

                        // Gọi service tìm user
                        service.findById(id),

                        // HTTP Status
                        200
                )
        );
    }

    // API tạo mới user
    // POST /api/v1/admin/users
    @PostMapping
    public ResponseEntity<ApiDataResponse<UserResponse>> create(

            // Validate dữ liệu request
            @Valid

            // Nhận JSON từ body
            @RequestBody UserRequest r) {

        return ResponseEntity

                // Trả về HTTP 201 Created
                .status(HttpStatus.CREATED)

                .body(

                        ApiDataResponse.success(

                                // Thông báo
                                "Create user successfully",

                                // Gọi service tạo user
                                service.create(r),

                                // HTTP Status
                                201
                        )
                );
    }

    // API cập nhật user
    // PUT /api/v1/admin/users/{id}
    @PutMapping("/{id}")
    public ResponseEntity<ApiDataResponse<UserResponse>> update(

            // Lấy id từ URL
            @PathVariable Long id,

            // Validate request body
            @Valid

            // Nhận JSON từ client
            @RequestBody UserRequest r) {

        return ResponseEntity.ok(

                ApiDataResponse.success(

                        // Thông báo
                        "Update user successfully",

                        // Gọi service cập nhật
                        service.update(id, r),

                        // HTTP Status
                        200
                )
        );
    }

    // API vô hiệu hóa user
    // DELETE /api/v1/admin/users/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(

            // Lấy id từ URL
            @PathVariable Long id) {

        // Gọi service khóa/vô hiệu hóa user
        service.deactivate(id);

        // Trả về HTTP 204 No Content
        return ResponseEntity.noContent().build();
    }
}