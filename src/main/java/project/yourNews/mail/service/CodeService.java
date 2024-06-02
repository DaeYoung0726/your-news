package project.yourNews.mail.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import project.yourNews.domains.member.repository.MemberRepository;
import project.yourNews.handler.exceptionHandler.error.ErrorCode;
import project.yourNews.handler.exceptionHandler.exception.CustomException;
import project.yourNews.mail.MailType;
import project.yourNews.mail.dto.CodeDto;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
@Service
public class CodeService {

    private final MailService mailService;
    private final MemberRepository memberRepository;
    private final Map<String, CodeDto> codeMap = new ConcurrentHashMap<>();
    private static final int VERIFICATION_EXPIRATION_MINUTES = 3; // 이메일 인증 유효 시간 3분
    private static final int RESEND_VALIDITY_MINUTES = 1; // 이메일 재전송 유효 시간 1분

    /* 회원가입 인증번호 확인 메서드. */
    public void sendCodeToMail(String email) {

        if(!checkEmail(email)) {
            throw new CustomException(ErrorCode.INVALID_MAIL_ADDRESS);
        }

        if(!checkRetryEmail(email)) {
            throw new CustomException(ErrorCode.ALREADY_MAIL_REQUEST);
        }

        String authCode = createCode();

        mailService.sendMail(email, authCode, MailType.CODE);

        codeMap.put(email, new CodeDto(authCode, LocalDateTime.now()));
    }

    /* 이메일 검증 */
    private boolean checkEmail(String email) {
        return email != null && !email.equals("");
    }

    /* 인증번호 재전송 시간 확인 */
    private boolean checkRetryEmail(String email) {

        CodeDto storedData = codeMap.get(email);
        if (storedData != null) {
            LocalDateTime currentTime = LocalDateTime.now();
            LocalDateTime expirationTime = storedData.getCreateTime().plusMinutes(RESEND_VALIDITY_MINUTES); // 이전에 보낸 메일로부터 1분이 지났는지 확인

            if (currentTime.isBefore(expirationTime)) {
                // 이전에 보낸 메일로부터 1분이 지나지 않았으면 메일을 보낼 수 없음
                return false;
            } else {
                // 이전에 보낸 메일로부터 1분이 지났으면 해당 데이터 삭제
                codeMap.remove(email);
            }
        }

        return true;
    }

    /* 인증번호 만드는 메서드. */
    private String createCode() {
        try {
            Random random = SecureRandom.getInstanceStrong();   // 암호학적으로 안전한 무작위 수를 생성. 인증번호는 보안적으로 중요하기 SecureRandom 사용.
            StringBuilder sb = new StringBuilder();
            for(int i = 0; i < 6; i++) {
                sb.append(random.nextInt(10));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to generate secure random number", e);
        }
    }

    /* 인증번호 확인 메서드. */
    public boolean verifiedCode(String email, String code) {

        if (memberRepository.existsByEmail(email)) {
            throw new CustomException(ErrorCode.ALREADY_EXISTS_MAIL);
        }

        CodeDto storedCode = codeMap.get(email);
        if(storedCode != null && storedCode.getCode().equals(code)) {
            LocalDateTime expirationTime = storedCode.getCreateTime().plusMinutes(VERIFICATION_EXPIRATION_MINUTES);

            if (LocalDateTime.now().isBefore(expirationTime)) {     // 유효 시간이 지나지 않았다면
                codeMap.remove(email);      // 인증코드가 맞다면 인증코드 삭제.
                return true;
            } else {
                codeMap.remove(email); // 유효 기간이 지났으면 해당 데이터 삭제
                throw new CustomException(ErrorCode.CODE_EXPIRED);
            }
        }
        throw new CustomException(ErrorCode.INVALID_CODE);  // 인증번호 일치하지 않으면
    }

}
