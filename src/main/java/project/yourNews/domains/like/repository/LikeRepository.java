package project.yourNews.domains.like.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.yourNews.domains.like.domain.Like;
import project.yourNews.domains.member.domain.Member;
import project.yourNews.domains.post.domain.Post;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {

    Optional<Like> findByMemberAndPost(Member member, Post post);
    boolean existsByMemberAndPost(Member member, Post post);
}
