package project.yourNews.domains.keyword.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.yourNews.domains.keyword.entity.Keyword;
import project.yourNews.domains.keyword.repository.KeywordRepository;
import project.yourNews.domains.subNews.domain.SubNews;

@RequiredArgsConstructor
@Service
public class KeywordService {

    private final KeywordRepository keywordRepository;

    /* 키워드 설정 */
    @Transactional
    public void linkNewsToKeyword(SubNews subNews, String keyword) {
        keywordRepository.save(
                Keyword.builder()
                        .keywordName(keyword)
                        .subNews(subNews)
                        .build()
        );
    }
}
