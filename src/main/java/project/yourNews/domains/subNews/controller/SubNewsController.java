package project.yourNews.domains.subNews.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.yourNews.domains.subNews.service.SubNewsService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SubNewsController {

    private final SubNewsService subNewsService;

    @PutMapping("/v1/users/update-sub-news")
    public ResponseEntity<String> updateSubNews(@RequestBody List<String> newsNames,
                                                @AuthenticationPrincipal UserDetails userDetails) {

        subNewsService.updateUserSubNews(userDetails.getUsername(), newsNames);
        return ResponseEntity.ok("소식 구독 업데이트 성공.");
    }
}
