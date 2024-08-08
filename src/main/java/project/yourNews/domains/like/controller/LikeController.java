package project.yourNews.domains.like.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.yourNews.domains.like.service.LikeService;
import project.yourNews.common.utils.api.ApiUtil;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/posts")
public class LikeController {

    private final LikeService likeService;

    /* 게시글 좋아요 증가 */
    @PostMapping("/{postId}/like")
    public ResponseEntity<?> likePost(@PathVariable Long postId,
                                                 @AuthenticationPrincipal UserDetails userDetails) {

        likeService.likePost(userDetails.getUsername(), postId);
        return ResponseEntity.ok(ApiUtil.from("게시글 좋아요 증가."));
    }

    /* 게시글 좋아요 감소 */
    @DeleteMapping("/{postId}/un-like")
    public ResponseEntity<?> unLikePost(@PathVariable Long postId,
                                             @AuthenticationPrincipal UserDetails userDetails) {

        likeService.unLikePost(userDetails.getUsername(), postId);
        return ResponseEntity.ok(ApiUtil.from("게시글 좋아요 감소."));
    }
}
