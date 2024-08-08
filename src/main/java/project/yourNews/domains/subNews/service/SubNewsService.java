package project.yourNews.domains.subNews.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.yourNews.domains.member.domain.Member;
import project.yourNews.domains.member.repository.MemberRepository;
import project.yourNews.domains.news.domain.News;
import project.yourNews.domains.news.repository.NewsRepository;
import project.yourNews.domains.subNews.domain.SubNews;
import project.yourNews.domains.subNews.repository.SubNewsRepository;
import project.yourNews.common.exception.error.ErrorCode;
import project.yourNews.common.exception.CustomException;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class SubNewsService {

    private final SubNewsRepository subNewsRepository;
    private final MemberRepository memberRepository;
    private final NewsRepository newsRepository;

    /* 사용자 소식 구독하기 */
    @Transactional
    public void saveSubNews(String username, String newsName) {

        Member findMember = memberRepository.findByUsername(username).orElseThrow(() ->
                new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        News findNews = newsRepository.findByNewsName(newsName).orElseThrow(() ->
                new CustomException(ErrorCode.NEWS_NOT_FOUND));

        SubNews subNews = SubNews.builder()
                .member(findMember)
                .news(findNews)
                .build();

        subNewsRepository.save(subNews);
    }

    /* 사용자 소식 업데이트 */
    @Transactional
    public void updateUserSubNews(String username, List<String> newsNames) {
        Member member = memberRepository.findByUsername(username).orElseThrow(() ->
                new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        // 사용자가 구독한 소식 삭제
        subNewsRepository.deleteAllByMember(member);

        // 소식 구독하기
        for (String newsName : newsNames) {
            News news = newsRepository.findByNewsName(newsName).orElseThrow(() ->
                    new CustomException(ErrorCode.NEWS_NOT_FOUND));

            SubNews subNews = SubNews.builder()
                    .member(member)
                    .news(news)
                    .build();

            subNewsRepository.save(subNews);
        }
    }
}
