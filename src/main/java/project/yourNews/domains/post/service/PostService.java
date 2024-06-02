package project.yourNews.domains.post.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.yourNews.aop.annotation.VerifyAuthentication;
import project.yourNews.domains.category.domain.Category;
import project.yourNews.domains.category.repository.CategoryRepository;
import project.yourNews.domains.member.domain.Member;
import project.yourNews.domains.member.repository.MemberRepository;
import project.yourNews.domains.post.domain.Post;
import project.yourNews.domains.post.dto.PostInfoDto;
import project.yourNews.domains.post.dto.PostRequestDto;
import project.yourNews.domains.post.dto.PostResponseDto;
import project.yourNews.domains.post.repository.PostRepository;
import project.yourNews.handler.exceptionHandler.error.ErrorCode;
import project.yourNews.handler.exceptionHandler.exception.CustomException;

@Slf4j
@RequiredArgsConstructor
@Service
public class PostService {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;

    /* 게시글 저장 */
    @Transactional
    public Long savePost(PostRequestDto postDto, String username, String categoryName) {

        Member findMember = memberRepository.findByUsername(username).orElseThrow(() ->
                new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Category findCategory = categoryRepository.findByName(categoryName).orElseThrow(() ->
                new CustomException(ErrorCode.CATEGORY_NOT_FOUND));

        postDto.setWriter(findMember);
        postDto.setCategory(findCategory);

        Post createdPost = postRepository.save(postDto.toPostEntity());
        return createdPost.getId();
    }

    /* 게시글 불러오기 */
    @Transactional(readOnly = true)
    public PostResponseDto readPost(Long postId) {

        Post findPost = postRepository.findById(postId).orElseThrow(() ->
                new CustomException(ErrorCode.POST_NOT_FOUND));

        return new PostResponseDto(findPost);
    }

    /* 카테고리 게시글 전체 들고오기 */
    @Transactional(readOnly = true)
    public Page<PostInfoDto> readPostsByCategory(String categoryName, Pageable pageable) {

        Category findCategory = categoryRepository.findByName(categoryName).orElseThrow(() ->
                new CustomException(ErrorCode.CATEGORY_NOT_FOUND));

        Page<Post> posts = postRepository.findByCategory(findCategory, pageable);

        return posts.map(PostInfoDto::new);
    }

    /* 게시글 업데이트 */
    @Transactional
    @VerifyAuthentication
    public void updatePost(PostRequestDto postDto, Long postId) {

        Post findPost = postRepository.findById(postId).orElseThrow(() ->
                new CustomException(ErrorCode.POST_NOT_FOUND));

        findPost.updatePost(postDto.getTitle(), postDto.getContent());
    }

    /* 게시글 삭제하기 */
    @Transactional
    @VerifyAuthentication
    public void deletePost(Long postId) {

        Post findPost = postRepository.findById(postId).orElseThrow(() ->
                new CustomException(ErrorCode.POST_NOT_FOUND));

        postRepository.delete(findPost);
    }
}
