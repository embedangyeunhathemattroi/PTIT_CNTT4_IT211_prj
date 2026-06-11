package ra.demo.model.dto.response;

import lombok.*;
import ra.demo.model.enums.RoleName;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private RoleName role;
    private boolean active;
}
