package ra.demo.model.dto.response;

// Import Lombok
import lombok.*;

// Enum Role
import ra.demo.model.enums.RoleName;

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
public class UserResponse {

    // ID người dùng
    private Long id;

    // Tên đăng nhập
    private String username;

    // Email
    private String email;

    // Họ và tên
    private String fullName;

    // Vai trò
    private RoleName role;

    // Trạng thái hoạt động
    private boolean active;
}