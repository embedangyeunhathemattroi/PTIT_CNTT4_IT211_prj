package ra.demo.repository;

// Import lớp Page dùng cho phân trang dữ liệu
import org.springframework.data.domain.Page;

// Import Pageable chứa thông tin phân trang và sắp xếp
import org.springframework.data.domain.Pageable;

// JpaRepository cung cấp sẵn các phương thức CRUD
import org.springframework.data.jpa.repository.JpaRepository;

// Entity User
import ra.demo.model.entity.User;

// Enum RoleName
import ra.demo.model.enums.RoleName;

// Optional dùng để xử lý dữ liệu có thể tồn tại hoặc không
import java.util.Optional;

// Repository thao tác với bảng users
// User là Entity
// Long là kiểu dữ liệu khóa chính
public interface UserRepository extends JpaRepository<User, Long> {

    // Tìm User theo username
    // Trả về Optional để tránh NullPointerException
    Optional<User> findByUsername(String username);

    // Tìm User theo email
    // Trả về Optional
    Optional<User> findByEmail(String email);

    // Kiểm tra username đã tồn tại hay chưa
    boolean existsByUsername(String username);

    // Kiểm tra email đã tồn tại hay chưa
    boolean existsByEmail(String email);

    // Tìm kiếm người dùng theo fullName hoặc email
    // Không phân biệt chữ hoa chữ thường
    // Có hỗ trợ phân trang
    Page<User> findByFullNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String fullName,
            String email,
            Pageable pageable);

    // Lấy danh sách User theo Role
    // Chỉ lấy các tài khoản đang hoạt động (active = true)
    // Có hỗ trợ phân trang
    Page<User> findByRoleAndActiveTrue(
            RoleName role,
            Pageable pageable);
}