package project.yourNews.domains.category.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import project.yourNews.domains.category.domain.Category;

@Getter @Setter
@NoArgsConstructor
public class CategoryRequestDto {

    @NotBlank(message = "카테고리 명은 필수 입력 값입니다.")
    private String name;

    public Category toCategoryEntity() {
        return Category.builder()
                .name(name)
                .build();
    }
}
