package ra.demo.service.impl;

// Lombok tự tạo constructor
import lombok.RequiredArgsConstructor;
// Hỗ trợ phân trang
import org.springframework.data.domain.Page;
// Triển khai Page thủ công
import org.springframework.data.domain.PageImpl;
// Thông tin phân trang
import org.springframework.data.domain.Pageable;
// Mã hóa mật khẩu
import org.springframework.security.crypto.password.PasswordEncoder;
// Đánh dấu Service
import org.springframework.stereotype.Service;
// Exception nghiệp vụ
import ra.demo.exception.ApiException;
// Exception dữ liệu trùng
import ra.demo.exception.ConflictException;
// Mapper Entity -> DTO
import ra.demo.mapper.ResponseMapper;
// DTO nhận dữ liệu User
import ra.demo.model.dto.request.UserRequest;
// DTO trả về User
import ra.demo.model.dto.response.UserResponse;
// Entity User
import ra.demo.model.entity.User;
// Repository User
import ra.demo.repository.UserRepository;
// Interface UserService
import ra.demo.service.UserService;

import java.util.stream.Collectors;

// Đăng ký Service Bean
@Service
// Tạo constructor cho các final field
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    // Repository thao tác User
    private final UserRepository repo;

    // Mã hóa mật khẩu
    private final PasswordEncoder encoder;

    // Lấy danh sách user có tìm kiếm + phân trang
    public Page<UserResponse> findAll(String keyword, Pageable p) {

        // Nếu keyword null thì dùng chuỗi rỗng
        String k = keyword == null ? "" : keyword;

        // Tìm theo fullname hoặc email
        Page<User> page =
                repo.findByFullNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                        k, k, p);

        // Chuyển Entity -> DTO
        var content = page.getContent()
                .stream()
                .map(ResponseMapper::toUser)
                .collect(Collectors.toList());

        // Trả Page DTO
        return new PageImpl<>(
                content,
                p,
                page.getTotalElements()
        );
    }

    // Tìm user theo id
    public UserResponse findById(Long id) {

        return ResponseMapper.toUser(
                getEntity(id)
        );
    }

    // Lấy User entity theo id
    private User getEntity(Long id) {

        return repo.findById(id)
                .orElseThrow(() ->
                        new ApiException("User not found"));
    }

    // Tạo user mới
    public UserResponse create(UserRequest r) {

        // Kiểm tra username hoặc email đã tồn tại
        if (repo.existsByUsername(r.getUsername())
                || repo.existsByEmail(r.getEmail()))

            throw new ConflictException(
                    "Username or email already exists"
            );

        // Lưu user mới
        User saved = repo.save(
                User.builder()
                        .username(r.getUsername())
                        .email(r.getEmail())
                        .fullName(r.getFullName())
                        .password(
                                encoder.encode(r.getPassword())
                        )
                        .role(r.getRole())
                        .active(
                                r.getActive() == null
                                        || r.getActive()
                        )
                        .build()
        );

        // Trả DTO
        return ResponseMapper.toUser(saved);
    }

    // Cập nhật user
    public UserResponse update(Long id, UserRequest r) {

        // Lấy user hiện tại
        User u = getEntity(id);

        // Nếu đổi email thì kiểm tra trùng
        if (!u.getEmail().equals(r.getEmail())
                && repo.existsByEmail(r.getEmail()))

            throw new ConflictException(
                    "Email already exists"
            );

        // Cập nhật email
        u.setEmail(r.getEmail());

        // Cập nhật họ tên
        u.setFullName(r.getFullName());

        // Cập nhật role
        u.setRole(r.getRole());

        // Cập nhật trạng thái active
        u.setActive(
                r.getActive() == null
                        || r.getActive()
        );

        // Nếu có mật khẩu mới
        if (r.getPassword() != null
                && !r.getPassword().isBlank())

            // Mã hóa mật khẩu mới
            u.setPassword(
                    encoder.encode(r.getPassword())
            );

        // Lưu dữ liệu
        return ResponseMapper.toUser(
                repo.save(u)
        );
    }

    // Khóa mềm user
    public void deactivate(Long id) {

        // Tìm user
        User u = getEntity(id);

        // Chuyển trạng thái inactive
        u.setActive(false);

        // Lưu lại DB
        repo.save(u);
    }
}