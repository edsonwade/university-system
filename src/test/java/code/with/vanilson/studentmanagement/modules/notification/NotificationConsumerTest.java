package code.with.vanilson.studentmanagement.modules.notification;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationConsumerTest {

    @Mock
    private NotificationRepository repository;

    @Mock
    private AuditLogRepository auditLogRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private NotificationConsumer notificationConsumer;

    @Test
    @DisplayName("Consume Billing Event - Should send email and save notification/audit log")
    void consumeBillingEvent_ShouldSendEmailAndSaveNotificationAndAuditLog() {
        // Arrange
        String message = "Invoice paid: 123";

        // Act
        notificationConsumer.consumeBillingEvent(message);

        // Assert
        verify(emailService, times(1)).sendSimpleMessage(eq("student-email@example.com"), eq("Billing Notification"),
                eq(message));
        verify(repository, times(1)).save(any(Notification.class));
        verify(auditLogRepository, times(1)).save(any(AuditLog.class));
    }

    @Test
    @DisplayName("Consume Notification Event - Should save notification and audit log")
    void consumeNotificationEvent_ShouldSaveNotificationAndAuditLog() {
        // Arrange
        String message = "System update at 10:00 PM";

        // Act
        notificationConsumer.consumeNotificationEvent(message);

        // Assert
        verify(repository, times(1)).save(any(Notification.class));
        verify(auditLogRepository, times(1)).save(any(AuditLog.class));
        verifyNoInteractions(emailService);
    }
}