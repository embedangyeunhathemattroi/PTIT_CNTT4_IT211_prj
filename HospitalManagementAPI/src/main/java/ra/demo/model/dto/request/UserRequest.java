package ra.demo.model.dto.request;
// Validation
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

// Lombok
import lombok.Getter;
import lombok.Setter;

// Enum Role
import ra.demo.model.enums.RoleName;

// Sinh getter
@Getter

// Sinh setter
@Setter
public class UserRequest {

    // Username
    @NotBlank
    private String username;

    // Email hợp lệ
    @Email
    @NotBlank
    private String email;

    // Họ tên
    @NotBlank
    private String fullName;

    // Password tối thiểu 6 ký tự
    @Size(min = 6)
    private String password;

    // Vai trò người dùng
    @NotNull
    private RoleName role;

    // Trạng thái hoạt động
    // Mặc định là true
    private Boolean active = true;
}