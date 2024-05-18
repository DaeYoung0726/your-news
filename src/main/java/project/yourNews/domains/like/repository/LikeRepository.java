package project.yourNews.domains.like.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.yourNews.domains.like.domain.Like;
import project.yourNews.domains.member.domain.Member;
import project.yourNews.domains.post.domain.Post;

public interface LikeRepository extends JpaRepository<Like, Long> {

    boolean existsByMemberAndPost(Member member, Post post);
}
