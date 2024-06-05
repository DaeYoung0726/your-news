package project.yourNews.domains.post.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import project.yourNews.domains.category.domain.Category;
import project.yourNews.domains.post.domain.Post;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    Page<Post> findByCategory(Category category, Pageable pageable);

    @Transactional
    @Modifying
    @Query("delete from Post p where p.id in :ids")
    void deleteAllPostByIdInQuery(@Param("ids") List<Long> ids);
}
