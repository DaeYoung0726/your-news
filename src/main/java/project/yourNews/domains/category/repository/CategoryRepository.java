package project.yourNews.domains.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.yourNews.domains.category.domain.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {

}
