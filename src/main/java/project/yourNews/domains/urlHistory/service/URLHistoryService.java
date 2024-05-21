package project.yourNews.domains.urlHistory.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.yourNews.domains.urlHistory.domain.URLHistory;
import project.yourNews.domains.urlHistory.dto.URLResponseDto;
import project.yourNews.domains.urlHistory.repository.URLHistoryRepository;
import project.yourNews.handler.exceptionHandler.error.ErrorCode;
import project.yourNews.handler.exceptionHandler.exception.CustomException;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class URLHistoryService {

    private final URLHistoryRepository urlHistoryRepository;

    /* 보낸 url 저장하기 */
    @Transactional
    public void saveURL(String url) {

        URLHistory urlHistory = URLHistory.builder()
                .dispatchedURL(url)
                .build();

        urlHistoryRepository.save(urlHistory);
    }

    /* 저장된 url 전체 불러오기 */
    @Transactional(readOnly = true)
    public List<URLResponseDto> readAllURL() {

        List<URLHistory> urlHistories = urlHistoryRepository.findAll();
        return urlHistories.stream().map(URLResponseDto::new).toList();
    }

    /* 저장된 url 삭제하기 */
    @Transactional
    public void deleteURL(Long urlId) {

        URLHistory url = urlHistoryRepository.findById(urlId).orElseThrow(() ->
                new CustomException(ErrorCode.URL_NOT_FOUND));

        urlHistoryRepository.delete(url);
    }

    /* 이미 보낸 소식인지 확인 */
    public boolean existsURLCheck(String url) {

        return urlHistoryRepository.existsByDispatchedURL(url);
    }
}
