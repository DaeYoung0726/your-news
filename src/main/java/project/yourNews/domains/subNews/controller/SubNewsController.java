package project.yourNews.domains.subNews.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import project.yourNews.domains.subNews.dto.SubNewsUpdateDto;
import project.yourNews.domains.subNews.service.SubNewsService;
import project.yourNews.common.utils.api.ApiUtil;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SubNewsController {

    private final SubNewsService subNewsService;

    @PutMapping("/v1/users/update-sub-news")
    public ResponseEntity<?> updateSubNews(@RequestBody SubNewsUpdateDto updateDto,
                                                @AuthenticationPrincipal UserDetails userDetails) {

        subNewsService.updateUserSubNews(userDetails.getUsername(), updateDto);
        return ResponseEntity.ok(ApiUtil.from("소식 구독 업데이트 성공."));
    }
}
