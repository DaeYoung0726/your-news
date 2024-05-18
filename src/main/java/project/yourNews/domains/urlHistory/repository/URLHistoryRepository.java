package project.yourNews.domains.urlHistory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.yourNews.domains.urlHistory.domain.URLHistory;

public interface URLHistoryRepository extends JpaRepository<URLHistory, Long> {

    boolean existsByDispatchedURL(String dispatchedURL);
}
