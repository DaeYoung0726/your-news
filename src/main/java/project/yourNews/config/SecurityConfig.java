package project.yourNews.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import project.yourNews.handler.logoutHandler.CustomLogoutHandler;
import project.yourNews.handler.logoutHandler.SuccessLogoutHandler;
import project.yourNews.jwt.filter.JwtAuthenticationFilter;
import project.yourNews.jwt.filter.JwtExceptionFilter;
import project.yourNews.token.tokenBlackList.service.TokenBlackListService;
import project.yourNews.util.jwt.JwtUtil;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;
    private final CustomLogoutHandler customLogoutHandler;
    private final SuccessLogoutHandler successLogoutHandler;
    private final TokenBlackListService tokenBlackListService;

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)

                .formLogin(AbstractHttpConfigurer::disable)

                .httpBasic(AbstractHttpConfigurer::disable)

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(authorize ->
                        authorize
                                .requestMatchers("/", "/index.html", "/api/**").permitAll()
                                .requestMatchers("/admin/**").hasAnyRole("ADMIN")
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
