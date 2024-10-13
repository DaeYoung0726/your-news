package project.yourNews.security.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import project.yourNews.domains.member.domain.Member;
import project.yourNews.domains.member.repository.MemberRepository;

@Service
@RequiredArgsConstructor
public class CustomDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    @Cacheable(value = "users", key = "#userId")
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        if (userId == null || userId.equals("")) {
            throw new UsernameNotFoundException(userId);
        }

        Member memberEntity = memberRepository.findById(Long.valueOf(userId)).orElseThrow(() ->
                new UsernameNotFoundException("User not found with userId: " + userId));

        return new CustomDetails(memberEntity);
    }
}
