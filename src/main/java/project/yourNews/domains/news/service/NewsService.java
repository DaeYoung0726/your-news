package project.yourNews.domains.news.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.yourNews.domains.news.domain.News;
import project.yourNews.domains.news.dto.NewsRequestDto;
import project.yourNews.domains.news.dto.NewsResponseDto;
import project.yourNews.domains.news.repository.NewsRepository;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class NewsService {

    private final NewsRepository newsRepository;

    /* 소식 생성 */
    @Transactional
    public void saveNews(NewsRequestDto newsDto) {

        newsRepository.save(newsDto.toNewsEntity());
    }

    /* 특정 소식 불러오기 */
    @Transactional(readOnly = true)
    public NewsResponseDto readNews(Long newsId) {

        News findNews = newsRepository.findById(newsId).orElseThrow(() ->
                new IllegalArgumentException("해당 소식은 존재하지 않습니다."));

        return new NewsResponseDto(findNews);
    }

    /* 전체 소식 불러오기 */
    @Transactional(readOnly = true)
    public List<NewsResponseDto> readAllNews() {

        List<News> news = newsRepository.findAll();
        return news.stream().map(NewsResponseDto::new).toList();
    }

    /* 소식 삭제하기 */
    @Transactional
    public void deleteNews(Long newsId) {

        News findNews = newsRepository.findById(newsId).orElseThrow(() ->
                new IllegalArgumentException("해당 소식은 존재하지 않습니다."));
        newsRepository.delete(findNews);
    }
}
