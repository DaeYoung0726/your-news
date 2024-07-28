package project.yourNews.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import project.yourNews.domains.post.domain.Post;
import project.yourNews.domains.post.repository.PostRepository;
import project.yourNews.handler.exceptionHandler.error.ErrorCode;
import project.yourNews.handler.exceptionHandler.exception.CustomException;

import java.util.UUID;

@Aspect
@Component
@RequiredArgsConstructor
public class VerifyAuthenticationAspect {

    private final PostRepository postRepository;

    @Around("@annotation(project.yourNews.aop.annotation.VerifyAuthentication)")
    public Object verifyAuthentication(ProceedingJoinPoint joinPoint) throws Throwable {

        Object[] args = joinPoint.getArgs();

        UserDetails principalDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String expectedUsername = principalDetails.getUsername();

        for (Object arg : args) {
            if (arg instanceof Long) {
                Long postId = (Long) arg;
                postVerifyAuthentication(expectedUsername, postId);
                break;
            }
        }
        return joinPoint.proceed();
    }

    /* 게시글에 대한 권환 확인 메서드 */
    private void postVerifyAuthentication(String expectedUsername, Long postId) {

        String actualUsername = postRepository.findWriterUsernameByPostId(postId);
        if (actualUsername == null) {
            throw new CustomException(ErrorCode.POST_NOT_FOUND);
        }

        if (!expectedUsername.equals(actualUsername))
            throw new CustomException(ErrorCode.UNAUTHORIZED);
    }
}