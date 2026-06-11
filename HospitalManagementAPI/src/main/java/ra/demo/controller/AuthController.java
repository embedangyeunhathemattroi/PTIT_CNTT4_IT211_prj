package ra.demo.controller;

// Lấy thông tin request HTTP hiện tại
import jakarta.servlet.http.HttpServletRequest;

// Kích hoạt validation cho dữ liệu request
import jakarta.validation.Valid;

// Lombok tự sinh constructor cho các field final
import lombok.RequiredArgsConstructor;

// Chứa các HTTP Status (200, 201, 404,...)
import org.springframework.http.HttpStatus;

// Đại diện HTTP Response
import org.springframework.http.ResponseEntity;

// Chứa thông tin user đang đăng nhập
import org.springframework.security.core.Authentication;

// Annotation ánh xạ API POST
import org.springframework.web.bind.annotation.PostMapping;

// Lấy dữ liệu JSON từ request body
import org.springframework.web.bind.annotation.RequestBody;

// Khai báo URL gốc cho controller
import org.springframework.web.bind.annotation.RequestMapping;

// Đánh dấu đây là REST Controller
import org.springframework.web.bind.annotation.RestController;

// Import tất cả DTO request
import ra.demo.model.dto.request.*;

// Response chuẩn của hệ thống
import ra.demo.model.dto.response.ApiDataResponse;

// DTO trả về khi quên mật khẩu
import ra.demo.model.dto.response.ForgotPasswordResponse;

// DTO chứa access token và refresh token
import ra.demo.model.dto.response.JwtResponse;

// Service xử lý nghiệp vụ xác thực
import ra.demo.service.AuthService;

// Đánh dấu đây là REST API Controller
@RestController

// URL gốc của controller
@RequestMapping("/api/v1/auth")

// Lombok tự tạo constructor cho field final
@RequiredArgsConstructor
public class AuthController {

    // Inject AuthService
    private final AuthService authService;

    // API đăng nhập
    // POST /api/v1/auth/login
    @PostMapping("/login")
    public ResponseEntity<ApiDataResponse<JwtResponse>> login(

            // Validate dữ liệu request
            @Valid

            // Nhận JSON từ body
            @RequestBody LoginRequest r) {

        return ResponseEntity.ok(

                ApiDataResponse.success(

                        // Thông báo thành công
                        "Login successfully",

                        // Gọi service đăng nhập
                        authService.login(r),

                        // HTTP Status
                        200
                )
        );
    }

    // API đăng ký tài khoản bệnh nhân
    // POST /api/v1/auth/register
    @PostMapping("/register")
    public ResponseEntity<ApiDataResponse<JwtResponse>> register(

            // Validate request
            @Valid

            // Nhận dữ liệu từ body
            @RequestBody RegisterRequest r) {

        return ResponseEntity

                // HTTP 201 Created
                .status(HttpStatus.CREATED)

                .body(

                        ApiDataResponse.success(

                                // Thông báo
                                "Register patient successfully",

                                // Gọi service đăng ký
                                authService.registerPatient(r),

                                // Status Code
                                201
                        )
                );
    }

    // API refresh token
    // POST /api/v1/auth/refresh
    @PostMapping("/refresh")
    public ResponseEntity<ApiDataResponse<JwtResponse>> refresh(

            // Validate request
            @Valid

            // Nhận refresh token từ body
            @RequestBody RefreshTokenRequest r) {

        return ResponseEntity.ok(

                ApiDataResponse.success(

                        // Thông báo
                        "Refresh token successfully",

                        // Sinh access token mới
                        authService.refresh(r),

                        // HTTP Status
                        200
                )
        );
    }

    // API đăng xuất
    // POST /api/v1/auth/logout
    @PostMapping("/logout")
    public ResponseEntity<ApiDataResponse<Void>> logout(

            // Lấy request hiện tại
            HttpServletRequest request,

            // Refresh token có thể có hoặc không
            @RequestBody(required = false)
            RefreshTokenRequest refresh) {

        // Gọi service logout
        authService.logout(

                // Lấy access token từ header Authorization
                request.getHeader("Authorization"),

                // Nếu body null thì truyền null
                refresh == null
                        ? null
                        : refresh.getRefreshToken()
        );

        return ResponseEntity.ok(

                ApiDataResponse.success(

                        // Thông báo
                        "Logout successfully",

                        // Không trả dữ liệu
                        null,

                        // HTTP Status
                        200
                )
        );
    }

    // API đổi mật khẩu
    // POST /api/v1/auth/change-password
    @PostMapping("/change-password")
    public ResponseEntity<ApiDataResponse<Void>> changePassword(

            // Thông tin user đang đăng nhập
            Authentication a,

            // Validate request
            @Valid

            // Nhận dữ liệu từ body
            @RequestBody ChangePasswordRequest r) {

        // Lấy username từ Authentication
        authService.changePassword(
                a.getName(),
                r
        );

        return ResponseEntity.ok(

                ApiDataResponse.success(

                        // Thông báo
                        "Change password successfully",

                        null,

                        200
                )
        );
    }

    // API quên mật khẩu
    // POST /api/v1/auth/forgot-password
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiDataResponse<ForgotPasswordResponse>> forgot(

            // Validate request
            @Valid

            // Nhận email hoặc username
            @RequestBody ForgotPasswordRequest r) {

        return ResponseEntity.ok(

                ApiDataResponse.success(

                        // Thông báo
                        "Create reset password token successfully",

                        // Tạo token reset password
                        authService.forgotPassword(r),

                        200
                )
        );
    }

    // API đặt lại mật khẩu
    // POST /api/v1/auth/reset-password
    @PostMapping("/reset-password")
    public ResponseEntity<ApiDataResponse<Void>> reset(

            // Validate request
            @Valid

            // Nhận token reset + mật khẩu mới
            @RequestBody ResetPasswordRequest r) {

        // Thực hiện reset password
        authService.resetPassword(r);

        return ResponseEntity.ok(

                ApiDataResponse.success(

                        // Thông báo
                        "Reset password successfully",

                        null,

                        200
                )
        );
    }
}