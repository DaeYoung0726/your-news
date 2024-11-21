package project.yourNews.domains.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.yourNews.domains.member.domain.BannedEmail;
import project.yourNews.domains.member.dto.BannedEmailRequestDto;
import project.yourNews.domains.member.repository.BannedEmailRepository;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class BannedEmailService {

    private final BannedEmailRepository bannedEmailRepository;

    /* 이메일 ban */
    @Transactional
    public void setBannedEmail(BannedEmailRequestDto bannedEmailDto) {

        bannedEmailDto.setBannedAt(LocalDateTime.now());

        BannedEmail bannedEmail = bannedEmailDto.toBannedEntity();

        bannedEmailRepository.save(bannedEmail);
    }

    /* 금지된 이메일인지 확인 */
    @Transactional(readOnly = true)
    public boolean checkBannedEmail(String email) {

        return bannedEmailRepository.existsByEmail(email);
    }
}
