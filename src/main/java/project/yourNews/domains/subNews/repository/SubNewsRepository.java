package project.yourNews.domains.subNews.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import project.yourNews.domains.subNews.domain.SubNews;

import java.util.Optional;

public interface SubNewsRepository extends JpaRepository<SubNews, Long> {

    Optional<SubNews> findByMember_IdAndNews_NewsName(Long memberId, String newsName);

    @Modifying
    @Query("DELETE FROM SubNews s WHERE s.member.id = :memberId")
    void deleteByMemberId(@Param("memberId") Long memberId);

    @Modifying
    @Query("DELETE FROM SubNews s WHERE s.news.id = :newsId")
    void deleteByNewsId(@Param("newsId") Long newsId);
}
