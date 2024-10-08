package project.yourNews.domains.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import project.yourNews.domains.keyword.repository.KeywordRepository;
import project.yourNews.domains.like.repository.LikeRepository;
import project.yourNews.domains.post.repository.PostRepository;
import project.yourNews.domains.subNews.repository.SubNewsRepository;

@Service
@RequiredArgsConstructor
public class AssociatedEntityService {

    private final PostRepository postRepository;
    private final SubNewsRepository subNewsRepository;
    private final LikeRepository likeRepository;
    private final KeywordRepository keywordRepository;

    /* 회원을 FK로 가지는 데이터 지우기 */
    public void deleteAllByMemberId(Long memberId) {

        likeRepository.deleteByMemberId(memberId);
        likeRepository.deletePostLikeByWriterId(memberId);
        postRepository.deleteByWriterId(memberId);
        keywordRepository.deleteByMemberId(memberId);
        subNewsRepository.deleteByMemberId(memberId);
    }

    /* 게시글을 FK로 가지는 데이터 지우기 */
    public void deleteAllByPostId(Long postId) {

        likeRepository.deleteByPostId(postId);
    }

    /* 소식을 FK로 가지는 데이터 지우기 */
    public void deleteAllByNewsId(Long newsId) {

        keywordRepository.deleteByNewsId(newsId);
        subNewsRepository.deleteByNewsId(newsId);
    }

    /* 사용자가 구독한 데아터 지우기 */
    public void deleteAllSubNewsByMemberId(Long memberId) {
        keywordRepository.deleteByMemberId(memberId);
        subNewsRepository.deleteByMemberId(memberId);
    }

}
