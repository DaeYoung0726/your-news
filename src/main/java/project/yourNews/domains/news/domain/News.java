package project.yourNews.domains.news.domain;

import jakarta.persistence.CascadeType;
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
import project.yourNews.domains.subNews.domain.SubNews;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class News {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(unique = true)
    private String newsName;

    @NotNull
    @Column(unique = true)
    private String newsURL;

    @OneToMany(mappedBy = "news")
    private List<SubNews> memberSubNews;


    public void updateNewsInfo(String newsName, String newsURL) {
        this.newsName = newsName;
        this.newsURL = newsURL;
    }
}
