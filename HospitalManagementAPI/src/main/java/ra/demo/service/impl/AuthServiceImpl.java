package ra.demo.service.impl;

// Lombok tạo constructor
import lombok.RequiredArgsConstructor;
// Xác thực người dùng
import org.springframework.security.authentication.AuthenticationManager;
// Token xác thực Username/Password
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// Đối tượng Authentication
import org.springframework.security.core.Authentication;
// Mã hóa mật khẩu
import org.springframework.security.crypto.password.PasswordEncoder;
// Service Bean
import org.springframework.stereotype.Service;
// Quản lý transaction
import org.springframework.transaction.annotation.Transactional;
// Exception nghiệp vụ
import ra.demo.exception.ApiException;
// Exception dữ liệu trùng
import ra.demo.exception.ConflictException;
// Exception cấm truy cập
import ra.demo.exception.ForbiddenException;
// Import DTO Request
import ra.demo.model.dto.request.*;
// Response quên mật khẩu
import ra.demo.model.dto.response.ForgotPasswordResponse;
// Response JWT
import ra.demo.model.dto.response.JwtResponse;
// Entity reset password token
import ra.demo.model.entity.PasswordResetToken;
// Entity refresh token
import ra.demo.model.entity.RefreshToken;
// Entity user
import ra.demo.model.entity.User;
// Enum role
import ra.demo.model.enums.RoleName;
// Repository reset token
import ra.demo.repository.PasswordResetTokenRepository;
// Repository refresh token
import ra.demo.repository.RefreshTokenRepository;
// Repository user
import ra.demo.repository.UserRepository;
// JWT Provider
import ra.demo.security.jwt.JwtProvider;
// UserDetails custom
import ra.demo.security.principal.CustomUserDetails;
// Interface AuthService
import ra.demo.service.AuthService;
// Service blacklist token
import ra.demo.service.token.TokenBlacklistService;

import java.time.LocalDateTime;
import java.util.UUID;

