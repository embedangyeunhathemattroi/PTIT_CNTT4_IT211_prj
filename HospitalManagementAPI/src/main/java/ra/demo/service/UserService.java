package ra.demo.service;

// Hỗ trợ phân trang dữ liệu
import org.springframework.data.domain.Page;

// Chứa thông tin phân trang (page, size, sort)
import org.springframework.data.domain.Pageable;

// DTO nhận dữ liệu User từ client
import ra.demo.model.dto.request.UserRequest;

// DTO trả dữ liệu User cho client
import ra.demo.model.dto.response.UserResponse;

// Interface định nghĩa các chức năng quản lý người dùng
public interface UserService {

    // Lấy danh sách người dùng
    // keyword dùng để tìm kiếm theo tên hoặc email
    // pageable dùng để phân trang và sắp xếp
    Page<UserResponse> findAll(
            String keyword,
            Pageable pageable
    );

    // Lấy thông tin chi tiết người dùng theo id
    UserResponse findById(
            Long id
    );

    // Tạo người dùng mới
    UserResponse create(
            UserRequest request
    );

    // Cập nhật thông tin người dùng
    UserResponse update(
            Long id,
            UserRequest request
    );

    // Khóa mềm tài khoản
    // Chỉ chuyển active = false
    // Không xóa dữ liệu khỏi database
    void deactivate(
            Long id
    );
}