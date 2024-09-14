package project.yourNews.common.mail.mail.service.strategy;

public interface MailStrategy {
    String getReceiver();
    void setReceiver(String receiver);
    String getSubject();
    String generateContent(String email, String content);
}
