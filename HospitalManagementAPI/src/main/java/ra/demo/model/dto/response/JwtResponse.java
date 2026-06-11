package ra.demo.model.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JwtResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType;
}
