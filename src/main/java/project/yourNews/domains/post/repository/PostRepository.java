package project.yourNews.domains.post.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import project.yourNews.domains.category.domain.Category;
import project.yourNews.domains.post.domain.Post;

public interface PostRepository extends JpaRepository<Post, Long> {

    Page<Post> findByCategory(Category category, Pageable pageable);

    @Modifying
    @Query("DELETE FROM Post p WHERE p.writer.id = :writerId")
    void deleteByWriterId(@Param("writerId") Long writerId);

    @Query("SELECT p.writer.username FROM Post p WHERE p.id = :postId")
    String findWriterUsernameByPostId(@Param("postId") Long postId);
}
