package project.yourNews.security.auth;

import lombok.RequiredArgsConstructor;
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
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if(username == null || username.equals("")) {
            throw new UsernameNotFoundException(username);
        }

        Member memberEntity = memberRepository.findByUsername(username).orElse(null);


        if(memberEntity == null) {
            throw new UsernameNotFoundException("사용자를 찾을수 없습니다.");
        }
        return new CustomDetails(memberEntity);
    }
}
