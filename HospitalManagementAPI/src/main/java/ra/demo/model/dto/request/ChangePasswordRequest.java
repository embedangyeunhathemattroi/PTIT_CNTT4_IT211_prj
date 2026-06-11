package ra.demo.model.dto.request;

// Validation không được để trống
import jakarta.validation.constraints.NotBlank;

// Lombok sinh constructor đầy đủ tham số
import lombok.AllArgsConstructor;

// Lombok getter
import lombok.Getter;

// Lombok constructor rỗng
import lombok.NoArgsConstructor;

// Lombok setter
import lombok.Setter;

// Sinh getter
@Getter

// Sinh setter
@Setter

// Constructor không tham số
@NoArgsConstructor

// Constructor đầy đủ tham số
@AllArgsConstructor
public class ChangePasswordRequest {

    // Mật khẩu hiện tại
    @NotBlank
    private String oldPassword;

    // Mật khẩu mới
    @NotBlank
    private String newPassword;
}