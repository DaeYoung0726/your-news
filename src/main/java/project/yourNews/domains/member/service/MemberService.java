package project.yourNews.domains.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.yourNews.domains.member.domain.Member;
import project.yourNews.domains.member.dto.MemberResponseDto;
import project.yourNews.domains.member.dto.MemberUpdateDto;
import project.yourNews.domains.member.dto.SignUpDto;
import project.yourNews.domains.member.repository.MemberRepository;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    /* 회원가입 메서드 */
    @Transactional
    public void signUp(SignUpDto signUpDto) {

        signUpDto.setPassword(passwordEncoder.encode(signUpDto.getPassword()));
        Member member = signUpDto.toMemberEntity();

        memberRepository.save(member);
    }

    /* 멤버 정보 불러오기 */
    @Transactional(readOnly = true)
    public MemberResponseDto readMember(String username) {

        Member findMember = memberRepository.findByUsername(username).orElseThrow(() ->
                new IllegalArgumentException("존재하지 않는 회원입니다.: " + username));

        return new MemberResponseDto(findMember);
    }

    /* 멤버 정보 업데이트 */
    @Transactional
    public void updateMember(MemberUpdateDto memberUpdateDto, String username) {

        Member findMember = memberRepository.findByUsername(username).orElseThrow(() ->
                new IllegalArgumentException("존재하지 않는 회원입니다.: " + username));
        findMember.updateInfo(passwordEncoder.encode(memberUpdateDto.getPassword()), memberUpdateDto.getNickname());
    }

    /* 멤버 삭제하기 */
    @Transactional
    public void deleteMember(String username) {

        Member findMember = memberRepository.findByUsername(username).orElseThrow(() ->
                new IllegalArgumentException("존재하지 않는 회원입니다.: " + username));
        memberRepository.delete(findMember);
    }

    /* 특정 소식 구독한 사용자 가져오기 */
    @Transactional(readOnly = true)
    public List<Member> getMembersSubscribedToNews(Long newsId) {

        return memberRepository.findBySubNews_NewsId(newsId);
    }

    /* 아이디 중복 확인 */
    public boolean existsUsernameCheck(String username) {
        /*  글로벌 에러 처리할 때 바꾸기
        if(!username.matches("^[ㄱ-ㅎ가-힣a-z0-9-_]{4,20}$")) {
            return "아이디는 특수문자를 제외한 4~20자리여야 합니다.";
        }
        */

        return memberRepository.existsByUsername(username);
    }

    /* 닉네임 중복 확인 */
    public boolean existsNicknameCheck(String nickname) {
        /*  글로벌 에러 처리할 때 바꾸기
        if (!nickname.matches("^[ㄱ-ㅎ가-힣a-zA-Z0-9-_]{2,10}$")) {
            return "닉네임은 특수문자를 제외한 2~10자리여야 합니다.";
        }
         */
        return memberRepository.existsByNickname(nickname);
    }
}
