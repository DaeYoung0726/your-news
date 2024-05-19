package project.yourNews.domains.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.yourNews.domains.member.domain.Member;
import project.yourNews.domains.member.dto.MemberInfoDto;
import project.yourNews.domains.member.repository.MemberRepository;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {

    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public Page<MemberInfoDto> findAllMembers(Pageable pageable) {

        Page<Member> members = memberRepository.findAll(pageable);

        return members.map(MemberInfoDto::new);
    }
}
