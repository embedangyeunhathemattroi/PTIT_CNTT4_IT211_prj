package ra.demo.service;

import ra.demo.model.dto.request.*;
import ra.demo.model.dto.response.ForgotPasswordResponse;
import ra.demo.model.dto.response.JwtResponse;

public interface AuthService {

    JwtResponse login(LoginRequest request);

    JwtResponse registerPatient(RegisterRequest request);

    JwtResponse refresh(RefreshTokenRequest request);

    void logout(String authorizationHeader,
                String refreshToken);

    void changePassword(String username,
                        ChangePasswordRequest request);

    ForgotPasswordResponse forgotPassword(
            ForgotPasswordRequest request);

    void resetPassword(
            ResetPasswordRequest request);
}