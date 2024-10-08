package project.yourNews.domains.news.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.yourNews.common.utils.api.ApiUtil;
import project.yourNews.domains.news.dto.NewsRequestDto;
import project.yourNews.domains.news.service.AdminNewsService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/admin/news")
public class AdminNewsController {

    private final AdminNewsService adminNewsService;

    /* 페이징 처리 후 소식 불러오기 */
    @GetMapping
    public ResponseEntity<?> readAllNews(@PageableDefault(sort = "newsName", direction = Sort.Direction.ASC)
                                             Pageable pageable) {

        return ResponseEntity.ok(adminNewsService.readAllNews(pageable));
    }

    /* 소식 추가 */
    @PostMapping
    public ResponseEntity<?> saveNews(@Valid @RequestBody NewsRequestDto newsRequestDto) {

        adminNewsService.saveNews(newsRequestDto);
        return ResponseEntity.ok(ApiUtil.from("소식 추가 성공."));
    }

    /* 특정 소식 불러오기 */
    @GetMapping("/{newsId}")
    public ResponseEntity<?> readNews(@PathVariable Long newsId) {

        return ResponseEntity.ok(adminNewsService.readNews(newsId));
    }

    /* 소식 삭제하기 */
    @DeleteMapping("/{newsId}")
    public ResponseEntity<?> deleteNews(@PathVariable Long newsId) {

        adminNewsService.deleteNews(newsId);
        return ResponseEntity.ok(ApiUtil.from("소식 삭제 성공."));
    }
}
