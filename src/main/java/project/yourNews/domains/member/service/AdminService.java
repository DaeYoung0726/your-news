package project.yourNews.domains.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.yourNews.domains.common.service.AssociatedEntityService;
import project.yourNews.domains.member.domain.Member;
import project.yourNews.domains.member.dto.MemberInfoDto;
import project.yourNews.domains.member.dto.MemberResponseDto;
import project.yourNews.domains.member.repository.MemberRepository;
import project.yourNews.handler.exceptionHandler.error.ErrorCode;
import project.yourNews.handler.exceptionHandler.exception.CustomException;

@Slf4j
@RequiredArgsConstructor
@Service
public class AdminService {

    private final MemberRepository memberRepository;
    private final AssociatedEntityService associatedEntityService;

    /* 사용자 전체 불러오기 */
    @Transactional(readOnly = true)
    public Page<MemberInfoDto> findAllMembers(Pageable pageable) {

        Page<Member> members = memberRepository.findAll(pageable);

        return members.map(MemberInfoDto::new);
    }

    /* id로 특정 사용자 불러오기 */
    @Transactional(readOnly = true)
    public MemberResponseDto readMemberById(Long memberId) {

        Member foundMember = memberRepository.findById(memberId).orElseThrow(() ->
                new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        return new MemberResponseDto(foundMember);
    }

    /* nickname으로 특정 사용자 불러오기 */
    @Transactional(readOnly = true)
    public MemberResponseDto readMemberByNickname(String nickname) {

        Member foundMember = memberRepository.findByNickname(nickname).orElseThrow(() ->
                new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        return new MemberResponseDto(foundMember);
    }

    /* 사용자 탈퇴 */
    @Transactional
    public void dropMember(String email) {

        Member findMember = memberRepository.findByEmail(email).orElseThrow(() ->
                new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        associatedEntityService.deleteAllLikeByMember(findMember);      // 좋아요 연관관계 삭제
        associatedEntityService.deleteAllPostByMember(findMember);      // 게시글 연관관계 삭제
        associatedEntityService.deleteAllSubNewsByMember(findMember);   // 구독 소식 연관관계 삭제

        memberRepository.delete(findMember);
    }
}
