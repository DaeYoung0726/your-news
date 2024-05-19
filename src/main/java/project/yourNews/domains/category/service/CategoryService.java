package project.yourNews.domains.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.yourNews.domains.category.domain.Category;
import project.yourNews.domains.category.dto.CategoryRequestDto;
import project.yourNews.domains.category.repository.CategoryRepository;

@Slf4j
@RequiredArgsConstructor
@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    /* 카테고리 생성 */
    @Transactional
    public void saveCategory(CategoryRequestDto categoryDto) {

        categoryRepository.save(categoryDto.toCategoryEntity());
    }

    /* 카테고리 삭제 */
    @Transactional
    public void deleteCategory(String categoryName) {

        Category findCategory = categoryRepository.findByName(categoryName).orElseThrow(() ->
                new IllegalArgumentException("존재하지 않는 카테고리입니다.: " + categoryName));

        categoryRepository.delete(findCategory);
    }
}
