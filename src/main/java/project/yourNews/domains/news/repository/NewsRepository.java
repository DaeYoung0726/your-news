package project.yourNews.domains.news.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.yourNews.domains.news.domain.News;

public interface NewsRepository extends JpaRepository<News, Long> {

}
