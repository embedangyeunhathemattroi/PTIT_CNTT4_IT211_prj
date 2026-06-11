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
public class JwtResponse {

    // JWT Access Token
    private String accessToken;

    // Refresh Token
    private String refreshToken;

    // Loại token
    private String tokenType;
}