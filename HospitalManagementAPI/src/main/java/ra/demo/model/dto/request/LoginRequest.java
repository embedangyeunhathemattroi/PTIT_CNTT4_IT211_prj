package ra.demo.model.dto.request;

// Validation không được để trống
import jakarta.validation.constraints.NotBlank;

// Lombok getter
import lombok.Getter;

// Lombok setter
import lombok.Setter;

// Sinh getter
@Getter

// Sinh setter
@Setter
public class LoginRequest {

    // Tên đăng nhập
    @NotBlank
    private String username;

    // Mật khẩu
    @NotBlank
    private String password;
}