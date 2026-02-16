package ee.lio.repository;

import ee.lio.model.PasswordResetToken;
import ee.lio.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<PasswordResetToken, Integer> {
    Optional<PasswordResetToken> findByTokenHash(String hash);

    Optional<PasswordResetToken> findByUser(User user);
}
