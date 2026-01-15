package code.with.vanilson.studentmanagement.modules.appointment;

import code.with.vanilson.studentmanagement.common.exception.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository repository;

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @InjectMocks
    private AppointmentService service;

    @Test
    @DisplayName("Schedule Appointment - Should return Appointment and send Kafka message")
    void scheduleAppointment_ShouldReturnAppointmentAndSendKafkaMessage() {
        // Arrange
        Long studentId = 1L;
        Long teacherId = 2L;
        LocalDateTime startTime = LocalDateTime.now().plusDays(1);
        LocalDateTime endTime = startTime.plusHours(1);

        Appointment appointment = Appointment.builder()
                .studentId(studentId)
                .teacherId(teacherId)
                .startTime(startTime)
                .endTime(endTime)
                .status(Appointment.AppointmentStatus.SCHEDULED)
                .build();
        appointment.setId(1L);

        when(repository.save(any(Appointment.class))).thenReturn(appointment);

        // Act
        Appointment result = service.scheduleAppointment(studentId, teacherId, startTime, endTime);

        // Assert
        assertNotNull(result);
        assertEquals(Appointment.AppointmentStatus.SCHEDULED, result.getStatus());
        verify(repository, times(1)).save(any(Appointment.class));
        verify(kafkaTemplate, times(1)).send(eq("notification-events"), anyString());
    }

    @Test
    @DisplayName("Cancel Appointment - Should return Cancelled Appointment and send Kafka message")
    void cancelAppointment_ShouldReturnCancelledAppointmentAndSendKafkaMessage() {
        // Arrange
        Long appointmentId = 1L;
        Appointment appointment = Appointment.builder()
                .status(Appointment.AppointmentStatus.SCHEDULED)
                .build();
        appointment.setId(appointmentId);

        when(repository.findById(appointmentId)).thenReturn(Optional.of(appointment));
        when(repository.save(any(Appointment.class))).thenReturn(appointment);

        // Act
        Appointment result = service.cancelAppointment(appointmentId);

        // Assert
        assertNotNull(result);
        assertEquals(Appointment.AppointmentStatus.CANCELLED, result.getStatus());
        verify(repository, times(1)).findById(appointmentId);
        verify(repository, times(1)).save(appointment);
        verify(kafkaTemplate, times(1)).send(eq("notification-events"), contains("Appointment cancelled"));
    }

    @Test
    @DisplayName("Cancel Appointment - Should throw ResourceNotFoundException when appointment does not exist")
    void cancelAppointment_ShouldThrowResourceNotFoundException_WhenAppointmentDoesNotExist() {
        // Arrange
        Long appointmentId = 1L;
        when(repository.findById(appointmentId)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            service.cancelAppointment(appointmentId);
        });

        assertEquals("appointment.not_found", exception.getMessage());
        assertArrayEquals(new Object[] { appointmentId }, exception.getArgs());
        verify(repository, times(1)).findById(appointmentId);
        verify(kafkaTemplate, never()).send(anyString(), anyString());
    }

    @Test
    @DisplayName("Get Student Appointments - Should return list of Appointments")
    void getStudentAppointments_ShouldReturnListOfAppointments() {
        // Arrange
        Long studentId = 1L;
        Appointment appointment1 = Appointment.builder().studentId(studentId).build();
        appointment1.setId(1L);
        Appointment appointment2 = Appointment.builder().studentId(studentId).build();
        appointment2.setId(2L);
        when(repository.findByStudentId(studentId)).thenReturn(List.of(appointment1, appointment2));

        // Act
        List<Appointment> result = service.getStudentAppointments(studentId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(repository, times(1)).findByStudentId(studentId);
    }
}
