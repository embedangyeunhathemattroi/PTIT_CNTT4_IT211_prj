package ra.demo.repository;

// JpaRepository cung cấp sẵn các phương thức CRUD
import org.springframework.data.jpa.repository.JpaRepository;

// Entity RefreshToken
import ra.demo.model.entity.RefreshToken;

// Entity User
import ra.demo.model.entity.User;

// Optional dùng để xử lý trường hợp có hoặc không tìm thấy dữ liệu
import java.util.Optional;

// Repository thao tác với bảng RefreshToken
// RefreshToken là Entity
// Long là kiểu dữ liệu khóa chính
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    // Tìm Refresh Token theo chuỗi token
    // Trả về Optional để tránh NullPointerException
    Optional<RefreshToken> findByToken(String token);

    // Xóa toàn bộ Refresh Token của một User
    void deleteByUser(User user);

    // Xóa Refresh Token theo giá trị token
    void deleteByToken(String token);
}