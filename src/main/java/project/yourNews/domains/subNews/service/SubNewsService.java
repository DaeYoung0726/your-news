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
                new IllegalArgumentException("존재하지 않는 회원입니다.: " + username));

        News findNews = newsRepository.findByNewsName(newsName).orElseThrow(() ->
                new IllegalArgumentException("해당 소식은 존재하지 않습니다."));

        SubNews subNews = SubNews.builder()
                .member(findMember)
                .news(findNews)
                .build();

        subNewsRepository.save(subNews);
    }

    /* 사용자가 구독 소식 전체 삭제하기 - 사용자 소식 업데이트 과정에서 사용 */
    @Transactional
    public void deleteAllSubNewsByMember(String username) {

        Member findMember = memberRepository.findByUsername(username).orElseThrow(() ->
                new IllegalArgumentException("존재하지 않는 회원입니다.: " + username));

        List<SubNews> MemberSubNews = subNewsRepository.findByMember(findMember);

        subNewsRepository.deleteAll(MemberSubNews);
    }
}
