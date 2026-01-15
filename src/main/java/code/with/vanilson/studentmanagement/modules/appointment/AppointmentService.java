package code.with.vanilson.studentmanagement.modules.appointment;

import code.with.vanilson.studentmanagement.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository repository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Transactional
    public Appointment scheduleAppointment(Long studentId, Long teacherId, LocalDateTime startTime,
            LocalDateTime endTime) {
        Appointment appointment = Appointment.builder()
                .studentId(studentId)
                .teacherId(teacherId)
                .startTime(startTime)
                .endTime(endTime)
                .status(Appointment.AppointmentStatus.SCHEDULED)
                .build();

        Appointment saved = repository.save(appointment);

        kafkaTemplate.send("notification-events",
                "Appointment scheduled for student: " + studentId + " with teacher: " + teacherId);

        return saved;
    }

    @Transactional
    public Appointment cancelAppointment(Long id) {
        Appointment appointment = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("appointment.not_found", id));

        appointment.setStatus(Appointment.AppointmentStatus.CANCELLED);
        Appointment updated = repository.save(appointment);

        kafkaTemplate.send("notification-events", "Appointment cancelled: " + id);

        return updated;
    }

    public List<Appointment> getStudentAppointments(Long studentId) {
        return repository.findByStudentId(studentId);
    }
}
