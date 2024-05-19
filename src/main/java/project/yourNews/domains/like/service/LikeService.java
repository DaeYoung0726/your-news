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
                new IllegalArgumentException("존재하지 않는 회원입니다.: " + username));
        Post findPost = postRepository.findById(postId).orElseThrow(() ->
                new IllegalArgumentException("존재하지 않는 글입니다.: " + postId));

        if (likeRepository.existsByMemberAndPost(findMember, findPost))
            throw new RuntimeException("이미 해당 게시글에 좋아요를 눌렀습니다.");

        Like like = Like.builder()
                .member(findMember)
                .post(findPost)
                .build();

        likeRepository.save(like);
    }

    /* 게시글 좋아요 취소 */
    @Transactional
    public void unLikePost(String username, Long postId) {

        Member findMember = memberRepository.findByUsername(username).orElseThrow(() ->
                new IllegalArgumentException("존재하지 않는 회원입니다.: " + username));
        Post findPost = postRepository.findById(postId).orElseThrow(() ->
                new IllegalArgumentException("존재하지 않는 글입니다.: " + postId));

        Like findLike = likeRepository.findByMemberAndPost(findMember, findPost).orElseThrow(() ->
                new IllegalArgumentException("해당 게시글을 좋아요 누른 적이 없습니다."));

        likeRepository.delete(findLike);
    }
}
