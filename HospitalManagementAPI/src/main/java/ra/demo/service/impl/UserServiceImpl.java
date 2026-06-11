package ra.demo.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ra.demo.exception.ApiException;
import ra.demo.exception.ConflictException;
import ra.demo.mapper.ResponseMapper;
import ra.demo.model.dto.request.UserRequest;
import ra.demo.model.dto.response.UserResponse;
import ra.demo.model.entity.User;
import ra.demo.repository.UserRepository;
import ra.demo.service.UserService;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repo;
    private final PasswordEncoder encoder;

    public Page<UserResponse> findAll(String keyword, Pageable p) {
        String k = keyword == null ? "" : keyword;
        Page<User> page = repo.findByFullNameContainingIgnoreCaseOrEmailContainingIgnoreCase(k, k, p);
        var content = page.getContent().stream().map(ResponseMapper::toUser).collect(Collectors.toList());
        return new PageImpl<>(content, p, page.getTotalElements());
    }

    public UserResponse findById(Long id) {
        return ResponseMapper.toUser(getEntity(id));
    }

    private User getEntity(Long id) {
        return repo.findById(id).orElseThrow(() -> new ApiException("User not found"));
    }

    public UserResponse create(UserRequest r) {
        if (repo.existsByUsername(r.getUsername()) || repo.existsByEmail(r.getEmail()))
            throw new ConflictException("Username or email already exists");
        User saved = repo.save(User.builder().username(r.getUsername()).email(r.getEmail()).fullName(r.getFullName())
                .password(encoder.encode(r.getPassword())).role(r.getRole())
                .active(r.getActive() == null || r.getActive()).build());
        return ResponseMapper.toUser(saved);
    }

    public UserResponse update(Long id, UserRequest r) {
        User u = getEntity(id);
        if (!u.getEmail().equals(r.getEmail()) && repo.existsByEmail(r.getEmail()))
            throw new ConflictException("Email already exists");
        u.setEmail(r.getEmail());
        u.setFullName(r.getFullName());
        u.setRole(r.getRole());
        u.setActive(r.getActive() == null || r.getActive());
        if (r.getPassword() != null && !r.getPassword().isBlank()) u.setPassword(encoder.encode(r.getPassword()));
        return ResponseMapper.toUser(repo.save(u));
    }

    public void deactivate(Long id) {
        User u = getEntity(id);
        u.setActive(false);
        repo.save(u);
    }
}
