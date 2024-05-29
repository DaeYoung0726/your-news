package project.yourNews.domains.member.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import project.yourNews.domains.member.dto.MemberResponseDto;
import project.yourNews.domains.member.dto.MemberUpdateDto;
import project.yourNews.domains.member.dto.SignUpDto;
import project.yourNews.domains.member.service.MemberService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/users")
public class MemberController {

    private final MemberService memberService;

    /* 회원가입 */
    @PostMapping
    public ResponseEntity<String> signUp(@Valid @RequestBody SignUpDto signUpDto) {

        memberService.signUp(signUpDto);
        return ResponseEntity.ok("회원가입 성공.");
    }

    /* 회원 정보 불러오기 (개인정보) */
    @GetMapping
    public MemberResponseDto readMember(@AuthenticationPrincipal UserDetails userDetails) {

        return memberService.readMember(userDetails.getUsername());
    }

    /* 회원 정보 업데이트 */
    @PutMapping
    public ResponseEntity<String> updateMember(@Valid @RequestBody MemberUpdateDto memberUpdateDto,
                                               @AuthenticationPrincipal UserDetails userDetails) {

        memberService.updateMember(memberUpdateDto, userDetails.getUsername());
        return ResponseEntity.ok("회원 정보 업데이트 성공.");
    }

    /* 회원 정보 삭제 */
    @DeleteMapping
    public ResponseEntity<String> deleteMember(@AuthenticationPrincipal UserDetails userDetails) {

        memberService.deleteMember(userDetails.getUsername());
        return ResponseEntity.ok("회원 정보 삭제 성공.");
    }

    /* 아이디 중복 확인 */
    @GetMapping("/check-username")
    public Boolean existsUsernameCheck(@RequestParam String username) {

        return memberService.existsUsernameCheck(username);
    }

    /* 닉네임 중복 확인 */
    @GetMapping("/check-nickname")
    public Boolean existsNicknameCheck(@RequestParam String nickname) {

        return memberService.existsNicknameCheck(nickname);
    }
}
