package ra.demo.model.entity;

// Import JPA
import jakarta.persistence.*;

// Import Lombok
import lombok.*;

// Enum Role
import ra.demo.model.enums.RoleName;

// Entity User
@Entity

// Đổi tên bảng thành users
@Table(name = "users")

// Lombok
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    // ID người dùng
    @Id

    // Tự tăng
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Username
    // Không được null và không được trùng
    @Column(nullable = false, unique = true)
    private String username;

    // Email
    // Không được null và không được trùng
    @Column(nullable = false, unique = true)
    private String email;

    // Mật khẩu đã mã hóa BCrypt
    @Column(nullable = false)
    private String password;

    // Họ tên
    @Column(nullable = false)
    private String fullName;

    // Vai trò người dùng
    @Enumerated(EnumType.STRING)

    // Không được null
    @Column(nullable = false)
    private RoleName role;

    // Trạng thái hoạt động
    @Column(nullable = false)
    private boolean active;
}