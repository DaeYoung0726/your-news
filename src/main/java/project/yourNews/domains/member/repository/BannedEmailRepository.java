package project.yourNews.domains.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.yourNews.domains.member.domain.BannedEmail;

public interface BannedEmailRepository extends JpaRepository<BannedEmail, Long> {

    boolean existsByEmail(String email);
}
