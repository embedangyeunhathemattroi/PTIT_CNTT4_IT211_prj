package ra.demo.model.entity;

// Import JPA
import jakarta.persistence.*;

// Import Lombok
import lombok.*;

// Kiểu dữ liệu ngày giờ
import java.time.LocalDateTime;

// Entity lưu token reset mật khẩu
@Entity

// Lombok
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordResetToken {

    // ID token
    @Id

    // Tự động tăng
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Giá trị token
    // Không được trùng
    @Column(nullable = false, unique = true)
    private String token;

    // User sở hữu token
    @ManyToOne(optional = false)
    private User user;

    // Thời gian hết hạn token
    private LocalDateTime expiryDate;

    // Đã sử dụng hay chưa
    private boolean used;
}