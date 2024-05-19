package project.yourNews.domains.category.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.yourNews.domains.post.domain.Post;

import java.util.List;

@Getter
@Builder
public class Person {
    private final String firstName;
    private final String lastName;
    private int age;
    private String email;
}

// 사용 예시

