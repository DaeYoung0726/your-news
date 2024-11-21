package project.yourNews.domains.subNews.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import project.yourNews.domains.subNews.domain.Keyword;

public interface KeywordRepository extends JpaRepository<Keyword, Long> {

    @Modifying
    @Query("DELETE FROM Keyword k WHERE k.subNews.id IN (SELECT s.id FROM SubNews s WHERE s.member.id = :memberId)")
    void deleteByMemberId(@Param("memberId") Long memberId);

    @Modifying
    @Query("DELETE FROM Keyword k WHERE k.subNews.id IN (SELECT s.id FROM SubNews s WHERE s.news.id = :newsId)")
    void deleteByNewsId(@Param("newsId") Long newsId);
}
