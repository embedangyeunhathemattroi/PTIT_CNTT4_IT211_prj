package ra.demo.model.dto.response;

// Import Lombok
import lombok.*;

// Sinh getter
@Getter

// Sinh setter
@Setter

// Constructor rỗng
@NoArgsConstructor

// Constructor đầy đủ tham số
@AllArgsConstructor

// Builder Pattern
@Builder
public class ForgotPasswordResponse {

    // Email yêu cầu reset
    private String email;

    // Token reset mật khẩu
    private String resetToken;

    // Ghi chú bổ sung
    private String note;
}