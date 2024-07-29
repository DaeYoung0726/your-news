package project.yourNews.domains.news.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import project.yourNews.domains.news.dto.NewsInfoDto;
import project.yourNews.domains.news.service.NewsService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class NewsController {

    private final NewsService newsService;

    /* 전체 소식 불러오기 */
    @GetMapping("/v1/news")
    public ResponseEntity<?> readAllNews() {

        return ResponseEntity.ok(newsService.readAllNews());
    }
}
