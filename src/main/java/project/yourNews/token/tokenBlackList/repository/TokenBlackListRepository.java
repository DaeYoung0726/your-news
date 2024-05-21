package project.yourNews.token.tokenBlackList.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.yourNews.token.tokenBlackList.entity.TokenBlackList;

import java.util.Optional;

public interface TokenBlackListRepository extends JpaRepository<TokenBlackList, Long> {

    Optional<TokenBlackList> findByTokenBlackList(String blackList);
}
