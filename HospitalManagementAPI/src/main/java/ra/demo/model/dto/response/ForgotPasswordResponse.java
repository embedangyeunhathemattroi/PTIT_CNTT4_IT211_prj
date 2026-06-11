package ra.demo.model.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ForgotPasswordResponse {
    private String email;
    private String resetToken;
    private String note;
}
