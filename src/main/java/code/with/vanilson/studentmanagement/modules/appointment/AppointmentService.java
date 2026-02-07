package code.with.vanilson.studentmanagement.modules.appointment;

import code.with.vanilson.studentmanagement.common.exception.NotificationException;
import code.with.vanilson.studentmanagement.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentService {
    private final Logger log = LoggerFactory.getLogger(AppointmentService.class);
    private final AppointmentRepository repository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Transactional
    public Appointment scheduleAppointment(Long studentId, Long teacherId, LocalDateTime startTime,
                                           LocalDateTime endTime) {

        List<Appointment> overlapping = repository.findOverlappingAppointments(teacherId, startTime, endTime);
        if (!overlapping.isEmpty()) {
            throw new IllegalStateException("Teacher already has an appointment during this time.");
        }

        Appointment appointment = Appointment.builder()
                .studentId(studentId)
                .teacherId(teacherId)
                .startTime(startTime)
                .endTime(endTime)
                .status(Appointment.AppointmentStatus.SCHEDULED)
                .build();

        Appointment saved = repository.save(appointment);

        try {
            kafkaTemplate.send("notification-events",
                    "Appointment scheduled for student: " + studentId + " with teacher: " + teacherId);
        } catch (KafkaException e) {
            log.error("Failed to make appointment notification event", e);
            throw new NotificationException("Failed to send appointment notification", e);
        }

        return saved;
    }

    @Transactional
    public Appointment cancelAppointment(Long id) {
        Appointment appointment = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("appointment.not_found", id));

        appointment.setStatus(Appointment.AppointmentStatus.CANCELLED);
        Appointment updated = repository.save(appointment);

        try {
            kafkaTemplate.send("notification-events", "Appointment cancelled: " + id);
        } catch (KafkaException e) {
            log.error("Failed to make appointment notification event", e);
            throw new NotificationException("Failed to send appointment notification", e);
        }

        return updated;
    }

    public List<Appointment> getStudentAppointments(Long studentId) {
        return repository.findByStudentId(studentId);
    }
}
