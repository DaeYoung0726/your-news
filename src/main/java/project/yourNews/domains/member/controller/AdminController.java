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
import org.springframework.web.bind.annotation.RestController;
import project.yourNews.domains.member.dto.MemberInfoDto;
import project.yourNews.domains.member.service.AdminService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/admin")
public class AdminController {

    private final AdminService adminService;

    /* 사용자 전체 불러오기 */
    @GetMapping("/users")
    public Page<MemberInfoDto> findAllMembers(@PageableDefault(sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {

        return adminService.findAllMembers(pageable);
    }

    /* 사용자 탈퇴 */
    @DeleteMapping("/users/{memberId}")
    public ResponseEntity<String> dropMember(@PathVariable Long memberId) {

        adminService.dropMember(memberId);
        return ResponseEntity.ok("회원 탈퇴 성공.");
    }
}
