/**
 * Author: vanilson muhongo
 * Date:06/02/2026
 * Time:22:59
 * Version:1
 */

package code.with.vanilson.studentmanagement.modules.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    private final Logger log = LoggerFactory.getLogger(NotificationService.class);
    private final KafkaTemplate<String, String> kafkaTemplate;

    public NotificationService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendAppointmentNotification(Long studentId, Long teacherId) {
        try {
            kafkaTemplate.send("notification-events",
                    "Appointment scheduled for student: " + studentId + " with teacher: " + teacherId);
        } catch (KafkaException e) {
            log.error("Failed to send appointment notification", e);
            // Could implement retry logic or dead letter queue
        }
    }
}
