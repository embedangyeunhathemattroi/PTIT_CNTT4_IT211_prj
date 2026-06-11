package ra.demo.model.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import ra.demo.model.enums.RoleName;

@Getter
@Setter
public class UserRequest {
    @NotBlank
    private String username;
    @Email
    @NotBlank
    private String email;
    @NotBlank
    private String fullName;
    @Size(min = 6)
    private String password;
    @NotNull
    private RoleName role;
    private Boolean active = true;
}
