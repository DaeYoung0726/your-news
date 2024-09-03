package project.yourNews.domains.subNews.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import project.yourNews.domains.member.domain.Member;
import project.yourNews.domains.subNews.domain.SubNews;

import java.util.List;
import java.util.Optional;

public interface SubNewsRepository extends JpaRepository<SubNews, Long> {

    void deleteAllByMember(Member member);

    Optional<SubNews> findByMember_IdAndNews_NewsName(Long memberId, String newsName);

    @Modifying
    @Query("delete from SubNews s where s.id in :ids")
    void deleteAllSubNewsByIdInQuery(@Param("ids") List<Long> ids);
}
