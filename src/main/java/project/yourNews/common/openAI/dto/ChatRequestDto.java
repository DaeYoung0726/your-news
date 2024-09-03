package project.yourNews.common.openAI.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ChatRequestDto {

    private String model;

    private List<Message> messages;

    @Builder
    public ChatRequestDto(String model, List<Message> messages) {
        this.model = model;
        this.messages = messages;
    }

    @Override
    public String toString() {
        return "ChatResponseDto{" +
                "model='" + model + '\'' +
                ", messages=" + messages +
                '}';
    }
}
