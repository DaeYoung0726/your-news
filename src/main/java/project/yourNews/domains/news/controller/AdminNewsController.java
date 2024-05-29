package project.yourNews.domains.news.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.yourNews.domains.news.dto.NewsRequestDto;
import project.yourNews.domains.news.dto.NewsResponseDto;
import project.yourNews.domains.news.service.AdminNewsService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/admin/news")
public class AdminNewsController {

    private final AdminNewsService adminNewsService;

    /* 소식 추가 */
    @PostMapping
    public ResponseEntity<String> saveNews(@Valid @RequestBody NewsRequestDto newsRequestDto) {

        adminNewsService.saveNews(newsRequestDto);
        return ResponseEntity.ok("소식 추가 성공.");
    }

    /* 특정 소식 불러오기 */
    @GetMapping("/{newsId}")
    public NewsResponseDto readNews(@PathVariable Long newsId) {

        return adminNewsService.readNews(newsId);
    }

    /* 소식 삭제하기 */
    @DeleteMapping("/{newsId}")
    public ResponseEntity<String> deleteNews(@PathVariable Long newsId) {

        adminNewsService.deleteNews(newsId);
        return ResponseEntity.ok("소식 삭제 성공.");
    }
}
