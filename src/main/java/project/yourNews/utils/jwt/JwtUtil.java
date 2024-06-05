package project.yourNews.utils.jwt;

import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    private final SecretKey secretKey;
    private final long accessExpiration;
    private final long refreshExpiration;
    private final String issuer;

    public JwtUtil(@Value("${spring.jwt.secret}") String SECRET_KEY,
                   @Value("${spring.jwt.access-expiration}") long accessExpiration,
                   @Value("${spring.jwt.refresh-expiration}") long refreshExpiration,
                   @Value("${spring.jwt.issuer") String issuer) {
        this.secretKey = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
        this.accessExpiration = accessExpiration;
        this.refreshExpiration = refreshExpiration;
        this.issuer = issuer;
    }

    /* 토큰(claim)에서 username 가져오기 */
    public String getUsername(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getSubject();
    }

    /* 토큰(claim)에서 권한(role) 가져오기 */
    public String getRole(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("role", String.class);
    }

    /* 토큰(claim)에 저장된 category가 refresh, access인지 확인 */
    public String getCategory(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("category", String.class);
    }

    /* 토큰에 지정한 만료 시간 확인*/
    public boolean isExpired(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
    }

    /* access Token 발급 */
    public String generateAccessToken(String role, String username) {

        Map<String, Object> claims = new HashMap<>();
        claims.put("category", "access");
        claims.put("role", role);

        return createJwt(claims, username, accessExpiration);
    }

    /* refresh Token 발급 */
    public String generateRefreshToken(String role, String username) {

        Map<String, Object> claims = new HashMap<>();
        claims.put("category", "refresh");
        claims.put("role", role);

        return createJwt(claims, username, refreshExpiration);
    }

    /* 토큰 생성 */
    private String createJwt(Map<String, Object> claims, String subject, Long expirationTime) {

        return Jwts.builder()
                .subject(subject)
                .claims(claims)
                .issuer(issuer)
                .issuedAt(new Date(System.currentTimeMillis()))     // JWT의 발행 시간을 설정
                .expiration(new Date(System.currentTimeMillis() + expirationTime))  // 만료 시간 설정.
                .signWith(secretKey)        //  JWT에 서명을 추가. JWT의 무결성을 보장하기 위해 사용.
                .compact();     // 설정된 정보를 기반으로 JWT를 생성하고 문자열로 직렬화.
    }

}