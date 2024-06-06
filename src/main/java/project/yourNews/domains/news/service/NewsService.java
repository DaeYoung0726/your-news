package project.yourNews.domains.news.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.yourNews.domains.news.domain.News;
import project.yourNews.domains.news.dto.NewsInfoDto;
import project.yourNews.domains.news.repository.NewsRepository;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class NewsService {

    private final NewsRepository newsRepository;

    /* 전체 소식 불러오기 */
    @Transactional(readOnly = true)
    public List<NewsInfoDto> readAllNews() {

        List<News> news = newsRepository.findAllByOrderByNewsNameAsc();
        return news.stream().map(NewsInfoDto::new).toList();
    }
}
