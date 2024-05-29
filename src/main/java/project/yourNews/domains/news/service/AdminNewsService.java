package project.yourNews.domains.news.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.yourNews.domains.news.domain.News;
import project.yourNews.domains.news.dto.NewsRequestDto;
import project.yourNews.domains.news.dto.NewsResponseDto;
import project.yourNews.domains.news.repository.NewsRepository;
import project.yourNews.handler.exceptionHandler.error.ErrorCode;
import project.yourNews.handler.exceptionHandler.exception.CustomException;

@RequiredArgsConstructor
@Service
public class AdminNewsService {

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
                new CustomException(ErrorCode.NEWS_NOT_FOUND));

        return new NewsResponseDto(findNews);
    }

    /* 소식 삭제하기 */
    @Transactional
    public void deleteNews(Long newsId) {

        News findNews = newsRepository.findById(newsId).orElseThrow(() ->
                new CustomException(ErrorCode.NEWS_NOT_FOUND));
        newsRepository.delete(findNews);
    }
}
