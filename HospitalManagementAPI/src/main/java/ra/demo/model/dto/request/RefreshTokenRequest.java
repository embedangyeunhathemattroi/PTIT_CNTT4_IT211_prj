package ra.demo.model.dto.request;

// Validation không được để trống
import jakarta.validation.constraints.NotBlank;

// Lombok
import lombok.Getter;
import lombok.Setter;

// Sinh getter
@Getter

// Sinh setter
@Setter
public class RefreshTokenRequest {

    // Refresh Token gửi từ client
    @NotBlank
    private String refreshToken;
}