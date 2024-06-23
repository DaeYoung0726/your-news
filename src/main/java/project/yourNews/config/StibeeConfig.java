package project.yourNews.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

@Configuration
public class StibeeConfig {

    @Value("${stibee.key}")
    private String apiKey;

    /* restTemplate 등록 (사용할 때 자동으로 헤더 값 주입) */
    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add((request, body, execution) -> {
            HttpHeaders headers = request.getHeaders();
            headers.set("AccessToken", apiKey);
            headers.set("Content-Type", "application/json");
            return execution.execute(request, body);
        });
        return restTemplate;
    }
}
