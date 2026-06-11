package ra.demo.service;

// Import tất cả DTO Request:
// LoginRequest, RegisterRequest,
// RefreshTokenRequest, ChangePasswordRequest,
// ForgotPasswordRequest, ResetPasswordRequest
import ra.demo.model.dto.request.*;

// DTO trả về khi quên mật khẩu
import ra.demo.model.dto.response.ForgotPasswordResponse;

// DTO chứa Access Token và Refresh Token
import ra.demo.model.dto.response.JwtResponse;

// Interface định nghĩa các chức năng xác thực người dùng
public interface AuthService {

    // Đăng nhập hệ thống
    // Nhận username/password
    // Trả về access token + refresh token
    JwtResponse login(LoginRequest request);

    // Đăng ký tài khoản bệnh nhân mới
    // Sau khi đăng ký sẽ trả JWT
    JwtResponse registerPatient(RegisterRequest request);

    // Làm mới Access Token bằng Refresh Token
    JwtResponse refresh(RefreshTokenRequest request);

    // Đăng xuất hệ thống
    // authorizationHeader chứa Access Token
    // refreshToken dùng để xóa Refresh Token trong DB
    void logout(String authorizationHeader,
                String refreshToken);

    // Đổi mật khẩu khi đã đăng nhập
    // username lấy từ SecurityContext
    // request chứa mật khẩu cũ và mới
    void changePassword(String username,
                        ChangePasswordRequest request);

    // Quên mật khẩu
    // Tạo Reset Token để reset password
    ForgotPasswordResponse forgotPassword(
            ForgotPasswordRequest request);

    // Đặt lại mật khẩu bằng Reset Token
    void resetPassword(
            ResetPasswordRequest request);
}