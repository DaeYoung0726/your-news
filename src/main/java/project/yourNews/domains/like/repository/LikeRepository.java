package project.yourNews.domains.like.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import project.yourNews.domains.like.domain.Like;
import project.yourNews.domains.member.domain.Member;
import project.yourNews.domains.post.domain.Post;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {

    Optional<Like> findByMember_UsernameAndPost_Id(String username, Long postId);
    boolean existsByMemberAndPost(Member member, Post post);

    @Modifying
    @Query("DELETE FROM Likes l WHERE l.member.id = :memberId")
    void deleteByMemberId(@Param("memberId") Long memberId);

    @Modifying
    @Query("DELETE FROM Likes l WHERE l.post.id IN (SELECT p.id FROM Post p WHERE p.writer.id = :writerId)")
    void deletePostLikeByWriterId(@Param("writerId") Long writerId);

    @Modifying
    @Query("DELETE FROM Likes l WHERE l.post.id = :postId")
    void deleteByPostId(@Param("postId") Long postId);
}
