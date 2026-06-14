package ra.demo.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ra.demo.exception.ApiException;
import ra.demo.exception.ConflictException;
import ra.demo.exception.ForbiddenException;
import ra.demo.model.dto.request.*;
import ra.demo.model.dto.response.ForgotPasswordResponse;
import ra.demo.model.dto.response.JwtResponse;
import ra.demo.model.entity.PasswordResetToken;
import ra.demo.model.entity.RefreshToken;
import ra.demo.model.entity.User;
import ra.demo.model.enums.RoleName;
import ra.demo.repository.PasswordResetTokenRepository;
import ra.demo.repository.RefreshTokenRepository;
import ra.demo.repository.UserRepository;
import ra.demo.security.jwt.JwtProvider;
import ra.demo.security.principal.CustomUserDetails;
import ra.demo.service.AuthService;
import ra.demo.service.token.TokenBlacklistService;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenBlacklistService tokenBlacklistService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public JwtResponse login(LoginRequest r) {

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                r.getUsername(),
                                r.getPassword()
                        )
                );

        User user = userRepository.findByUsername(r.getUsername())
                .orElseThrow(() -> new ApiException("User not found"));

        if (!user.isActive()) {
            throw new ForbiddenException("Account is inactive");
        }

        String accessToken =
                jwtProvider.generateAccessToken(authentication);

        String refreshToken =
                jwtProvider.generateRefreshToken(r.getUsername());

        refreshTokenRepository.save(
                RefreshToken.builder()
                        .token(refreshToken)
                        .user(user)
                        .expiryDate(
                                jwtProvider.expirationAsLocal(
                                        refreshToken
                                )
                        )
                        .build()
        );

        return JwtResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .build();
    }

    @Override
    @Transactional
    public JwtResponse registerPatient(RegisterRequest r) {

        if (userRepository.existsByUsername(r.getUsername())
                || userRepository.existsByEmail(r.getEmail())) {
            throw new ConflictException(
                    "Username or email already exists"
            );
        }

        User user = userRepository.save(
                User.builder()
                        .username(r.getUsername())
                        .email(r.getEmail())
                        .fullName(r.getFullName())
                        .password(
                                passwordEncoder.encode(
                                        r.getPassword()
                                )
                        )
                        .role(RoleName.PATIENT)
                        .active(true)
                        .build()
        );

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(user.getUsername());
        loginRequest.setPassword(r.getPassword());

        return login(loginRequest);
    }

    @Override
    public JwtResponse refresh(RefreshTokenRequest r) {

        RefreshToken refreshToken =
                refreshTokenRepository
                        .findByToken(r.getRefreshToken())
                        .orElseThrow(() ->
                                new ApiException(
                                        "Refresh token invalid"
                                ));

        if (refreshToken.getExpiryDate()
                .isBefore(LocalDateTime.now())) {
            throw new ApiException("Refresh token expired");
        }

        if (!refreshToken.getUser().isActive()) {
            throw new ForbiddenException("Account is inactive");
        }

        CustomUserDetails details =
                new CustomUserDetails(refreshToken.getUser());

        String accessToken =
                jwtProvider.generateAccessToken(
                        new UsernamePasswordAuthenticationToken(
                                details,
                                null,
                                details.getAuthorities()
                        )
                );

        return JwtResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .tokenType("Bearer")
                .build();
    }

    @Override
    @Transactional
    public void logout(
            String authorizationHeader,
            String refreshToken
    ) {

        if (authorizationHeader == null
                || !authorizationHeader.startsWith("Bearer ")) {
            throw new ApiException("Missing token");
        }

        String accessToken =
                authorizationHeader.substring(7);

        tokenBlacklistService.blacklist(
                accessToken,
                jwtProvider.expirationAsLocal(accessToken)
        );

        if (refreshToken != null
                && !refreshToken.isBlank()) {
            refreshTokenRepository.deleteByToken(refreshToken);
        }
    }

    @Override
    @Transactional
    public void changePassword(
            String username,
            ChangePasswordRequest request
    ) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException("User not found"));

        if (!passwordEncoder.matches(
                request.getOldPassword(),
                user.getPassword()
        )) {
            throw new ApiException(
                    "Old password is incorrect"
            );
        }

        user.setPassword(
                passwordEncoder.encode(
                        request.getNewPassword()
                )
        );

        userRepository.save(user);

        refreshTokenRepository.deleteByUser(user);
    }

    @Override
    @Transactional
    public ForgotPasswordResponse forgotPassword(
            ForgotPasswordRequest request
    ) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ApiException("Email not found"));

        String token = UUID.randomUUID().toString();

        passwordResetTokenRepository.save(
                PasswordResetToken.builder()
                        .token(token)
                        .user(user)
                        .expiryDate(
                                LocalDateTime.now()
                                        .plusMinutes(30)
                        )
                        .used(false)
                        .build()
        );

        return ForgotPasswordResponse.builder()
                .email(user.getEmail())
                .resetToken(token)
                .note(
                        "Demo project: use this resetToken to call "
                                + "/api/v1/auth/reset-password. "
                                + "In production, send it by email."
                )
                .build();
    }

    @Override
    @Transactional
    public void resetPassword(
            ResetPasswordRequest request
    ) {

        PasswordResetToken token =
                passwordResetTokenRepository
                        .findByToken(request.getResetToken())
                        .orElseThrow(() ->
                                new ApiException(
                                        "Reset token invalid"
                                ));

        if (token.isUsed()) {
            throw new ApiException(
                    "Reset token already used"
            );
        }

        if (token.getExpiryDate()
                .isBefore(LocalDateTime.now())) {
            throw new ApiException(
                    "Reset token expired"
            );
        }

        User user = token.getUser();

        user.setPassword(
                passwordEncoder.encode(
                        request.getNewPassword()
                )
        );

        userRepository.save(user);

        token.setUsed(true);

        passwordResetTokenRepository.save(token);

        refreshTokenRepository.deleteByUser(user);
    }
}