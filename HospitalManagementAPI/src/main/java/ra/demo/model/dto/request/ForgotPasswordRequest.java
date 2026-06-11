package ra.demo.model.dto.request;

// Validation email đúng định dạng
import jakarta.validation.constraints.Email;

// Validation không được để trống
import jakarta.validation.constraints.NotBlank;

// Lombok annotations
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
public class ForgotPasswordRequest {

    // Email nhận yêu cầu reset mật khẩu
    @NotBlank

    // Phải đúng định dạng email
    @Email
    private String email;
}