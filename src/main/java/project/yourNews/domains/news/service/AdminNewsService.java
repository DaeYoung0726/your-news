package project.yourNews.domains.news.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.yourNews.domains.common.service.AssociatedEntityService;
import project.yourNews.domains.news.domain.News;
import project.yourNews.domains.news.dto.NewsInfoDto;
import project.yourNews.domains.news.dto.NewsRequestDto;
import project.yourNews.domains.news.dto.NewsResponseDto;
import project.yourNews.domains.news.repository.NewsRepository;
import project.yourNews.handler.exceptionHandler.error.ErrorCode;
import project.yourNews.handler.exceptionHandler.exception.CustomException;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class AdminNewsService {

    private final NewsRepository newsRepository;
    private final AssociatedEntityService associatedEntityService;

    /* 전체 소식 불러오기 */
    @Transactional(readOnly = true)
    public Page<NewsInfoDto> readAllNews(Pageable pageable) {

        Page<News> newsPage = newsRepository.findAll(pageable);
        return newsPage.map(NewsInfoDto::new);
    }
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

        associatedEntityService.deleteAllSubNewsByNews(findNews);   // 구독 소식 연관관계 삭제

        newsRepository.delete(findNews);
    }
}
