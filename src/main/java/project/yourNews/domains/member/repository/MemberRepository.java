package project.yourNews.domains.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.yourNews.domains.member.domain.Member;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByUsername(String username);
    Optional<Member> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByNickname(String nickname);
    boolean existsByEmail(String email);
    boolean existsByUsernameAndEmail(String username, String email);
    List<Member> findBySubNews_News_NewsURL(String newsId);
}
