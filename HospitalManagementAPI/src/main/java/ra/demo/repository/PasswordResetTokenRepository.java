package ra.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ra.demo.model.entity.PasswordResetToken;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);
}
