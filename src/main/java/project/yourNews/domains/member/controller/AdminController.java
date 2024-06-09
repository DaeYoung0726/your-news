package project.yourNews.domains.member.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import project.yourNews.domains.member.dto.MemberInfoDto;
import project.yourNews.domains.member.dto.MemberResponseDto;
import project.yourNews.domains.member.service.AdminService;
import project.yourNews.domains.post.service.AdminPostService;
import project.yourNews.utils.api.ApiUtil;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/admin")
public class AdminController {

    private final AdminService adminService;
    private final AdminPostService adminPostService;

    /* 사용자 전체 불러오기 */
    @GetMapping("/users")
    public Page<MemberInfoDto> findAllMembers(@PageableDefault(sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {

        return adminService.findAllMembers(pageable);
    }

    /* 특정 사용자 불러오기 */
    @GetMapping("/users/{memberId}")
    public MemberResponseDto getMemberById(@PathVariable Long memberId) {
        return adminService.readMemberById(memberId);
    }

    /* 닉네임으로 사용자 불러오기 */
    @GetMapping("/users/by-nickname")
    public MemberResponseDto getMemberByNickname(@RequestParam String nickname) {
        return adminService.readMemberByNickname(nickname);
    }

    /* 사용자 탈퇴 */
    @DeleteMapping("/users/{memberId}")
    public ResponseEntity<?> dropMember(@PathVariable Long memberId) {

        adminService.dropMember(memberId);
        return ResponseEntity.ok(ApiUtil.from("회원 탈퇴 성공."));
    }

    /* 글 삭제 - 어드민 */
    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<?> deletePostByAdmin(@PathVariable Long postId) {

        adminPostService.deletePostByAdmin(postId);
        return ResponseEntity.ok(ApiUtil.from("글 삭제 성공."));
    }
}
