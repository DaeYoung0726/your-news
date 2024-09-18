package project.yourNews.domains.like.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.yourNews.domains.like.domain.Like;
import project.yourNews.domains.like.repository.LikeRepository;
import project.yourNews.domains.member.domain.Member;
import project.yourNews.domains.member.repository.MemberRepository;
import project.yourNews.domains.post.domain.Post;
import project.yourNews.domains.post.repository.PostRepository;
import project.yourNews.common.exception.error.ErrorCode;
import project.yourNews.common.exception.CustomException;

@Slf4j
@RequiredArgsConstructor
@Service
public class LikeService {

    private final LikeRepository likeRepository;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;

    /* 게시글 좋아요 */
    @Transactional
    public void likePost(String username, Long postId) {

        Member findMember = memberRepository.findByUsername(username).orElseThrow(() ->
                new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        Post findPost = postRepository.findById(postId).orElseThrow(() ->
                new CustomException(ErrorCode.POST_NOT_FOUND));

        if (likeRepository.existsByMemberAndPost(findMember, findPost))
            throw new CustomException(ErrorCode.ALREADY_LIKE_POST);

        Like like = Like.builder()
                .member(findMember)
                .post(findPost)
                .build();

        likeRepository.save(like);
    }

    /* 게시글 좋아요 취소 */
    @Transactional
    public void unLikePost(String username, Long postId) {

        Like findLike = likeRepository.findByMember_UsernameAndPost_Id(username, postId).orElseThrow(() ->
                new CustomException(ErrorCode.LIKE_NOT_FOUND));

        likeRepository.delete(findLike);
    }
}
