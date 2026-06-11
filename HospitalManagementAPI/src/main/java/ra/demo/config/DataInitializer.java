package ra.demo.config;

// Lombok tự tạo constructor chứa các final field
import lombok.RequiredArgsConstructor;

// Cho phép tạo bean khi ứng dụng khởi động
import org.springframework.boot.CommandLineRunner;

// Đánh dấu đây là lớp cấu hình Spring
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Dùng để mã hóa mật khẩu
import org.springframework.security.crypto.password.PasswordEncoder;

// Entity User
import ra.demo.model.entity.User;

// Enum chứa các role của hệ thống
import ra.demo.model.enums.RoleName;

// Repository thao tác với bảng User
import ra.demo.repository.UserRepository;

// Đánh dấu đây là class cấu hình
@Configuration

// Tự sinh constructor cho các thuộc tính final
@RequiredArgsConstructor
public class DataInitializer {

    // Bean mã hóa mật khẩu được Spring inject vào
    private final PasswordEncoder encoder;

    // Tạo bean CommandLineRunner
    // Bean này sẽ chạy tự động khi ứng dụng Spring Boot khởi động
    @Bean
    CommandLineRunner init(UserRepository repo) {

        // Lambda của CommandLineRunner
        return args -> {

            // Kiểm tra tài khoản admin đã tồn tại chưa
            if (!repo.existsByUsername("admin"))

                // Nếu chưa tồn tại thì tạo mới
                repo.save(

                        // Sử dụng Builder Pattern để tạo User
                        User.builder()

                                // Username đăng nhập
                                .username("admin")

                                // Email
                                .email("admin@hospital.com")

                                // Họ tên
                                .fullName("System Admin")

                                // Mã hóa password trước khi lưu DB
                                .password(encoder.encode("123456"))

                                // Role ADMIN
                                .role(RoleName.ADMIN)

                                // Trạng thái hoạt động
                                .active(true)

                                // Hoàn tất đối tượng User
                                .build()
                );

            // Kiểm tra tài khoản doctor đã tồn tại chưa
            if (!repo.existsByUsername("doctor"))

                // Nếu chưa tồn tại thì thêm mới
                repo.save(

                        User.builder()

                                // Tên đăng nhập
                                .username("doctor")

                                // Email bác sĩ
                                .email("doctor@hospital.com")

                                // Họ tên bác sĩ mặc định
                                .fullName("Default Doctor")

                                // Mã hóa mật khẩu
                                .password(encoder.encode("123456"))

                                // Quyền bác sĩ
                                .role(RoleName.DOCTOR)

                                // Trạng thái kích hoạt
                                .active(true)

                                .build()
                );

            // Kiểm tra tài khoản patient đã tồn tại chưa
            if (!repo.existsByUsername("patient"))

                // Nếu chưa có thì thêm mới
                repo.save(

                        User.builder()

                                // Username
                                .username("patient")

                                // Email
                                .email("patient@hospital.com")

                                // Tên bệnh nhân mặc định
                                .fullName("Default Patient")

                                // Mã hóa mật khẩu
                                .password(encoder.encode("123456"))

                                // Quyền bệnh nhân
                                .role(RoleName.PATIENT)

                                // Tài khoản hoạt động
                                .active(true)

                                .build()
                );
        };
    }
}