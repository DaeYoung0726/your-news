package project.yourNews.domains.like.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import project.yourNews.domains.like.domain.Like;
import project.yourNews.domains.member.domain.Member;
import project.yourNews.domains.post.domain.Post;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {

    Optional<Like> findByMemberAndPost(Member member, Post post);
    boolean existsByMemberAndPost(Member member, Post post);

    @Transactional
    @Modifying
    @Query("delete from Likes l where l.id in :ids")
    void deleteAllLikeByIdInQuery(@Param("ids") List<Long> ids);
}
