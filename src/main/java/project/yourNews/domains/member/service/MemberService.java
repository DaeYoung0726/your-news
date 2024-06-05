package project.yourNews.domains.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.yourNews.domains.member.domain.Member;
import project.yourNews.domains.member.dto.MemberInfoDto;
import project.yourNews.domains.member.dto.MemberResponseDto;
import project.yourNews.domains.member.dto.MemberUpdateDto;
import project.yourNews.domains.member.dto.SignUpDto;
import project.yourNews.domains.member.repository.MemberRepository;
import project.yourNews.domains.subNews.service.SubNewsService;
import project.yourNews.handler.exceptionHandler.error.ErrorCode;
import project.yourNews.handler.exceptionHandler.exception.CustomException;
import project.yourNews.utils.redis.RedisUtil;

import java.util.List;
import java.util.stream.Collectors;

import static project.yourNews.utils.redis.RedisProperties.SUB_MEMBER_EXPIRATION_TIME;
import static project.yourNews.utils.redis.RedisProperties.SUB_MEMBER_KEY_PREFIX;

@Slf4j
@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final SubNewsService subNewsService;
    private final RedisUtil redisUtil;
    private static final String USERNAME_PATTERN = "^[ㄱ-ㅎ가-힣a-z0-9-_]{4,20}$";
    private static final String NICKNAME_PATTERN = "^[ㄱ-ㅎ가-힣a-zA-Z0-9-_]{2,10}$";

    /* 회원가입 메서드 */
    @Transactional
    public void signUp(SignUpDto signUpDto) {

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
        memberRepository.delete(findMember);
    }

    /* 특정 소식 구독한 사용자 가져오기 */
    @Transactional(readOnly = true)
    public List<MemberInfoDto> getMembersSubscribedToNews(String newsURL) {

        List<Member> members = memberRepository.findBySubNews_News_NewsURL(newsURL);
        return members.stream().map(MemberInfoDto::new).collect(Collectors.toList());
    }

    /* 캐시를 사용하여 특정 소식을 구독한 사용자 가져오기 */
    @Transactional(readOnly = true)
    public List<MemberInfoDto> getMembersSubscribedToNewsCached(String newsURL) {
        String cacheKey = SUB_MEMBER_KEY_PREFIX + newsURL;

        // 캐시에서 데이터 조회
        List<MemberInfoDto> members = (List<MemberInfoDto>) redisUtil.get(cacheKey);
        if (members == null) {
            // 캐시에 데이터가 없으면 데이터베이스에서 조회하고 캐시에 저장
            members = this.getMembersSubscribedToNews(newsURL);
            redisUtil.set(cacheKey, members);
            redisUtil.expire(cacheKey, SUB_MEMBER_EXPIRATION_TIME);  // 캐시 만료 시간 설정 (1시간)
        }
        return members;
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
