package ra.demo.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ra.demo.model.dto.request.*;
import ra.demo.model.dto.response.ApiDataResponse;
import ra.demo.model.dto.response.ForgotPasswordResponse;
import ra.demo.model.dto.response.JwtResponse;
import ra.demo.service.AuthService;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiDataResponse<JwtResponse>> login(@Valid @RequestBody LoginRequest r) {
        return ResponseEntity.ok(ApiDataResponse.success("Login successfully", authService.login(r), 200));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiDataResponse<JwtResponse>> register(@Valid @RequestBody RegisterRequest r) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiDataResponse.success("Register patient successfully", authService.registerPatient(r), 201));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiDataResponse<JwtResponse>> refresh(@Valid @RequestBody RefreshTokenRequest r) {
        return ResponseEntity.ok(ApiDataResponse.success("Refresh token successfully", authService.refresh(r), 200));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiDataResponse<Void>> logout(HttpServletRequest request,
                                                        @RequestBody(required = false) RefreshTokenRequest refresh) {
        authService.logout(request.getHeader("Authorization"), refresh == null ? null : refresh.getRefreshToken());
        return ResponseEntity.ok(ApiDataResponse.success("Logout successfully", null, 200));
    }

    @PostMapping("/change-password")
    public ResponseEntity<ApiDataResponse<Void>> changePassword(Authentication a,
                                                                @Valid @RequestBody ChangePasswordRequest r) {
        authService.changePassword(a.getName(), r);
        return ResponseEntity.ok(ApiDataResponse.success("Change password successfully", null, 200));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiDataResponse<ForgotPasswordResponse>> forgot(@Valid @RequestBody ForgotPasswordRequest r) {
        return ResponseEntity.ok(
                ApiDataResponse.success("Create reset password token successfully", authService.forgotPassword(r),
                        200));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiDataResponse<Void>> reset(@Valid @RequestBody ResetPasswordRequest r) {
        authService.resetPassword(r);
        return ResponseEntity.ok(ApiDataResponse.success("Reset password successfully", null, 200));
    }
}
