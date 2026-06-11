package ra.demo.security.principal;

// Tự động tạo constructor cho final field
import lombok.RequiredArgsConstructor;

// Interface load thông tin user cho Spring Security
import org.springframework.security.core.userdetails.UserDetails;

// Service chuẩn của Spring Security
import org.springframework.security.core.userdetails.UserDetailsService;

// Exception khi không tìm thấy user
import org.springframework.security.core.userdetails.UsernameNotFoundException;

// Đánh dấu đây là Service Bean
import org.springframework.stereotype.Service;

// Repository thao tác với bảng User
import ra.demo.repository.UserRepository;

// Đăng ký bean vào Spring Container
@Service

// Tự động sinh constructor chứa userRepository
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    // Repository truy vấn dữ liệu user
    private final UserRepository userRepository;

    // Tìm user theo username
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // Tìm user trong database
        return new CustomUserDetails(
                userRepository.findByUsername(username)

                        // Không tìm thấy thì ném exception
                        .orElseThrow(() ->
                                new UsernameNotFoundException("User not found"))
        );
    }
}