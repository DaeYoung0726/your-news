package project.yourNews.domains.subNews.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.yourNews.domains.member.domain.Member;
import project.yourNews.domains.subNews.domain.SubNews;

import java.util.List;

public interface SubNewsRepository extends JpaRepository<SubNews, Long> {

    List<SubNews> findByMember(Member member);
}
