package project.yourNews.domains.bannedEmail.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.yourNews.domains.bannedEmail.domain.BannedEmail;

public interface BannedEmailRepository extends JpaRepository<BannedEmail, Long> {

    boolean existsByEmail(String email);
}
