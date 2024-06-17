package project.yourNews.mail.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import project.yourNews.domains.bannedEmail.service.BannedEmailService;
import project.yourNews.handler.exceptionHandler.error.ErrorCode;
import project.yourNews.handler.exceptionHandler.exception.CustomException;
import project.yourNews.mail.dto.AskDto;
import project.yourNews.mail.service.AskService;
import project.yourNews.mail.service.CodeService;
import project.yourNews.utils.api.ApiUtil;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/email")
public class MailController {

    private final CodeService codeService;
    private final AskService askService;
    private final BannedEmailService bannedEmailService;

    /* 이메일 인증 번호 보내기 */
    @PostMapping("/verification-request")
    public ResponseEntity<?> sendCodeToMail(@RequestParam("email") @Valid @Email String email) {

        if (bannedEmailService.checkBannedEmail(email))
            throw new CustomException(ErrorCode.BANNED_EMAIL);

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
