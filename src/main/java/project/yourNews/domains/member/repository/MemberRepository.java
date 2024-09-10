package project.yourNews.domains.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import project.yourNews.domains.member.domain.Member;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByUsername(String username);
    Optional<Member> findByNickname(String nickname);
    Optional<Member> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByNickname(String nickname);
    boolean existsByEmail(String email);
    boolean existsByUsernameAndEmail(String username, String email);
    List<Member> findBySubStatusAndSubNews_News_NewsName(boolean subStatus, String newsName);
    List<Member> findBySubStatusAndSubNews_Keyword_KeywordName(boolean subStatus, String keyword);

    @Query("SELECT m.email FROM Member m WHERE m.id = :memberId")
    String findEmailByMemberId(@Param("memberId") Long memberId);

    @Modifying
    @Query("update Member m set m.subStatus = :subStatus where m.username = :username")
    void updateSubStatusByUsername(@Param("username") String username, @Param("subStatus") boolean subStatus);
}
