package project.yourNews.domains.member.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import project.yourNews.common.utils.api.ApiUtil;
import project.yourNews.domains.bannedEmail.dto.BannedEmailRequestDto;
import project.yourNews.domains.bannedEmail.service.BannedEmailService;
import project.yourNews.domains.member.service.AdminService;
import project.yourNews.domains.post.service.AdminPostService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/admin")
public class AdminController {

    private final AdminService adminService;
    private final AdminPostService adminPostService;
    private final BannedEmailService bannedEmailService;

    /* 사용자 전체 불러오기 */
    @GetMapping("/users")
    public ResponseEntity<?> findAllMembers(@PageableDefault(sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {

        return ResponseEntity.ok(adminService.findAllMembers(pageable));
    }

    /* 특정 사용자 불러오기 */
    @GetMapping("/users/{memberId}")
    public ResponseEntity<?> getMemberById(@PathVariable Long memberId) {
        return ResponseEntity.ok(adminService.readMemberById(memberId));
    }

    /* 닉네임으로 사용자 불러오기 */
    @GetMapping("/users/by-nickname")
    public ResponseEntity<?> getMemberByNickname(@RequestParam String nickname) {
        return ResponseEntity.ok(adminService.readMemberByNickname(nickname));
    }

    /* 사용자 탈퇴 */
    @DeleteMapping("/users")
    public ResponseEntity<?> dropMember(@RequestBody BannedEmailRequestDto bannedEmailDto) {

        bannedEmailService.setBannedEmail(bannedEmailDto);
        adminService.dropMember(bannedEmailDto.getId());
        return ResponseEntity.ok(ApiUtil.from("회원 탈퇴 성공."));
    }

    /* 글 삭제 - 어드민 */
    @DeleteMapping("/posts/{categoryName}/{postId}")
    public ResponseEntity<?> deletePostByAdmin(@PathVariable Long postId,
                                               @PathVariable String categoryName) {

        adminPostService.deletePostByAdmin(postId, categoryName);
        return ResponseEntity.ok(ApiUtil.from("글 삭제 성공."));
    }
}
