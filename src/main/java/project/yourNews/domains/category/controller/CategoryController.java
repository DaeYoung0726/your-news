package project.yourNews.domains.category.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.yourNews.common.utils.api.ApiUtil;
import project.yourNews.domains.category.dto.CategoryRequestDto;
import project.yourNews.domains.category.service.CategoryService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/category")
public class CategoryController {

    private final CategoryService categoryService;

    /* 게시글 카테고리 생성 */
    @PostMapping
    public ResponseEntity<?> saveCategory(@Valid @RequestBody CategoryRequestDto categoryRequestDto) {

        categoryService.saveCategory(categoryRequestDto);
        return ResponseEntity.ok(ApiUtil.from("카테고리 추가 성공."));
    }

    /* 게시글 카테고리 삭제 */
    @DeleteMapping("/{categoryName}")
    public ResponseEntity<?> deleteCategory(@PathVariable String categoryName) {

        categoryService.deleteCategory(categoryName);
        return ResponseEntity.ok(ApiUtil.from("카테고리 삭제 성공."));
    }
}
