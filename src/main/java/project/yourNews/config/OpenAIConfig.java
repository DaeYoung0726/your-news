package project.yourNews.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

@Configuration
public class OpenAIConfig {

    @Value("${openai.api.key}")
    private String openAiKey;

    @Bean
    public RestTemplate openAiRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add((request, body, execution) -> {
            HttpHeaders headers = request.getHeaders();
            headers.set("Authorization", "Bearer " + openAiKey);
            headers.set("Content-Type", "application/json");
            return execution.execute(request, body);
        });
        return restTemplate;
    }
}