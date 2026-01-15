package code.with.vanilson.studentmanagement.modules.notification;

public interface EmailService {
    void sendSimpleMessage(String to, String subject, String text);
}
