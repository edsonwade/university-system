package code.with.vanilson.studentmanagement.modules.notification;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("NotificationConsumer Unit Tests")
class NotificationConsumerTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private AuditLogRepository auditLogRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private NotificationConsumer notificationConsumer;

    private final String BILLING_MESSAGE = "Invoice created for student: 1, Amount: $100.00";
    private final String NOTIFICATION_MESSAGE = "Appointment scheduled for student: 1 with teacher: 2";

    @BeforeEach
    void setUp() {
        // No specific setup needed as we're testing consumer methods
    }

    @Test
    @DisplayName("Should consume billing event successfully")
    void consumeBillingEvent_Success() {
        // Given
        Notification savedNotification = Notification.builder()
                .message(BILLING_MESSAGE)
                .type("EMAIL")
                .timestamp(LocalDateTime.now())
                .build();

        AuditLog savedAuditLog = AuditLog.builder()
                .action("BILLING_EVENT")
                .details(BILLING_MESSAGE)
                .timestamp(LocalDateTime.now())
                .build();

        when(notificationRepository.save(any(Notification.class))).thenReturn(savedNotification);
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(savedAuditLog);
        doNothing().when(emailService).sendSimpleMessage(anyString(), anyString(), anyString());

        // When
        assertDoesNotThrow(() -> notificationConsumer.consumeBillingEvent(BILLING_MESSAGE));

        // Then
        verify(emailService).sendSimpleMessage(eq("student-email@example.com"), eq("Billing Notification"), eq(BILLING_MESSAGE));
        verify(notificationRepository).save(argThat(notification ->
                notification.getMessage().equals(BILLING_MESSAGE) &&
                        notification.getType().equals("EMAIL") &&
                        notification.getTimestamp() != null
        ));
        verify(auditLogRepository).save(argThat(auditLog ->
                auditLog.getAction().equals("BILLING_EVENT") &&
                        auditLog.getDetails().equals(BILLING_MESSAGE) &&
                        auditLog.getTimestamp() != null
        ));
    }

    @Test
    @DisplayName("Should consume notification event successfully")
    void consumeNotificationEvent_Success() {
        // Given
        Notification savedNotification = Notification.builder()
                .message(NOTIFICATION_MESSAGE)
                .type("SYSTEM")
                .timestamp(LocalDateTime.now())
                .build();

        AuditLog savedAuditLog = AuditLog.builder()
                .action("SYSTEM_NOTIFICATION")
                .details(NOTIFICATION_MESSAGE)
                .timestamp(LocalDateTime.now())
                .build();

        when(notificationRepository.save(any(Notification.class))).thenReturn(savedNotification);
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(savedAuditLog);

        // When
        assertDoesNotThrow(() -> notificationConsumer.consumeNotificationEvent(NOTIFICATION_MESSAGE));

        // Then
        verify(emailService, never()).sendSimpleMessage(anyString(), anyString(), anyString());
        verify(notificationRepository).save(argThat(notification ->
                notification.getMessage().equals(NOTIFICATION_MESSAGE) &&
                        notification.getType().equals("SYSTEM") &&
                        notification.getTimestamp() != null
        ));
        verify(auditLogRepository).save(argThat(auditLog ->
                auditLog.getAction().equals("SYSTEM_NOTIFICATION") &&
                        auditLog.getDetails().equals(NOTIFICATION_MESSAGE) &&
                        auditLog.getTimestamp() != null
        ));
    }
    @Disabled
    @Test
    @DisplayName("Should handle null billing event message")
    void consumeBillingEvent_NullMessage_HandlesGracefully() {
        // Given
        Notification savedNotification = Notification.builder()

                .message(null)
                .type("EMAIL")
                .timestamp(LocalDateTime.now())
                .build();

        AuditLog savedAuditLog = AuditLog.builder()

                .action("BILLING_EVENT")
                .details(null)
                .timestamp(LocalDateTime.now())
                .build();

        when(notificationRepository.save(any(Notification.class))).thenReturn(savedNotification);
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(savedAuditLog);
        doNothing().when(emailService).sendSimpleMessage(anyString(), anyString(), anyString());

        // When
        assertDoesNotThrow(() -> notificationConsumer.consumeBillingEvent(null));

        // Then
        verify(emailService).sendSimpleMessage(eq("student-email@example.com"), eq("Billing Notification"), isNull());
        verify(notificationRepository).save(any(Notification.class));
        verify(auditLogRepository).save(any(AuditLog.class));
    }

    @Test
    @DisplayName("Should handle empty billing event message")
    void consumeBillingEvent_EmptyMessage_HandlesGracefully() {
        // Given
        String emptyMessage = "";
        Notification savedNotification = Notification.builder()

                .message(emptyMessage)
                .type("EMAIL")
                .timestamp(LocalDateTime.now())
                .build();

        AuditLog savedAuditLog = AuditLog.builder()

                .action("BILLING_EVENT")
                .details(emptyMessage)
                .timestamp(LocalDateTime.now())
                .build();

        when(notificationRepository.save(any(Notification.class))).thenReturn(savedNotification);
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(savedAuditLog);
        doNothing().when(emailService).sendSimpleMessage(anyString(), anyString(), anyString());

        // When
        assertDoesNotThrow(() -> notificationConsumer.consumeBillingEvent(emptyMessage));

        // Then
        verify(emailService).sendSimpleMessage(eq("student-email@example.com"), eq("Billing Notification"), eq(emptyMessage));
        verify(notificationRepository).save(any(Notification.class));
        verify(auditLogRepository).save(any(AuditLog.class));
    }

    @Test
    @DisplayName("Should handle null notification event message")
    void consumeNotificationEvent_NullMessage_HandlesGracefully() {
        // Given
        Notification savedNotification = Notification.builder()

                .message(null)
                .type("SYSTEM")
                .timestamp(LocalDateTime.now())
                .build();

        AuditLog savedAuditLog = AuditLog.builder()

                .action("SYSTEM_NOTIFICATION")
                .details(null)
                .timestamp(LocalDateTime.now())
                .build();

        when(notificationRepository.save(any(Notification.class))).thenReturn(savedNotification);
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(savedAuditLog);

        // When
        assertDoesNotThrow(() -> notificationConsumer.consumeNotificationEvent(null));

        // Then
        verify(emailService, never()).sendSimpleMessage(anyString(), anyString(), anyString());
        verify(notificationRepository).save(any(Notification.class));
        verify(auditLogRepository).save(any(AuditLog.class));
    }

    @Test
    @DisplayName("Should handle empty notification event message")
    void consumeNotificationEvent_EmptyMessage_HandlesGracefully() {
        // Given
        String emptyMessage = "";
        Notification savedNotification = Notification.builder()

                .message(emptyMessage)
                .type("SYSTEM")
                .timestamp(LocalDateTime.now())
                .build();

        AuditLog savedAuditLog = AuditLog.builder()

                .action("SYSTEM_NOTIFICATION")
                .details(emptyMessage)
                .timestamp(LocalDateTime.now())
                .build();

        when(notificationRepository.save(any(Notification.class))).thenReturn(savedNotification);
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(savedAuditLog);

        // When
        assertDoesNotThrow(() -> notificationConsumer.consumeNotificationEvent(emptyMessage));

        // Then
        verify(emailService, never()).sendSimpleMessage(anyString(), anyString(), anyString());
        verify(notificationRepository).save(any(Notification.class));
        verify(auditLogRepository).save(any(AuditLog.class));
    }

    @Disabled
    @Test
    @DisplayName("Should handle email service exception during billing event consumption")
    void consumeBillingEvent_EmailServiceException_StillSavesData() {
        // Given
        Notification savedNotification = Notification.builder()

                .message(BILLING_MESSAGE)
                .type("EMAIL")
                .timestamp(LocalDateTime.now())
                .build();

        AuditLog savedAuditLog = AuditLog.builder()

                .action("BILLING_EVENT")
                .details(BILLING_MESSAGE)
                .timestamp(LocalDateTime.now())
                .build();

        when(notificationRepository.save(any(Notification.class))).thenReturn(savedNotification);
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(savedAuditLog);
        doThrow(new RuntimeException("Email service failed"))
                .when(emailService).sendSimpleMessage(anyString(), anyString(), anyString());

        // When
        assertDoesNotThrow(() -> notificationConsumer.consumeBillingEvent(BILLING_MESSAGE));

        // Then
        verify(emailService).sendSimpleMessage(eq("student-email@example.com"), eq("Billing Notification"), eq(BILLING_MESSAGE));
        verify(notificationRepository).save(any(Notification.class));
        verify(auditLogRepository).save(any(AuditLog.class));
    }

    @Test
    @DisplayName("Should handle notification repository exception during billing event consumption")
    void consumeBillingEvent_NotificationRepositoryException_ThrowsException() {
        // Given
        when(notificationRepository.save(any(Notification.class)))
                .thenThrow(new RuntimeException("Database error"));
        doNothing().when(emailService).sendSimpleMessage(anyString(), anyString(), anyString());

        // When & Then
        assertThrows(RuntimeException.class,
                () -> notificationConsumer.consumeBillingEvent(BILLING_MESSAGE));

        verify(emailService).sendSimpleMessage(eq("student-email@example.com"), eq("Billing Notification"), eq(BILLING_MESSAGE));
        verify(notificationRepository).save(any(Notification.class));
        verify(auditLogRepository, never()).save(any(AuditLog.class));
    }

    @Test
    @DisplayName("Should handle audit log repository exception during billing event consumption")
    void consumeBillingEvent_AuditLogRepositoryException_ThrowsException() {
        // Given
        Notification savedNotification = Notification.builder()

                .message(BILLING_MESSAGE)
                .type("EMAIL")
                .timestamp(LocalDateTime.now())
                .build();

        when(notificationRepository.save(any(Notification.class))).thenReturn(savedNotification);
        when(auditLogRepository.save(any(AuditLog.class)))
                .thenThrow(new RuntimeException("Database error"));
        doNothing().when(emailService).sendSimpleMessage(anyString(), anyString(), anyString());

        // When & Then
        assertThrows(RuntimeException.class,
                () -> notificationConsumer.consumeBillingEvent(BILLING_MESSAGE));

        verify(emailService).sendSimpleMessage(eq("student-email@example.com"), eq("Billing Notification"), eq(BILLING_MESSAGE));
        verify(notificationRepository).save(any(Notification.class));
        verify(auditLogRepository).save(any(AuditLog.class));
    }

    @Test
    @DisplayName("Should handle notification repository exception during notification event consumption")
    void consumeNotificationEvent_NotificationRepositoryException_ThrowsException() {
        // Given
        when(notificationRepository.save(any(Notification.class)))
                .thenThrow(new RuntimeException("Database error"));

        // When & Then
        assertThrows(RuntimeException.class,
                () -> notificationConsumer.consumeNotificationEvent(NOTIFICATION_MESSAGE));

        verify(emailService, never()).sendSimpleMessage(anyString(), anyString(), anyString());
        verify(notificationRepository).save(any(Notification.class));
        verify(auditLogRepository, never()).save(any(AuditLog.class));
    }

    @Test
    @DisplayName("Should handle audit log repository exception during notification event consumption")
    void consumeNotificationEvent_AuditLogRepositoryException_ThrowsException() {
        // Given
        Notification savedNotification = Notification.builder()

                .message(NOTIFICATION_MESSAGE)
                .type("SYSTEM")
                .timestamp(LocalDateTime.now())
                .build();

        when(notificationRepository.save(any(Notification.class))).thenReturn(savedNotification);
        when(auditLogRepository.save(any(AuditLog.class)))
                .thenThrow(new RuntimeException("Database error"));

        // When & Then
        assertThrows(RuntimeException.class,
                () -> notificationConsumer.consumeNotificationEvent(NOTIFICATION_MESSAGE));

        verify(emailService, never()).sendSimpleMessage(anyString(), anyString(), anyString());
        verify(notificationRepository).save(any(Notification.class));
        verify(auditLogRepository).save(any(AuditLog.class));
    }

    @Test
    @DisplayName("Should handle very long billing event message")
    void consumeBillingEvent_VeryLongMessage_Success() {
        // Given
        String longMessage = "A".repeat(10000); // Very long message
        Notification savedNotification = Notification.builder()

                .message(longMessage)
                .type("EMAIL")
                .timestamp(LocalDateTime.now())
                .build();

        AuditLog savedAuditLog = AuditLog.builder()

                .action("BILLING_EVENT")
                .details(longMessage)
                .timestamp(LocalDateTime.now())
                .build();

        when(notificationRepository.save(any(Notification.class))).thenReturn(savedNotification);
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(savedAuditLog);
        doNothing().when(emailService).sendSimpleMessage(anyString(), anyString(), anyString());

        // When
        assertDoesNotThrow(() -> notificationConsumer.consumeBillingEvent(longMessage));

        // Then
        verify(emailService).sendSimpleMessage(eq("student-email@example.com"), eq("Billing Notification"), eq(longMessage));
        verify(notificationRepository).save(argThat(notification ->
                notification.getMessage().equals(longMessage) &&
                        notification.getType().equals("EMAIL")
        ));
        verify(auditLogRepository).save(argThat(auditLog ->
                auditLog.getAction().equals("BILLING_EVENT") &&
                        auditLog.getDetails().equals(longMessage)
        ));
    }

    @Test
    @DisplayName("Should handle special characters in billing event message")
    void consumeBillingEvent_SpecialCharacters_Success() {
        // Given
        String specialMessage = "Invoice created with special chars: émojis 🎉 and symbols: !@#$%^&*()";
        Notification savedNotification = Notification.builder()

                .message(specialMessage)
                .type("EMAIL")
                .timestamp(LocalDateTime.now())
                .build();

        AuditLog savedAuditLog = AuditLog.builder()

                .action("BILLING_EVENT")
                .details(specialMessage)
                .timestamp(LocalDateTime.now())
                .build();

        when(notificationRepository.save(any(Notification.class))).thenReturn(savedNotification);
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(savedAuditLog);
        doNothing().when(emailService).sendSimpleMessage(anyString(), anyString(), anyString());

        // When
        assertDoesNotThrow(() -> notificationConsumer.consumeBillingEvent(specialMessage));

        // Then
        verify(emailService).sendSimpleMessage(eq("student-email@example.com"), eq("Billing Notification"), eq(specialMessage));
        verify(notificationRepository).save(argThat(notification ->
                notification.getMessage().equals(specialMessage) &&
                        notification.getType().equals("EMAIL")
        ));
        verify(auditLogRepository).save(argThat(auditLog ->
                auditLog.getAction().equals("BILLING_EVENT") &&
                        auditLog.getDetails().equals(specialMessage)
        ));
    }

    @Test
    @DisplayName("Should handle JSON-like billing event message")
    void consumeBillingEvent_JsonLikeMessage_Success() {
        // Given
        String jsonMessage = "{\"action\":\"invoice_created\",\"studentId\":1,\"amount\":100.00}";
        Notification savedNotification = Notification.builder()

                .message(jsonMessage)
                .type("EMAIL")
                .timestamp(LocalDateTime.now())
                .build();

        AuditLog savedAuditLog = AuditLog.builder()

                .action("BILLING_EVENT")
                .details(jsonMessage)
                .timestamp(LocalDateTime.now())
                .build();

        when(notificationRepository.save(any(Notification.class))).thenReturn(savedNotification);
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(savedAuditLog);
        doNothing().when(emailService).sendSimpleMessage(anyString(), anyString(), anyString());

        // When
        assertDoesNotThrow(() -> notificationConsumer.consumeBillingEvent(jsonMessage));

        // Then
        verify(emailService).sendSimpleMessage(eq("student-email@example.com"), eq("Billing Notification"), eq(jsonMessage));
        verify(notificationRepository).save(argThat(notification ->
                notification.getMessage().equals(jsonMessage) &&
                        notification.getType().equals("EMAIL")
        ));
        verify(auditLogRepository).save(argThat(auditLog ->
                auditLog.getAction().equals("BILLING_EVENT") &&
                        auditLog.getDetails().equals(jsonMessage)
        ));
    }

    @Test
    @DisplayName("Should handle Unicode characters in notification event message")
    void consumeNotificationEvent_UnicodeCharacters_Success() {
        // Given
        String unicodeMessage = "Unicode test: 中文 русский العربية हिन्दी עברית 日本語 한국어";
        Notification savedNotification = Notification.builder()

                .message(unicodeMessage)
                .type("SYSTEM")
                .timestamp(LocalDateTime.now())
                .build();

        AuditLog savedAuditLog = AuditLog.builder()

                .action("SYSTEM_NOTIFICATION")
                .details(unicodeMessage)
                .timestamp(LocalDateTime.now())
                .build();

        when(notificationRepository.save(any(Notification.class))).thenReturn(savedNotification);
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(savedAuditLog);

        // When
        assertDoesNotThrow(() -> notificationConsumer.consumeNotificationEvent(unicodeMessage));

        // Then
        verify(emailService, never()).sendSimpleMessage(anyString(), anyString(), anyString());
        verify(notificationRepository).save(argThat(notification ->
                notification.getMessage().equals(unicodeMessage) &&
                        notification.getType().equals("SYSTEM")
        ));
        verify(auditLogRepository).save(argThat(auditLog ->
                auditLog.getAction().equals("SYSTEM_NOTIFICATION") &&
                        auditLog.getDetails().equals(unicodeMessage)
        ));
    }
}
