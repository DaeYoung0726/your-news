package project.yourNews.domains.post.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
import project.yourNews.common.utils.api.ApiUtil;
import project.yourNews.domains.post.dto.PostRequestDto;
import project.yourNews.domains.post.dto.PostResponseDto;
import project.yourNews.domains.post.service.PostService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class PostController {

    private final PostService postService;

    /* 게시글 작성 */
    @PostMapping("/{categoryName}/posts")
    public ResponseEntity<?> savePost(@Valid @RequestBody PostRequestDto postRequestDto,
                                           @PathVariable String categoryName,
                                           @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.ok(ApiUtil.from(postService.savePost(postRequestDto, userDetails.getUsername(), categoryName)));
    }

    /* 게시글 불러오기 */
    @GetMapping("/posts/{postId}")
    public ResponseEntity<?> readPost(@PathVariable Long postId,
                                                        @AuthenticationPrincipal UserDetails userDetails) {
        PostResponseDto postResponse = postService.readPost(postId);
        boolean isAuthor = false;

        if (userDetails != null)
            isAuthor = postResponse.getWriterUsername().equals(userDetails.getUsername());

        Map<String, Object> response = new HashMap<>();
        response.put("post", postResponse);
        response.put("isAuthor", isAuthor);

        return ResponseEntity.ok(response);
    }

    /* 카테고리 게시글 전체 불러오기 */
    @GetMapping("/{categoryName}/posts")
    public ResponseEntity<?> readPostsByCategory(@PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
                                                 @PathVariable String categoryName) {

        return ResponseEntity.ok(postService.readPostsByCategory(categoryName, pageable));
    }

    /* 게시글 업데이트 */
    @PutMapping("/posts/{categoryName}/{postId}")
    public ResponseEntity<?> updatePost(@Valid @RequestBody PostRequestDto postRequestDto,
                                        @PathVariable Long postId,
                                        @PathVariable String categoryName) {

        postService.updatePost(postRequestDto, postId, categoryName);
        return ResponseEntity.ok(ApiUtil.from("게시글 업데이트 성공."));
    }

    /* 게시글 삭제 */
    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<?> deletePost(@PathVariable Long postId) {

        postService.deletePost(postId);
        return ResponseEntity.ok(ApiUtil.from("게시글 삭제 성공."));
    }
}
