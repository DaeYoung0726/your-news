package project.yourNews.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ApiConfig {

    @Value("${openai.api.key}")
    private String openAiKey;

    @Value("${stibee.key}")
    private String stibeeKey;

    @Bean
    public RestTemplate openAiRestTemplate() {
        return createRestTemplate(openAiKey, "Authorization", "Bearer ");
    }

    @Bean
    public RestTemplate stibeeRestTemplate() {
        return createRestTemplate(stibeeKey, "AccessToken", "");
    }

    private RestTemplate createRestTemplate(String apiKey, String headerName, String prefix) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(createInterceptor(apiKey, headerName, prefix));
        return restTemplate;
    }

    private ClientHttpRequestInterceptor createInterceptor(String apiKey, String headerName, String prefix) {
        return (request, body, execution) -> {
            HttpHeaders headers = request.getHeaders();
            headers.set(headerName, prefix + apiKey);
            headers.set("Content-Type", "application/json");
            return execution.execute(request, body);
        };
    }
}
