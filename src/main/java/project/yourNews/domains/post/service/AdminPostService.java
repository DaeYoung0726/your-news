package project.yourNews.domains.post.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.yourNews.domains.common.service.AssociatedEntityService;
import project.yourNews.domains.post.repository.PostRepository;

@RequiredArgsConstructor
@Service
public class AdminPostService {

    private final PostRepository postRepository;
    private final AssociatedEntityService associatedEntityService;

    /* 어드민이 글 삭제 */
    @Transactional
    public void deletePostByAdmin(Long postId) {

        associatedEntityService.deleteAllByPostId(postId);  // 좋아요 연관관계 삭제
        postRepository.deleteById(postId);
    }
}
