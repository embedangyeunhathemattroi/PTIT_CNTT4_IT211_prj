package ra.demo.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import ra.demo.model.entity.User;
import ra.demo.model.enums.RoleName;
import ra.demo.repository.UserRepository;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final PasswordEncoder encoder;

    @Bean
    CommandLineRunner init(UserRepository repo) {
        return args -> {

            if (!repo.existsByUsername("admin"))
                repo.save(
                        User.builder()
                                .username("admin")
                                .email("admin@hospital.com")
                                .fullName("System Admin")
                                .password(encoder.encode("123456"))
                                .role(RoleName.ADMIN)
                                .active(true)
                                .build()
                );

            if (!repo.existsByUsername("doctor"))
                repo.save(
                        User.builder()
                                .username("doctor")
                                .email("doctor@hospital.com")
                                .fullName("Default Doctor")
                                .password(encoder.encode("123456"))
                                .role(RoleName.DOCTOR)
                                .active(true)
                                .build()
                );

            if (!repo.existsByUsername("patient"))
                repo.save(
                        User.builder()
                                .username("patient")
                                .email("patient@hospital.com")
                                .fullName("Default Patient")
                                .password(encoder.encode("123456"))
                                .role(RoleName.PATIENT)
                                .active(true)
                                .build()
                );
        };
    }
}