package ra.demo.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ra.demo.model.dto.request.UserRequest;
import ra.demo.model.dto.response.UserResponse;

public interface UserService {

    Page<UserResponse> findAll(
            String keyword,
            Pageable pageable
    );

    UserResponse findById(
            Long id
    );

    UserResponse create(
            UserRequest request
    );

    UserResponse update(
            Long id,
            UserRequest request
    );

    void deactivate(
            Long id
    );
}