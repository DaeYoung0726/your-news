package project.yourNews.domains.subNews.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.yourNews.common.exception.CustomException;
import project.yourNews.common.exception.error.ErrorCode;
import project.yourNews.domains.common.service.AssociatedEntityService;
import project.yourNews.domains.keyword.entity.Keyword;
import project.yourNews.domains.keyword.service.KeywordService;
import project.yourNews.domains.member.domain.Member;
import project.yourNews.domains.member.repository.MemberRepository;
import project.yourNews.domains.news.domain.News;
import project.yourNews.domains.news.repository.NewsRepository;
import project.yourNews.domains.subNews.domain.SubNews;
import project.yourNews.domains.subNews.dto.SubNewsUpdateDto;
import project.yourNews.domains.subNews.repository.SubNewsRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class SubNewsService {

    private final KeywordService keywordService;
    private final AssociatedEntityService associatedEntityService;
    private final SubNewsRepository subNewsRepository;
    private final MemberRepository memberRepository;
    private final NewsRepository newsRepository;

    private static final String YU_NEWS_NAME = "영대소식";

    /* 사용자 소식 구독하기 */
    @Transactional
    public SubNews subscribeToNews(Member member, String newsName) {

        News findNews = newsRepository.findByNewsName(newsName).orElseThrow(() ->
                new CustomException(ErrorCode.NEWS_NOT_FOUND));

        SubNews subNews = SubNews.builder()
                .member(member)
                .news(findNews)
                .build();

        return subNewsRepository.save(subNews);
    }

    /* 사용자 소식 구독하기 (영대소식 - 키워드 존재) */
    @Transactional
    public void subscribeToNewsWithKeyword(Member member, String newsName, List<String> keywords) {

        SubNews savedSubNews = this.subscribeToNews(member, newsName);

        for (String keyword: keywords) {
            keywordService.linkNewsToKeyword(savedSubNews, keyword);
        }
    }

    /* 사용자 소식 업데이트 */
    @Transactional
    public void updateUserSubNews(String username, SubNewsUpdateDto updateDto) {
        Member member = memberRepository.findByUsername(username).orElseThrow(() ->
                new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        associatedEntityService.deleteAllSubNewsByMemberId(member.getId());

        for (String newsName: updateDto.getNewsNames()) {
            SubNews savedSubNews = this.subscribeToNews(member, newsName);
            if (newsName.equals(YU_NEWS_NAME)) {
                for (String keyword: updateDto.getKeywords()) {
                    keywordService.linkNewsToKeyword(savedSubNews, keyword);
                }
            }
        }
    }

    /* 구독한 키워드 불러오기 */
    @Transactional(readOnly = true)
    public List<String> getSubscribedKeyword(Long memberId) {

        Optional<SubNews> subNews = subNewsRepository.findByMember_IdAndNews_NewsName(memberId, YU_NEWS_NAME);

        if (subNews.isPresent()) {
            return subNews.get().getKeyword().stream()
                    .map(Keyword::getKeywordName)
                    .collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }
}
