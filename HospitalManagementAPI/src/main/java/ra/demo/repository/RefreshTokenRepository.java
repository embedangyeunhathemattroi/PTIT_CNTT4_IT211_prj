package ra.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ra.demo.model.entity.RefreshToken;
import ra.demo.model.entity.User;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    void deleteByUser(User user);

    void deleteByToken(String token);
}