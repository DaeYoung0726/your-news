package project.yourNews.domains.news.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.yourNews.domains.news.domain.News;

import java.util.List;
import java.util.Optional;

public interface NewsRepository extends JpaRepository<News, Long> {

    Optional<News> findByNewsName(String newsName);

    List<News> findAllByOrderByNewsNameAsc();
}
