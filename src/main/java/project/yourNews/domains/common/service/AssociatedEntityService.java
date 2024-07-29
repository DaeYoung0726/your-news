package project.yourNews.domains.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.yourNews.domains.like.domain.Like;
import project.yourNews.domains.like.repository.LikeRepository;
import project.yourNews.domains.member.domain.Member;
import project.yourNews.domains.news.domain.News;
import project.yourNews.domains.post.domain.Post;
import project.yourNews.domains.post.repository.PostRepository;
import project.yourNews.domains.subNews.domain.SubNews;
import project.yourNews.domains.subNews.repository.SubNewsRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssociatedEntityService {

    private final PostRepository postRepository;
    private final SubNewsRepository subNewsRepository;
    private final LikeRepository likeRepository;

    /* 회원과 관계 매핑된 좋아요 삭제(회원이 좋아요한 것) */
    public void deleteAllLikeByMember(Member foundMember) {

        List<Long> likeIds = foundMember.getLikes().stream()
                .map(Like::getId)
                .collect(Collectors.toList());

        if (!likeIds.isEmpty()) {
            likeRepository.deleteAllLikeByIdInQuery(likeIds);
        }
    }

    /* 회원과 관계 매핑된 게시글 삭제 (회원의 게시글) */
    public void deleteAllPostByMember(Member foundMember) {

        List<Post> posts = foundMember.getPosts();

        for (Post post : posts) {
            this.deleteAllLikeByPost(post);    // 게시글의 좋아요 삭제
        }

        List<Long> postIds = posts.stream()
                .map(Post::getId)
                .collect(Collectors.toList());

        if (!postIds.isEmpty()) {
            postRepository.deleteAllPostByIdInQuery(postIds);
        }
    }


    /* 회원과 관계 매핑된 소식 구독 삭제(회원이 구독한 소식) */
    public void deleteAllSubNewsByMember(Member foundMember) {

        List<Long> subNewsId = foundMember.getSubNews().stream()
                .map(SubNews::getId)
                .collect(Collectors.toList());

        if (!subNewsId.isEmpty()) {
            subNewsRepository.deleteAllSubNewsByIdInQuery(subNewsId);
        }
    }

    /* 게시글과 관계 매핑된 좋아요 삭제(게시글의 좋아요) */
    public void deleteAllLikeByPost(Post foundPost) {

        List<Long> likeIds = foundPost.getLikes().stream()
                .map(Like::getId)
                .collect(Collectors.toList());

        if (!likeIds.isEmpty()) {
            likeRepository.deleteAllLikeByIdInQuery(likeIds);
        }
    }

    /* 소식과 관계 매핑된 구독 소식 삭제 (소식을 구독한 내용) */
    public void deleteAllSubNewsByNews(News foundNews) {
        List<Long> subNewsIds = foundNews.getMemberSubNews().stream()
                .map(SubNews::getId)
                .collect(Collectors.toList());

        if (!subNewsIds.isEmpty()) {
            subNewsRepository.deleteAllSubNewsByIdInQuery(subNewsIds);
        }
    }
}
