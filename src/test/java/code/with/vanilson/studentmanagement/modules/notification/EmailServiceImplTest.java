package code.with.vanilson.studentmanagement.modules.notification;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceImplTest {

    @Mock
    private JavaMailSender emailSender;

    @InjectMocks
    private EmailServiceImpl emailService;

    @Test
    @DisplayName("Send Simple Message - Should send email with correct details")
    void sendSimpleMessage_ShouldSendEmailWithCorrectDetails() {
        // Arrange
        String to = "test@example.com";
        String subject = "Test Subject";
        String text = "Test Body";

        // Act
        emailService.sendSimpleMessage(to, subject, text);

        // Assert
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(emailSender, times(1)).send(messageCaptor.capture());

        SimpleMailMessage sentMessage = messageCaptor.getValue();
        assertEquals(to, Objects.requireNonNull(sentMessage.getTo())[0]);
        assertEquals(subject, sentMessage.getSubject());
        assertEquals(text, sentMessage.getText());
        assertEquals("your-outlook-email@outlook.com", sentMessage.getFrom());
    }

    @Test
    @DisplayName("Send Simple Message - Should handle exception when email sending fails")
    void sendSimpleMessage_ShouldHandleException_WhenEmailSendingFails() {
        // Arrange
        String to = "test@example.com";
        String subject = "Test Subject";
        String text = "Test Body";

        doThrow(new RuntimeException("Mail server down")).when(emailSender).send(any(SimpleMailMessage.class));

        // Act
        emailService.sendSimpleMessage(to, subject, text);

        // Assert
        verify(emailSender, times(1)).send(any(SimpleMailMessage.class));
        // No exception should be thrown to the caller as it's caught in the service
    }
}