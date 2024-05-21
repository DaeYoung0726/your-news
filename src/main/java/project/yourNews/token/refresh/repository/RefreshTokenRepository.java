package project.token.refresh.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.token.refresh.entity.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByRefreshToken(String refreshToken);
}
