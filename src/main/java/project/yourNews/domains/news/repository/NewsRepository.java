package project.yourNews.domains.news.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import project.yourNews.domains.news.domain.News;

import java.util.List;
import java.util.Optional;

public interface NewsRepository extends JpaRepository<News, Long> {

    Optional<News> findByNewsName(String newsName);

    List<News> findAllByOrderByNewsNameAsc();

    @Query("SELECT COUNT(sn) FROM SubNews sn WHERE sn.news.id = :newsId")
    int countSubNewsByNewsId(@Param("newsId") Long newsId);
}
