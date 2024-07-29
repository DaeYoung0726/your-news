package project.yourNews.domains.member.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import project.yourNews.domains.bannedEmail.service.BannedEmailService;
import project.yourNews.domains.member.dto.MemberResponseDto;
import project.yourNews.domains.member.dto.MemberUpdateDto;
import project.yourNews.domains.member.dto.SignUpDto;
import project.yourNews.domains.member.service.MemberService;
import project.yourNews.handler.exceptionHandler.error.ErrorCode;
import project.yourNews.handler.exceptionHandler.exception.CustomException;
import project.yourNews.utils.api.ApiUtil;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/users")
public class MemberController {

    private final MemberService memberService;
    private final BannedEmailService bannedEmailService;

    /* 회원가입 */
    @PostMapping
    public ResponseEntity<?> signUp(@Valid @RequestBody SignUpDto signUpDto) {

        if (bannedEmailService.checkBannedEmail(signUpDto.getEmail())) {
            throw new CustomException(ErrorCode.BANNED_EMAIL);
        }
        memberService.signUp(signUpDto);
        return ResponseEntity.ok(ApiUtil.from("회원가입 성공."));
    }

    /* 회원 정보 불러오기 (개인정보) */
    @GetMapping
    public ResponseEntity<?> readMember(@AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.ok(memberService.readMember(userDetails.getUsername()));
    }

    /* 회원 정보 업데이트 */
    @PutMapping
    public ResponseEntity<?> updateMember(@Valid @RequestBody MemberUpdateDto memberUpdateDto,
                                               @AuthenticationPrincipal UserDetails userDetails) {

        memberService.updateMember(memberUpdateDto, userDetails.getUsername());
        return ResponseEntity.ok(ApiUtil.from("회원 정보 업데이트 성공."));
    }

    /* 회원 정보 삭제 */
    @DeleteMapping
    public ResponseEntity<?> deleteMember(@AuthenticationPrincipal UserDetails userDetails) {

        memberService.deleteMember(userDetails.getUsername());
        return ResponseEntity.ok(ApiUtil.from("회원 정보 삭제 성공."));
    }

    /* 아이디 중복 확인 */
    @GetMapping("/check-username")
    public ResponseEntity<?> existsUsernameCheck(@RequestParam("username") String username) {

        return ResponseEntity.ok(ApiUtil.from(memberService.existsUsernameCheck(username)));
    }

    /* 닉네임 중복 확인 */
    @GetMapping("/check-nickname")
    public ResponseEntity<?> existsNicknameCheck(@RequestParam("nickname") String nickname) {

        return ResponseEntity.ok(ApiUtil.from(memberService.existsNicknameCheck(nickname)));
    }

    /* 소식 수신 수정 */
    @PatchMapping("/subscribe")
    public ResponseEntity<?> subscribe(@RequestParam("username") String username,
                                       @RequestParam("value") boolean value) {

        memberService.updateSubStatus(username, value);
        return ResponseEntity.ok(ApiUtil.from("소식 수신 수정 완료."));
    }
}
