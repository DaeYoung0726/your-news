package project.yourNews.domains.post.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.yourNews.domains.common.service.AssociatedEntityService;
import project.yourNews.domains.post.domain.Post;
import project.yourNews.domains.post.repository.PostRepository;
import project.yourNews.common.exception.error.ErrorCode;
import project.yourNews.common.exception.CustomException;

@RequiredArgsConstructor
@Service
public class AdminPostService {

    private final PostRepository postRepository;
    private final AssociatedEntityService associatedEntityService;

    /* 어드민이 글 삭제 */
    @Transactional
    public void deletePostByAdmin(Long postId) {

        Post findPost = postRepository.findById(postId).orElseThrow(() ->
                new CustomException(ErrorCode.POST_NOT_FOUND));

        associatedEntityService.deleteAllLikeByPost(findPost);  // 좋아요 연관관계 삭제

        postRepository.delete(findPost);
    }
}
