package project.yourNews.domains.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.yourNews.domains.common.service.AssociatedEntityService;
import project.yourNews.domains.member.domain.Member;
import project.yourNews.domains.member.dto.MemberResponseDto;
import project.yourNews.domains.member.dto.MemberUpdateDto;
import project.yourNews.domains.member.dto.SignUpDto;
import project.yourNews.domains.member.repository.MemberRepository;
import project.yourNews.domains.subNews.service.SubNewsService;
import project.yourNews.common.exception.error.ErrorCode;
import project.yourNews.common.exception.CustomException;
import project.yourNews.common.mail.stibee.service.StibeeService;
import project.yourNews.common.utils.redis.RedisUtil;

import java.util.List;
import java.util.stream.Collectors;

import static project.yourNews.common.utils.redis.RedisProperties.CODE_KEY_PREFIX;

@Slf4j
@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final SubNewsService subNewsService;
    private final AssociatedEntityService associatedEntityService;
    private final StibeeService stibeeService;
    private final RedisUtil redisUtil;
    private static final String USERNAME_PATTERN = "^[ㄱ-ㅎ가-힣a-z0-9-_]{4,20}$";
    private static final String NICKNAME_PATTERN = "^[ㄱ-ㅎ가-힣a-zA-Z0-9-_]{2,10}$";

    /* 회원가입 메서드 */
    @Transactional
    public void signUp(SignUpDto signUpDto) {

        String key = CODE_KEY_PREFIX + signUpDto.getEmail();

        if (!signUpDto.getVerificationCode().equals(redisUtil.get(key))) {  // api 플랫폼 등을 통한 무분별한 가입을 막기 위한 메서드.
            throw new CustomException(ErrorCode.INVALID_CODE);
        }

        redisUtil.del(key); // 인증번호 일치할 시, redis에 저장된 값 삭제

        signUpDto.setPassword(passwordEncoder.encode(signUpDto.getPassword()));
        Member member = signUpDto.toMemberEntity();

        memberRepository.save(member);

        if (signUpDto.getSubNewsNames() != null) {
            for (String subNews : signUpDto.getSubNewsNames()) {     // 소식 구독하기
                subNewsService.saveSubNews(signUpDto.getUsername(), subNews);
            }
        }
    }

    /* 멤버 정보 불러오기 */
    @Transactional(readOnly = true)
    public MemberResponseDto readMember(String username) {

        Member findMember = memberRepository.findByUsername(username).orElseThrow(() ->
                new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        return new MemberResponseDto(findMember);
    }

    /* 멤버 정보 업데이트 */
    @Transactional
    public void updateMember(MemberUpdateDto memberUpdateDto, String username) {

        Member findMember = memberRepository.findByUsername(username).orElseThrow(() ->
                new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        if (!passwordEncoder.matches(memberUpdateDto.getCurrentPassword(), findMember.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_CURRENT_PASSWORD);
        }

        findMember.updateInfo(passwordEncoder.encode(memberUpdateDto.getNewPassword()), memberUpdateDto.getNickname());
    }

    /* 멤버 삭제하기 */
    @Transactional
    public void deleteMember(String username) {

        Member findMember = memberRepository.findByUsername(username).orElseThrow(() ->
                new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        associatedEntityService.deleteAllLikeByMember(findMember);      // 좋아요 연관관계 삭제
        associatedEntityService.deleteAllPostByMember(findMember);      // 게시글 연관관계 삭제
        associatedEntityService.deleteAllSubNewsByMember(findMember);   // 구독 소식 연관관계 삭제

        stibeeService.deleteSubscriber(findMember.getEmail());  //  Stibee 구독 삭제

        memberRepository.delete(findMember);
    }

    /* 특정 소식 구독한 사용자 가져오기 */
    @Transactional(readOnly = true)
    public List<String> getMembersSubscribedToNews(String newsName) {

        List<Member> members = memberRepository.findBySubStatusAndSubNews_News_NewsName(true, newsName);
        return members.stream().map(Member::getEmail).collect(Collectors.toList());
    }

    /* 정보 수신 상태 변경 */
    @Transactional
    public void updateSubStatus(String username, boolean status) {

        Member findMember = memberRepository.findByUsername(username).orElseThrow(() ->
                new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        String email = findMember.getEmail();

        // Stibee 구독 처리 과정
        if (status)         // 수신 허용
            stibeeService.subscribe(email);
        else                // 수신 거부
            stibeeService.deleteSubscriber(email);

        findMember.updateSubStatus(status);
    }

    /* 아이디 중복 확인 */
    public boolean existsUsernameCheck(String username) {

        if(!username.matches(USERNAME_PATTERN)) {
            throw new CustomException(ErrorCode.INVALID_USERNAME_PATTERN);
        }
        return memberRepository.existsByUsername(username);
    }

    /* 닉네임 중복 확인 */
    public boolean existsNicknameCheck(String nickname) {

        if (!nickname.matches(NICKNAME_PATTERN)) {
            throw new CustomException(ErrorCode.INVALID_NICKNAME_PATTERN);
        }
        return memberRepository.existsByNickname(nickname);
    }
}
