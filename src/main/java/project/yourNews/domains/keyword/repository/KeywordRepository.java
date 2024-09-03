package project.yourNews.domains.keyword.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import project.yourNews.domains.keyword.entity.Keyword;

import java.util.List;

public interface KeywordRepository extends JpaRepository<Keyword, Long> {

    @Modifying
    @Query("delete from Keyword k where k.id in :ids")
    void deleteAllKeywordByIdInQuery(@Param("ids") List<Long> ids);
}
