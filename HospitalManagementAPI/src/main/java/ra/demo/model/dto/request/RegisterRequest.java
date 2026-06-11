package ra.demo.model.dto.request;

// Validation
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// Lombok
import lombok.Getter;
import lombok.Setter;

// Sinh getter
@Getter

// Sinh setter
@Setter
public class RegisterRequest {

    // Tên đăng nhập
    @NotBlank
    private String username;

    // Email
    @Email
    @NotBlank
    private String email;

    // Mật khẩu tối thiểu 6 ký tự
    @NotBlank
    @Size(min = 6)
    private String password;

    // Họ tên
    @NotBlank
    private String fullName;
}