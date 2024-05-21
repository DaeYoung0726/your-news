package project.yourNews.token.tokenBlackList.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.yourNews.token.tokenBlackList.entity.TokenBlackList;
import project.yourNews.token.tokenBlackList.repository.TokenBlackListRepository;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class TokenBlackListService {

    private final TokenBlackListRepository tokenBlackListRepository;

    /* Token 블랙리스트 등록 */
    @Transactional
    public void saveBlackList(String accessToken) {

        TokenBlackList blackList = TokenBlackList.builder()
                .tokenBlackList(accessToken)
                .build();

        tokenBlackListRepository.save(blackList);
    }

    /* Token 블랙리스트 존재 확인 */
    @Transactional(readOnly = true)
    public boolean existsBlackListCheck(String accessToken) {

        TokenBlackList findBlackList = tokenBlackListRepository.findByTokenBlackList(accessToken).orElse(null);

        if (findBlackList != null) {
            boolean blackListCheck = findBlackList.getCreatedDate().plusHours(1).isBefore(LocalDateTime.now());
            if (!blackListCheck) {
                this.deleteBlackList(findBlackList);
                return false;
            }
            return true;
        }
        return false;
    }

    /* 블랙리스트에서 삭제 */
    @Transactional
    public void deleteBlackList(TokenBlackList blackList) {

        tokenBlackListRepository.delete(blackList);
    }

}
