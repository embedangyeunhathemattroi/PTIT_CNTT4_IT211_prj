package ra.demo.security.config;

// Lombok tự tạo constructor cho các field final
import lombok.RequiredArgsConstructor;

// Annotation đánh dấu class cấu hình Spring
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Quản lý quá trình xác thực người dùng
import org.springframework.security.authentication.AuthenticationManager;

// Cấu hình AuthenticationManager từ Spring Security
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

// Dùng để cấu hình bảo mật HTTP
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

// Chính sách quản lý Session
import org.springframework.security.config.http.SessionCreationPolicy;

// Mã hóa mật khẩu bằng BCrypt
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

// Interface mã hóa mật khẩu
import org.springframework.security.crypto.password.PasswordEncoder;

// Chuỗi filter bảo mật của Spring Security
import org.springframework.security.web.SecurityFilterChain;

// Filter xử lý đăng nhập mặc định của Spring Security
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// JWT Filter tự xây dựng
import ra.demo.security.jwt.JwtAuthFilter;

// Đánh dấu đây là class cấu hình
@Configuration

// Lombok tự sinh constructor chứa các field final
@RequiredArgsConstructor
public class SpringSecurityConfig {

    // Filter dùng để xác thực JWT
    private final JwtAuthFilter jwtAuthFilter;

    // Khai báo Bean PasswordEncoder
    // Dùng để mã hóa và kiểm tra mật khẩu
    @Bean
    public PasswordEncoder passwordEncoder() {

        // BCrypt với strength = 10
        return new BCryptPasswordEncoder(10);
    }

    // Khai báo AuthenticationManager
    // Dùng cho chức năng Login
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration c
    ) throws Exception {

        // Lấy AuthenticationManager do Spring tạo sẵn
        return c.getAuthenticationManager();
    }

    // Cấu hình Security chính của hệ thống
    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http
    ) throws Exception {

        return http

                // Tắt CSRF
                // Thường dùng cho REST API + JWT
                .csrf(csrf -> csrf.disable())

                // Không sử dụng Session
                // Mọi request đều xác thực bằng JWT
                .sessionManagement(s ->
                        s.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                // Phân quyền API
                .authorizeHttpRequests(auth -> auth

                        // Các API công khai
                        .requestMatchers(
                                "/api/v1/auth/login",
                                "/api/v1/auth/register",
                                "/api/v1/auth/refresh",
                                "/api/v1/auth/forgot-password",
                                "/api/v1/auth/reset-password"
                        ).permitAll()

                        // Chỉ cần đăng nhập
                        .requestMatchers(
                                "/api/v1/auth/logout",
                                "/api/v1/auth/change-password"
                        ).authenticated()

                        // Chỉ ADMIN được truy cập
                        .requestMatchers(
                                "/api/v1/admin/**"
                        ).hasRole("ADMIN")

                        // Chỉ DOCTOR được truy cập
                        .requestMatchers(
                                "/api/v1/doctor/**"
                        ).hasRole("DOCTOR")

                        // Chỉ PATIENT được truy cập
                        .requestMatchers(
                                "/api/v1/patient/**"
                        ).hasRole("PATIENT")

                        // Các request còn lại cho phép truy cập
                        .anyRequest().permitAll()
                )

                // Chèn JWT Filter vào trước
                // UsernamePasswordAuthenticationFilter
                .addFilterBefore(
                        jwtAuthFilter,
                        UsernamePasswordAuthenticationFilter.class
                )

                // Build SecurityFilterChain
                .build();
    }
}