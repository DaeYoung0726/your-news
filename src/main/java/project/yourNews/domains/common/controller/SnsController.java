package project.yourNews.domains.common.controller;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.ConfirmSubscriptionRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import project.yourNews.domains.bannedEmail.dto.BannedEmailRequestDto;
import project.yourNews.domains.bannedEmail.service.BannedEmailService;
import project.yourNews.domains.member.service.MemberService;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class SnsController {

    private final MemberService memberService;
    private final BannedEmailService bannedEmailService;
    private final AmazonSNS amazonSNS;
    private final ObjectMapper objectMapper;


    /* SNS 반송 및 수신 거부 이벤트 처리 */
    @PostMapping("/sns/notifications")
    public ResponseEntity<Void> handleSNSNotification(@RequestBody String snsNotification) {
        try {
            JsonNode snsMessage = objectMapper.readTree(snsNotification);
            String messageType = snsMessage.get("Type").asText();

            if ("SubscriptionConfirmation".equals(messageType)) {
                String token = snsMessage.get("Token").asText();
                String topicArn = snsMessage.get("TopicArn").asText();

                amazonSNS.confirmSubscription(new ConfirmSubscriptionRequest()
                        .withTopicArn(topicArn)
                        .withToken(token));

            } else if ("Notification".equals(messageType)) {
                JsonNode notification = objectMapper.readTree(snsMessage.get("Message").asText());
                String notificationType = notification.get("notificationType").asText();

                if ("Bounce".equals(notificationType)) {
                    handleBounce(notification.get("bounce"));
                } else if ("Complaint".equals(notificationType)) {
                    handleComplaint(notification.get("complaint"));
                }
            }
        } catch (IOException e) {
        }

        return ResponseEntity.ok().build();
    }

    /* 반송 처리 */
    private void handleBounce(JsonNode bounce) {
        for (JsonNode bouncedRecipient : bounce.get("bouncedRecipients")) {
            String emailAddress = bouncedRecipient.get("emailAddress").asText();

            BannedEmailRequestDto bannedEmailDto = new BannedEmailRequestDto();
            bannedEmailDto.setEmail(emailAddress);
            bannedEmailService.setBannedEmail(bannedEmailDto);
        }
    }

    /* 수신거부 처리 */
    private void handleComplaint(JsonNode complaint) {
        for (JsonNode complainedRecipient : complaint.get("complainedRecipients")) {
            String emailAddress = complainedRecipient.get("emailAddress").asText();

            memberService.updateSubStatus(emailAddress, false);
        }
    }
}
