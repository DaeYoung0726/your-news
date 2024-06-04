package project.yourNews.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;
import project.yourNews.domains.member.domain.Role;
import project.yourNews.handler.logoutHandler.CustomLogoutHandler;
import project.yourNews.handler.logoutHandler.SuccessLogoutHandler;
import project.yourNews.jwt.filter.JwtAuthenticationFilter;
import project.yourNews.jwt.filter.JwtExceptionFilter;
import project.yourNews.token.tokenBlackList.TokenBlackListService;
import project.yourNews.utils.jwt.JwtUtil;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtUtil jwtUtil;
    private final CorsFilter corsFilter;
    private final ObjectMapper objectMapper;
    private final CustomLogoutHandler customLogoutHandler;
    private final SuccessLogoutHandler successLogoutHandler;
    private final TokenBlackListService tokenBlackListService;

    private static final String[] PUBLIC_ENDPOINTS = {"/js/**", "/css/**", "/",
            "/v1/users/check-username", "/v1/news",
            "/v1/email/**", "/v1/users/check-nickname", "/v1/auth/**",
            "/v1/*/posts", "/*.html", "/adm/*.html"};
    private static final String[] ANONYMOUS_ENDPOINTS = {"/v1/users"};
    private static final String[] ADMIN_ENDPOINTS = {"/v1/admin/**"};

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)

                .addFilter(corsFilter)

                .formLogin(AbstractHttpConfigurer::disable)

                .httpBasic(AbstractHttpConfigurer::disable)

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(authorize ->
                        authorize
                                .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                                .requestMatchers(HttpMethod.POST, ANONYMOUS_ENDPOINTS).anonymous()
                                .requestMatchers(ADMIN_ENDPOINTS).hasAnyRole(String.valueOf(Role.ADMIN))
                                .anyRequest().authenticated())

                .addFilterBefore(new JwtAuthenticationFilter(jwtUtil, objectMapper, tokenBlackListService),
                        UsernamePasswordAuthenticationFilter.class)

                .addFilterBefore(new JwtExceptionFilter(objectMapper), JwtAuthenticationFilter.class)

                .logout(logout ->
                        logout
                                .addLogoutHandler(customLogoutHandler)
                                .logoutSuccessHandler(successLogoutHandler));


        return http.build();
    }
}
