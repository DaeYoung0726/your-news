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

    @Query("SELECT m.email FROM Member m JOIN m.subNews s WHERE m.subStatus = :subStatus AND s.news.newsName = :newsName")
    List<String> findEmailsByNewsName(@Param("subStatus") boolean subStatus, @Param("newsName") String newsName);

    @Query("SELECT m.email FROM Member m JOIN m.subNews s JOIN s.keyword k WHERE m.subStatus = :subStatus AND k.keywordName = :keyword")
    List<String> findEmailsByNewsKeyword(@Param("subStatus") boolean subStatus, @Param("keyword") String keyword);

    @Query("SELECT m.email FROM Member m JOIN m.subNews s JOIN s.news n WHERE m.dailySubStatus = :dailySubStatus AND n.newsName = :newsName")
    List<String> findEmailsByNewsNameWithDailySubStatus(@Param("dailySubStatus") boolean dailySubStatus, @Param("newsName") String newsName);

    @Query("SELECT m.email FROM Member m WHERE m.id = :memberId")
    String findEmailByMemberId(@Param("memberId") Long memberId);

    @Modifying
    @Query("update Member m set m.subStatus = :subStatus, m.dailySubStatus = :dailySubStatus where m.username = :username")
    void updateSubStatusByUsername(
            @Param("username") String username,
            @Param("subStatus") boolean subStatus,
            @Param("dailySubStatus") boolean dailySubStatus
    );
}
