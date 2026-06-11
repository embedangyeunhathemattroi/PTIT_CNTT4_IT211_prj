package ra.demo.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ra.demo.model.entity.User;
import ra.demo.model.enums.RoleName;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Page<User> findByFullNameContainingIgnoreCaseOrEmailContainingIgnoreCase(String fullName, String email,
                                                                             Pageable pageable);

    Page<User> findByRoleAndActiveTrue(RoleName role, Pageable pageable);
}
