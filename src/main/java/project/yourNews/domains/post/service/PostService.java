package project.yourNews.domains.post.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.yourNews.domains.category.domain.Category;
import project.yourNews.domains.category.repository.CategoryRepository;
import project.yourNews.domains.member.domain.Member;
import project.yourNews.domains.member.repository.MemberRepository;
import project.yourNews.domains.post.domain.Post;
import project.yourNews.domains.post.dto.PostRequestDto;
import project.yourNews.domains.post.dto.PostResponseDto;
import project.yourNews.domains.post.repository.PostRepository;

@Slf4j
@RequiredArgsConstructor
@Service
public class PostService {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;

    /* 게시글 저장 */
    @Transactional
    public void savePost(PostRequestDto postDto, String username, String categoryName) {

        Member findMember = memberRepository.findByUsername(username).orElseThrow(() ->
                new IllegalArgumentException("존재하지 않는 회원입니다.: " + username));

        Category findCategory = categoryRepository.findByName(categoryName).orElseThrow(() ->
                new IllegalArgumentException("존재하지 않는 카테고리입니다.: " + categoryName));

        postDto.setWriter(findMember);
        postDto.setCategory(findCategory);

        postRepository.save(postDto.toPostEntity());
    }

    /* 게시글 불러오기 */
    @Transactional(readOnly = true)
    public PostResponseDto readPost(Long postId) {

        Post findPost = postRepository.findById(postId).orElseThrow(() ->
                new IllegalArgumentException("존재하지 않는 글입니다.: " + postId));

        return new PostResponseDto(findPost);
    }

    /* 카테고리 게시글 전체 들고오기 */
    @Transactional(readOnly = true)
    public Page<PostResponseDto> readPostsByCategory(String categoryName, Pageable pageable) {

        Category findCategory = categoryRepository.findByName(categoryName).orElseThrow(() ->
                new IllegalArgumentException("존재하지 않는 카테고리입니다.: " + categoryName));

        Page<Post> posts = postRepository.findByCategory(findCategory, pageable);

        return posts.map(PostResponseDto::new);
    }

    /* 게시글 업데이트 */
    @Transactional
    public void updatePost(PostRequestDto postDto, Long postId) {

        Post findPost = postRepository.findById(postId).orElseThrow(() ->
                new IllegalArgumentException("존재하지 않는 글입니다.: " + postId));

        findPost.updatePost(postDto.getTitle(), postDto.getContent());
    }

    /* 게시글 삭제하기 */
    @Transactional
    public void deletePost(Long postId) {

        Post findPost = postRepository.findById(postId).orElseThrow(() ->
                new IllegalArgumentException("존재하지 않는 글입니다.: " + postId));

        postRepository.delete(findPost);
    }
}
