package ra.demo.repository;

// JpaRepository cung cấp sẵn các phương thức CRUD
import org.springframework.data.jpa.repository.JpaRepository;

// Entity PasswordResetToken
import ra.demo.model.entity.PasswordResetToken;

// Optional dùng để xử lý trường hợp có hoặc không tìm thấy dữ liệu
import java.util.Optional;

// Repository thao tác với bảng PasswordResetToken
// PasswordResetToken là Entity
// Long là kiểu dữ liệu khóa chính
public interface PasswordResetTokenRepository
        extends JpaRepository<PasswordResetToken, Long> {

    // Tìm Password Reset Token theo giá trị token
    // Trả về Optional để tránh NullPointerException
    Optional<PasswordResetToken> findByToken(String token);
}