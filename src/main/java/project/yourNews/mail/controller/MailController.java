package project.yourNews.mail.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import project.yourNews.mail.service.CodeService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class MailController {

    private final CodeService codeService;

    /* 이메일 인증 번호 보내기 */
    @PostMapping("/email/verification-request")
    public ResponseEntity<String> sendCodeToMail(@RequestParam("email") @Valid @Email String email) {

        codeService.sendCodeToMail(email);
        return ResponseEntity.ok("인증메일 보내기 성공.");
    }

    /* 인증 번호 확인 */
    @PostMapping("/email/code-verification")
    public Boolean verifyCode(@RequestParam(value = "email") @Valid @Email String email,
                             @RequestParam("code") String code) {

        return codeService.verifiedCode(email, code);
    }
}