// Đăng ký Service
@Service
// Tạo constructor
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    // Xử lý xác thực login
    private final AuthenticationManager authenticationManager;

    // Xử lý JWT
    private final JwtProvider jwtProvider;

    // Repository User
    private final UserRepository userRepository;

    // Repository Refresh Token
    private final RefreshTokenRepository refreshTokenRepository;

    // Service blacklist token
    private final TokenBlacklistService tokenBlacklistService;

    // Repository Reset Password Token
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    // Mã hóa mật khẩu
    private final PasswordEncoder passwordEncoder;

    // Đăng nhập
    @Override
    public JwtResponse login(LoginRequest r) {

        // Xác thực username/password
        Authentication a = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        r.getUsername(),
                        r.getPassword()));

        // Tìm user
        User u = userRepository.findByUsername(r.getUsername())
                .orElseThrow(() -> new ApiException("User not found"));

        // Kiểm tra tài khoản hoạt động
        if (!u.isActive())
            throw new ForbiddenException("Account is inactive");

        // Sinh access token
        String access = jwtProvider.generateAccessToken(a);

        // Sinh refresh token
        String refresh = jwtProvider.generateRefreshToken(r.getUsername());

        // Lưu refresh token
        refreshTokenRepository.save(
                RefreshToken.builder()
                        .token(refresh)
                        .user(u)
                        .expiryDate(jwtProvider.expirationAsLocal(refresh))
                        .build());

        // Trả token
        return JwtResponse.builder()
                .accessToken(access)
                .refreshToken(refresh)
                .tokenType("Bearer")
                .build();
    }

    // Đăng ký bệnh nhân
    @Override
    @Transactional
    public JwtResponse registerPatient(RegisterRequest r) {

        // Kiểm tra username/email tồn tại
        if (userRepository.existsByUsername(r.getUsername())
                || userRepository.existsByEmail(r.getEmail()))
            throw new ConflictException("Username or email already exists");

        // Lưu user mới
        User u = userRepository.save(
                User.builder()
                        .username(r.getUsername())
                        .email(r.getEmail())
                        .fullName(r.getFullName())
                        .password(passwordEncoder.encode(r.getPassword()))
                        .role(RoleName.PATIENT)
                        .active(true)
                        .build());

        // Tạo request login
        LoginRequest login = new LoginRequest();
        login.setUsername(u.getUsername());
        login.setPassword(r.getPassword());

        // Đăng nhập ngay sau khi đăng ký
        return login(login);
    }

    // Refresh access token
    @Override
    public JwtResponse refresh(RefreshTokenRequest r) {

        // Tìm refresh token
        RefreshToken rt = refreshTokenRepository
                .findByToken(r.getRefreshToken())
                .orElseThrow(() -> new ApiException("Refresh token invalid"));

        // Kiểm tra hết hạn
        if (rt.getExpiryDate().isBefore(LocalDateTime.now()))
            throw new ApiException("Refresh token expired");

        // Kiểm tra tài khoản
        if (!rt.getUser().isActive())
            throw new ForbiddenException("Account is inactive");

        // Tạo UserDetails
        CustomUserDetails details =
                new CustomUserDetails(rt.getUser());

        // Sinh access token mới
        String access = jwtProvider.generateAccessToken(
                new UsernamePasswordAuthenticationToken(
                        details,
                        null,
                        details.getAuthorities()));

        // Trả token
        return JwtResponse.builder()
                .accessToken(access)
                .refreshToken(rt.getToken())
                .tokenType("Bearer")
                .build();
    }

    // Đăng xuất
    @Override
    @Transactional
    public void logout(String h, String refreshToken) {

        // Kiểm tra header token
        if (h == null || !h.startsWith("Bearer "))
            throw new ApiException("Missing token");

        // Cắt Bearer
        String token = h.substring(7);

        // Đưa access token vào blacklist
        tokenBlacklistService.blacklist(
                token,
                jwtProvider.expirationAsLocal(token));

        // Xóa refresh token
        if (refreshToken != null && !refreshToken.isBlank())
            refreshTokenRepository.deleteByToken(refreshToken);
    }

    // Đổi mật khẩu
    @Override
    @Transactional
    public void changePassword(
            String username,
            ChangePasswordRequest request) {

        // Tìm user
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException("User not found"));

        // Kiểm tra mật khẩu cũ
        if (!passwordEncoder.matches(
                request.getOldPassword(),
                user.getPassword()))

            throw new ApiException("Old password is incorrect");

        // Mã hóa mật khẩu mới
        user.setPassword(
                passwordEncoder.encode(
                        request.getNewPassword()));

        // Lưu DB
        userRepository.save(user);

        // Xóa toàn bộ refresh token
        refreshTokenRepository.deleteByUser(user);
    }

    // Quên mật khẩu
    @Override
    @Transactional
    public ForgotPasswordResponse forgotPassword(
            ForgotPasswordRequest request) {

        // Tìm user theo email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ApiException("Email not found"));

        // Sinh reset token
        String token = UUID.randomUUID().toString();

        // Lưu reset token
        passwordResetTokenRepository.save(
                PasswordResetToken.builder()
                        .token(token)
                        .user(user)
                        .expiryDate(
                                LocalDateTime.now().plusMinutes(30))
                        .used(false)
                        .build());

        // Trả kết quả
        return ForgotPasswordResponse.builder()
                .email(user.getEmail())
                .resetToken(token)
                .note("Demo project: use this resetToken to call /api/v1/auth/reset-password. In production, send it by email.")
                .build();
    }

    // Reset mật khẩu
    @Override
    @Transactional
    public void resetPassword(
            ResetPasswordRequest request) {

        // Tìm reset token
        PasswordResetToken token =
                passwordResetTokenRepository
                        .findByToken(request.getResetToken())
                        .orElseThrow(() ->
                                new ApiException("Reset token invalid"));

        // Kiểm tra đã dùng chưa
        if (token.isUsed())
            throw new ApiException("Reset token already used");

        // Kiểm tra hết hạn
        if (token.getExpiryDate().isBefore(LocalDateTime.now()))
            throw new ApiException("Reset token expired");

        // Lấy user
        User user = token.getUser();

        // Cập nhật mật khẩu mới
        user.setPassword(
                passwordEncoder.encode(
                        request.getNewPassword()));

        // Lưu user
        userRepository.save(user);

        // Đánh dấu token đã sử dụng
        token.setUsed(true);

        // Lưu token
        passwordResetTokenRepository.save(token);

        // Xóa toàn bộ refresh token
        refreshTokenRepository.deleteByUser(user);
    }
}