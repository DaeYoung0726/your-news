package project.yourNews.common.utils.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoder;
import io.jsonwebtoken.io.Encoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import project.yourNews.common.utils.jwt.customBase64Url.CustomBase64UrlDecoder;
import project.yourNews.common.utils.jwt.customBase64Url.CustomBase64UrlEncoder;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.InputStream;
import java.io.OutputStream;
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

    private final Encoder<OutputStream, OutputStream> base64UrlEncoder = new CustomBase64UrlEncoder();
    private final Decoder<InputStream, InputStream> base64UrlDecoder = new CustomBase64UrlDecoder();

    public JwtUtil(@Value("${spring.jwt.secret}") String SECRET_KEY,
                   @Value("${spring.jwt.access-expiration}") long accessExpiration,
                   @Value("${spring.jwt.refresh-expiration}") long refreshExpiration,
                   @Value("${spring.jwt.issuer}") String issuer) {
        this.secretKey = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
        this.accessExpiration = accessExpiration;
        this.refreshExpiration = refreshExpiration;
        this.issuer = issuer;
    }

    /* access Token 발급 */
    public String generateAccessToken(String role, String username, Long userId) {

        return createJwt(createClaims(role, userId), username, accessExpiration);
    }

    /* refresh Token 발급 */
    public String generateRefreshToken(String role, String username, Long userId) {

        return createJwt(createClaims(role, userId), username, refreshExpiration);
    }

    /* claims 생성 */
    private Map<String, Object> createClaims(String role, Long userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        claims.put("userId", userId);
        return claims;
    }

    /* 토큰 생성 */
    private String createJwt(Map<String, Object> claims, String subject, Long expirationTime) {

        return Jwts.builder()
                .subject(subject)
                .claims(claims)
                .b64Url(base64UrlEncoder)
                .issuer(issuer)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(secretKey)
                .compact();
    }

    /* 토큰 정보 불러오기 */
    private Claims parseClaims(String token){
        return Jwts.parser()
                .verifyWith(secretKey)
                .b64Url(base64UrlDecoder)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /* 토큰(claim)에서 username 가져오기 */
    public String getUsername(String token) {
        return parseClaims(token).getSubject();
    }

    /* 토큰(claim)에서 권한(role) 가져오기 */
    public String getRole(String token) {
        return parseClaims(token).get("role", String.class);
    }

    /* 토큰(claim)에 저장된 id 가져오기 */
    public Long getUserId(String token) {
        return parseClaims(token).get("userId", Long.class);
    }

    /* 토큰에 지정한 만료 시간 확인*/
    public boolean isExpired(String token) {
        return parseClaims(token).getExpiration().before(new Date());
    }

    public boolean isBase64URL(String token) {
        return token.matches("^[0-9A-Za-z-_.]+$");
    }
}