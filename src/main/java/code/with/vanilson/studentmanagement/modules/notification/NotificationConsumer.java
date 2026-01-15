package code.with.vanilson.studentmanagement.modules.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationConsumer {

        private final NotificationRepository repository;
        private final AuditLogRepository auditLogRepository;
        private final EmailService emailService;

        @KafkaListener(topics = "billing-events", groupId = "notification-group")
        public void consumeBillingEvent(String message) {
                log.info("Received billing event: {}", message);

                // Send real email
                emailService.sendSimpleMessage("student-email@example.com", "Billing Notification", message);

                // Save to MongoDB
                Notification notification = Notification.builder()
                                .message(message)
                                .type("EMAIL") // Defaulting to EMAIL for now
                                .timestamp(LocalDateTime.now())
                                .build();

                repository.save(notification);
                log.info("Notification saved to MongoDB");

                // Audit Log
                AuditLog auditLog = AuditLog.builder()
                                .action("BILLING_EVENT")
                                .details(message)
                                .timestamp(LocalDateTime.now())
                                .build();
                auditLogRepository.save(auditLog);
        }

        @KafkaListener(topics = "notification-events", groupId = "notification-group")
        public void consumeNotificationEvent(String message) {
                log.info("Received notification event: {}", message);

                Notification notification = Notification.builder()
                                .message(message)
                                .type("SYSTEM")
                                .timestamp(LocalDateTime.now())
                                .build();

                repository.save(notification);

                AuditLog auditLog = AuditLog.builder()
                                .action("SYSTEM_NOTIFICATION")
                                .details(message)
                                .timestamp(LocalDateTime.now())
                                .build();
                auditLogRepository.save(auditLog);
        }
}
