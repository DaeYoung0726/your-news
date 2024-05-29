package project.yourNews.domains.post.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.yourNews.domains.post.dto.PostInfoDto;
import project.yourNews.domains.post.dto.PostRequestDto;
import project.yourNews.domains.post.dto.PostResponseDto;
import project.yourNews.domains.post.service.PostService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class PostController {

    private final PostService postService;

    /* 게시글 작성 */
    @PostMapping("/{categoryName}/posts")
    public ResponseEntity<String> savePost(@Valid @RequestBody PostRequestDto postRequestDto,
                                           @PathVariable String categoryName,
                                           @AuthenticationPrincipal UserDetails userDetails) {

        postService.savePost(postRequestDto, userDetails.getUsername(), categoryName);
        return ResponseEntity.ok("게시글 작성 성공.");
    }

    /* 게시글 불러오기 */
    @GetMapping("/posts/{postId}")
    public PostResponseDto readPost(@PathVariable Long postId) {

        return postService.readPost(postId);
    }

    /* 카테고리 게시글 전체 불러오기 */
    @GetMapping("/{categoryName}/posts")
    public Page<PostInfoDto> readPostsByCategory(@PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
                                                 @PathVariable String categoryName) {

        return postService.readPostsByCategory(categoryName, pageable);
    }

    /* 게시글 업데이트 */
    @PutMapping("/posts/{postId}")
    public ResponseEntity<String> updatePost(@Valid @RequestBody PostRequestDto postRequestDto,
                                             @PathVariable Long postId) {

        postService.updatePost(postRequestDto, postId);
        return ResponseEntity.ok("게시글 업데이트 성공.");
    }

    /* 게시글 삭제 */
    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<String> deletePost(@PathVariable Long postId) {

        postService.deletePost(postId);
        return ResponseEntity.ok("게시글 삭제 성공.");
    }
}
