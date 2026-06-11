package ra.demo.model.dto.request;

// Validation
import jakarta.validation.constraints.NotBlank;

// Lombok
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Sinh getter
@Getter

// Sinh setter
@Setter

// Constructor rỗng
@NoArgsConstructor

// Constructor đầy đủ tham số
@AllArgsConstructor
public class ResetPasswordRequest {

    // Token reset mật khẩu
    @NotBlank
    private String resetToken;

    // Mật khẩu mới
    @NotBlank
    private String newPassword;
}