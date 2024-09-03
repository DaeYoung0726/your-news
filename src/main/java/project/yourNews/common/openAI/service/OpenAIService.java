package project.yourNews.common.openAI.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import project.yourNews.common.openAI.dto.ChatRequestDto;
import project.yourNews.common.openAI.dto.ChatResponseDto;
import project.yourNews.common.openAI.dto.Message;

import java.util.List;

@Service
public class OpenAIService {

    private final RestTemplate restTemplate;

    @Value("${openai.api.url}")
    private String openAiUrl;

    @Value("${openai.api.model}")
    private String openAiModel;

    public OpenAIService(@Qualifier("openAiRestTemplate") RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ChatResponseDto askQuestion(List<Message> messages) {

        ChatRequestDto request = ChatRequestDto.builder()
                .model(openAiModel)
                .messages(messages)
                .build();

        HttpEntity<ChatRequestDto> entity = new HttpEntity<>(request);
        ResponseEntity<ChatResponseDto> response = restTemplate.exchange(
                openAiUrl,
                HttpMethod.POST,
                entity,
                ChatResponseDto.class
        );

        return response.getBody();
    }
}