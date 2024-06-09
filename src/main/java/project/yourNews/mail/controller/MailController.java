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
import project.yourNews.mail.dto.AskDto;
import project.yourNews.mail.service.AskService;
import project.yourNews.mail.service.CodeService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/email")
public class MailController {

    private final CodeService codeService;
    private final AskService askService;

    /* 이메일 인증 번호 보내기 */
    @PostMapping("/verification-request")
    public ResponseEntity<String> sendCodeToMail(@RequestParam("email") @Valid @Email String email) {

        codeService.sendCodeToMail(email);
        return ResponseEntity.ok("인증메일 보내기 성공.");
    }

    /* 인증 번호 확인 */
    @PostMapping("/code-verification")
    public Boolean verifyCode(@RequestParam(value = "email") @Valid @Email String email,
                             @RequestParam("code") String code) {

        return codeService.verifiedCode(email, code);
    }

    /* 문의하기 */
    @PostMapping("/ask")
    public ResponseEntity<String> askToAdmin(@RequestBody AskDto askDto) {

        askService.askToAdmin(askDto);
        return ResponseEntity.ok("문의하기 성공.");
    }
}
