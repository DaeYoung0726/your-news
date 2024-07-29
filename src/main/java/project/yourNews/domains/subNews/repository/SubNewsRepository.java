package project.yourNews.domains.subNews.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import project.yourNews.domains.member.domain.Member;
import project.yourNews.domains.subNews.domain.SubNews;

import java.util.List;

public interface SubNewsRepository extends JpaRepository<SubNews, Long> {

    void deleteAllByMember(Member member);

    @Modifying
    @Query("delete from SubNews s where s.id in :ids")
    void deleteAllSubNewsByIdInQuery(@Param("ids") List<Long> ids);
}
