package project.yourNews.domains.post.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import project.yourNews.domains.category.domain.Category;
import project.yourNews.domains.post.domain.Post;

public interface PostRepository extends JpaRepository<Post, Long> {

    Page<Post> findByCategory(Category category);
}
