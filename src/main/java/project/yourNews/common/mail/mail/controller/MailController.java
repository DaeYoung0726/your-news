package project.yourNews.common.mail.mail.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import project.yourNews.common.mail.mail.service.AskService;
import project.yourNews.common.mail.mail.service.CodeService;
import project.yourNews.domains.bannedEmail.service.BannedEmailService;
import project.yourNews.common.exception.error.ErrorCode;
import project.yourNews.common.exception.CustomException;
import project.yourNews.common.mail.mail.dto.AskDto;
import project.yourNews.common.mail.stibee.service.StibeeService;
import project.yourNews.common.utils.api.ApiUtil;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/email")
public class MailController {

    private final CodeService codeService;
    private final AskService askService;
    private final BannedEmailService bannedEmailService;
    private final StibeeService stibeeService;

    /* 이메일 인증 번호 보내기 */
    @PostMapping("/verification-request")
    public ResponseEntity<?> sendCodeToMail(@RequestParam("email") @Valid @Email String email) {

        if (bannedEmailService.checkBannedEmail(email))
            throw new CustomException(ErrorCode.BANNED_EMAIL);

        stibeeService.subscribe(email);     // Stibee에 메일 구독하기

        codeService.sendCodeToMail(email);
        return ResponseEntity.ok(ApiUtil.from("인증메일 보내기 성공."));
    }

    /* 인증 번호 확인 */
    @PostMapping("/code-verification")
    public ResponseEntity<?> verifyCode(@RequestParam(value = "email") @Valid @Email String email,
                             @RequestParam("code") String code) {

        return ResponseEntity.ok(ApiUtil.from(codeService.verifiedCode(email, code)));
    }

    /* 문의하기 */
    @PostMapping("/ask")
    public ResponseEntity<?> askToAdmin(@RequestBody AskDto askDto) {

        askService.askToAdmin(askDto);
        return ResponseEntity.ok(ApiUtil.from("문의하기 성공."));
    }
}
