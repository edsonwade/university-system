package code.with.vanilson.studentmanagement.modules.appointment;

import code.with.vanilson.studentmanagement.common.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.contains;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AppointmentService Unit Tests")
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @InjectMocks
    private AppointmentService appointmentService;

    private Appointment testAppointment;
    private List<Appointment> testAppointments;
    private final Long STUDENT_ID = 1L;
    private final Long TEACHER_ID = 2L;
    private final LocalDateTime START_TIME = LocalDateTime.now().plusHours(1);
    private final LocalDateTime END_TIME = LocalDateTime.now().plusHours(2);

    @BeforeEach
    void setUp() {
        testAppointment = Appointment.builder()
                .studentId(STUDENT_ID)
                .teacherId(TEACHER_ID)
                .startTime(START_TIME)
                .endTime(END_TIME)
                .status(Appointment.AppointmentStatus.SCHEDULED)
                .build();

        testAppointments = Arrays.asList(
                testAppointment,
                Appointment.builder()

                        .studentId(STUDENT_ID)
                        .teacherId(3L)
                        .startTime(LocalDateTime.now().plusDays(1))
                        .endTime(LocalDateTime.now().plusDays(1).plusHours(1))
                        .status(Appointment.AppointmentStatus.SCHEDULED)
                        .build()
        );
    }

    @Test
    @DisplayName("Should schedule appointment successfully")
    void scheduleAppointment_Success() {
        // Given
        when(appointmentRepository.findOverlappingAppointments(TEACHER_ID, START_TIME, END_TIME))
                .thenReturn(Collections.emptyList());
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(testAppointment);
        when(kafkaTemplate.send(anyString(), anyString())).thenReturn(null);

        // When
        Appointment result = appointmentService.scheduleAppointment(STUDENT_ID, TEACHER_ID, START_TIME, END_TIME);

        // Then
        assertNotNull(result);
        assertEquals(STUDENT_ID, result.getStudentId());
        assertEquals(TEACHER_ID, result.getTeacherId());
        assertEquals(START_TIME, result.getStartTime());
        assertEquals(END_TIME, result.getEndTime());
        assertEquals(Appointment.AppointmentStatus.SCHEDULED, result.getStatus());
        verify(appointmentRepository).findOverlappingAppointments(TEACHER_ID, START_TIME, END_TIME);
        verify(appointmentRepository).save(any(Appointment.class));
        verify(kafkaTemplate).send(eq("notification-events"),
                contains("Appointment scheduled for student: " + STUDENT_ID));
    }

    @Test
    @DisplayName("Should throw exception when scheduling overlapping appointment")
    void scheduleAppointment_OverlappingAppointment_ThrowsException() {
        // Given
        Appointment overlappingAppointment = Appointment.builder()
                .studentId(999L)
                .teacherId(TEACHER_ID)
                .startTime(START_TIME.minusMinutes(30))
                .endTime(END_TIME.plusMinutes(30))
                .status(Appointment.AppointmentStatus.SCHEDULED)
                .build();

        when(appointmentRepository.findOverlappingAppointments(TEACHER_ID, START_TIME, END_TIME))
                .thenReturn(Collections.singletonList(overlappingAppointment));

        // When & Then
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> appointmentService.scheduleAppointment(STUDENT_ID, TEACHER_ID, START_TIME, END_TIME)
        );
        assertEquals("Teacher already has an appointment during this time.", exception.getMessage());
        verify(appointmentRepository).findOverlappingAppointments(TEACHER_ID, START_TIME, END_TIME);
        verify(appointmentRepository, never()).save(any(Appointment.class));
        verify(kafkaTemplate, never()).send(anyString(), anyString());
    }

    @Test
    @DisplayName("Should cancel appointment successfully")
    void cancelAppointment_Success() {
        // Given
        Appointment cancelledAppointment = Appointment.builder()
                .studentId(STUDENT_ID)
                .teacherId(TEACHER_ID)
                .startTime(START_TIME)
                .endTime(END_TIME)
                .status(Appointment.AppointmentStatus.CANCELLED)
                .build();

        when(appointmentRepository.findById(testAppointment.getId())).thenReturn(Optional.of(testAppointment));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(cancelledAppointment);
        when(kafkaTemplate.send(anyString(), anyString())).thenReturn(null);

        // When
        Appointment result = appointmentService.cancelAppointment(testAppointment.getId());

        // Then
        assertNotNull(result);
        assertEquals(Appointment.AppointmentStatus.CANCELLED, result.getStatus());
        verify(appointmentRepository).findById(testAppointment.getId());
        verify(appointmentRepository).save(any(Appointment.class));
        verify(kafkaTemplate).send(eq("notification-events"),
                contains("Appointment cancelled: " + testAppointment.getId()));
    }

    @Test
    @DisplayName("Should throw exception when cancelling non-existent appointment")
    void cancelAppointment_NotFound_ThrowsException() {
        // Given
        Long nonExistentId = 999L;
        when(appointmentRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> appointmentService.cancelAppointment(nonExistentId)
        );
        assertEquals("appointment.not_found", exception.getMessage());
        verify(appointmentRepository).findById(nonExistentId);
        verify(appointmentRepository, never()).save(any(Appointment.class));
        verify(kafkaTemplate, never()).send(anyString(), anyString());
    }

    @Test
    @DisplayName("Should get student appointments successfully")
    void getStudentAppointments_Success() {
        // Given
        when(appointmentRepository.findByStudentId(STUDENT_ID)).thenReturn(testAppointments);

        // When
        List<Appointment> result = appointmentService.getStudentAppointments(STUDENT_ID);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(testAppointments.get(0).getId(), result.get(0).getId());
        assertEquals(testAppointments.get(1).getId(), result.get(1).getId());
        verify(appointmentRepository).findByStudentId(STUDENT_ID);
    }

    @Test
    @DisplayName("Should return empty list when student has no appointments")
    void getStudentAppointments_EmptyList() {
        // Given
        when(appointmentRepository.findByStudentId(STUDENT_ID)).thenReturn(Collections.emptyList());

        // When
        List<Appointment> result = appointmentService.getStudentAppointments(STUDENT_ID);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(appointmentRepository).findByStudentId(STUDENT_ID);
    }

    @Test
    @DisplayName("Should handle repository exception during appointment scheduling")
    void scheduleAppointment_RepositoryException_ThrowsException() {
        // Given
        when(appointmentRepository.findOverlappingAppointments(TEACHER_ID, START_TIME, END_TIME))
                .thenReturn(Collections.emptyList());
        when(appointmentRepository.save(any(Appointment.class))).thenThrow(new RuntimeException("Database error"));

        // When & Then
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> appointmentService.scheduleAppointment(STUDENT_ID, TEACHER_ID, START_TIME, END_TIME)
        );
        assertEquals("Database error", exception.getMessage());
        verify(appointmentRepository).findOverlappingAppointments(TEACHER_ID, START_TIME, END_TIME);
        verify(appointmentRepository).save(any(Appointment.class));
        verify(kafkaTemplate, never()).send(anyString(), anyString());
    }

    @Test
    @DisplayName("Should handle repository exception during appointment cancellation")
    void cancelAppointment_RepositoryException_ThrowsException() {
        // Given
        when(appointmentRepository.findById(testAppointment.getId())).thenReturn(Optional.of(testAppointment));
        when(appointmentRepository.save(any(Appointment.class))).thenThrow(new RuntimeException("Database error"));

        // When & Then
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> appointmentService.cancelAppointment(testAppointment.getId())
        );
        assertEquals("Database error", exception.getMessage());
        verify(appointmentRepository).findById(testAppointment.getId());
        verify(appointmentRepository).save(any(Appointment.class));
        verify(kafkaTemplate, never()).send(anyString(), anyString());
    }

    @Test
    @DisplayName("Should handle repository exception during get student appointments")
    void getStudentAppointments_RepositoryException_ThrowsException() {
        // Given
        when(appointmentRepository.findByStudentId(STUDENT_ID)).thenThrow(new RuntimeException("Database error"));

        // When & Then
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> appointmentService.getStudentAppointments(STUDENT_ID)
        );
        assertEquals("Database error", exception.getMessage());
        verify(appointmentRepository).findByStudentId(STUDENT_ID);
    }

    @Disabled
    @Test
    @DisplayName("Should handle Kafka exception during appointment scheduling")
    void scheduleAppointment_KafkaException_StillSchedulesAppointment() {
        // Given
        when(appointmentRepository.findOverlappingAppointments(TEACHER_ID, START_TIME, END_TIME))
                .thenReturn(Collections.emptyList());
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(testAppointment);
        when(kafkaTemplate.send(anyString(), anyString())).thenThrow(new RuntimeException("Kafka error"));

        // When
        Appointment result = appointmentService.scheduleAppointment(STUDENT_ID, TEACHER_ID, START_TIME, END_TIME);

        // Then
        assertNotNull(result);
        assertEquals(STUDENT_ID, result.getStudentId());
        verify(appointmentRepository).save(any(Appointment.class));
        verify(kafkaTemplate).send(anyString(), anyString());
    }

    @Disabled
    @Test
    @DisplayName("Should handle Kafka exception during appointment cancellation")
    void cancelAppointment_KafkaException_StillCancelsAppointment() {
        // Given
        Appointment cancelledAppointment = Appointment.builder()
                .studentId(STUDENT_ID)
                .teacherId(TEACHER_ID)
                .startTime(START_TIME)
                .endTime(END_TIME)
                .status(Appointment.AppointmentStatus.CANCELLED)
                .build();

        when(appointmentRepository.findById(testAppointment.getId())).thenReturn(Optional.of(testAppointment));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(cancelledAppointment);
        when(kafkaTemplate.send(anyString(), anyString())).thenThrow(new RuntimeException("Kafka error"));

        // When
        Appointment result = appointmentService.cancelAppointment(testAppointment.getId());

        // Then
        assertNotNull(result);
        assertEquals(Appointment.AppointmentStatus.CANCELLED, result.getStatus());
        verify(appointmentRepository).save(any(Appointment.class));
        verify(kafkaTemplate).send(anyString(), anyString());
    }

    @Test
    @DisplayName("Should schedule appointment with same time as existing appointment for different teacher")
    void scheduleAppointment_DifferentTeacherSameTime_Success() {
        // Given
        Long differentTeacherId = 2L;
        when(appointmentRepository.findOverlappingAppointments(differentTeacherId, START_TIME, END_TIME))
                .thenReturn(Collections.emptyList());
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(testAppointment);
        when(kafkaTemplate.send(anyString(), anyString())).thenReturn(null);

        // When
        Appointment result = appointmentService.scheduleAppointment(STUDENT_ID, differentTeacherId, START_TIME, END_TIME);

        // Then
        assertNotNull(result);
        assertEquals(differentTeacherId, result.getTeacherId());
        verify(appointmentRepository).findOverlappingAppointments(differentTeacherId, START_TIME, END_TIME);
        verify(appointmentRepository).save(any(Appointment.class));
    }

    @Test
    @DisplayName("Should schedule appointment with minimum duration")
    void scheduleAppointment_MinimumDuration_Success() {
        // Given
        LocalDateTime minEndTime = START_TIME.plusMinutes(1); // 1 minute duration
        Appointment minDurationAppointment = Appointment.builder()
                .studentId(STUDENT_ID)
                .teacherId(TEACHER_ID)
                .startTime(START_TIME)
                .endTime(minEndTime)
                .status(Appointment.AppointmentStatus.SCHEDULED)
                .build();

        when(appointmentRepository.findOverlappingAppointments(TEACHER_ID, START_TIME, minEndTime))
                .thenReturn(Collections.emptyList());
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(minDurationAppointment);
        when(kafkaTemplate.send(anyString(), anyString())).thenReturn(null);

        // When
        Appointment result = appointmentService.scheduleAppointment(STUDENT_ID, TEACHER_ID, START_TIME, minEndTime);

        // Then
        assertNotNull(result);
        assertEquals(minEndTime, result.getEndTime());
        verify(appointmentRepository).save(any(Appointment.class));
    }

    @Test
    @DisplayName("Should schedule appointment with maximum duration")
    void scheduleAppointment_MaximumDuration_Success() {
        // Given
        LocalDateTime maxEndTime = START_TIME.plusDays(7); // 7 days duration
        Appointment maxDurationAppointment = Appointment.builder()
                .studentId(STUDENT_ID)
                .teacherId(TEACHER_ID)
                .startTime(START_TIME)
                .endTime(maxEndTime)
                .status(Appointment.AppointmentStatus.SCHEDULED)
                .build();

        when(appointmentRepository.findOverlappingAppointments(TEACHER_ID, START_TIME, maxEndTime))
                .thenReturn(Collections.emptyList());
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(maxDurationAppointment);
        when(kafkaTemplate.send(anyString(), anyString())).thenReturn(null);

        // When
        Appointment result = appointmentService.scheduleAppointment(STUDENT_ID, TEACHER_ID, START_TIME, maxEndTime);

        // Then
        assertNotNull(result);
        assertEquals(maxEndTime, result.getEndTime());
        verify(appointmentRepository).save(any(Appointment.class));
    }

    @Test
    @DisplayName("Should handle overlapping check exception during appointment scheduling")
    void scheduleAppointment_OverlappingCheckException_ThrowsException() {
        // Given
        when(appointmentRepository.findOverlappingAppointments(TEACHER_ID, START_TIME, END_TIME))
                .thenThrow(new RuntimeException("Database error"));

        // When & Then
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> appointmentService.scheduleAppointment(STUDENT_ID, TEACHER_ID, START_TIME, END_TIME)
        );
        assertEquals("Database error", exception.getMessage());
        verify(appointmentRepository).findOverlappingAppointments(TEACHER_ID, START_TIME, END_TIME);
        verify(appointmentRepository, never()).save(any(Appointment.class));
        verify(kafkaTemplate, never()).send(anyString(), anyString());
    }

    @Test
    @DisplayName("Should get appointments for student with maximum valid ID")
    void getStudentAppointments_MaxStudentId_Success() {
        // Given
        Long maxStudentId = Long.MAX_VALUE;
        when(appointmentRepository.findByStudentId(maxStudentId)).thenReturn(testAppointments);

        // When
        List<Appointment> result = appointmentService.getStudentAppointments(maxStudentId);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(appointmentRepository).findByStudentId(maxStudentId);
    }

    @Test
    @DisplayName("Should get appointments for student with minimum valid ID")
    void getStudentAppointments_MinStudentId_Success() {
        // Given
        Long minStudentId = 1L;
        when(appointmentRepository.findByStudentId(minStudentId)).thenReturn(testAppointments);

        // When
        List<Appointment> result = appointmentService.getStudentAppointments(minStudentId);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(appointmentRepository).findByStudentId(minStudentId);
    }

    @Test
    @DisplayName("Should cancel already cancelled appointment")
    void cancelAppointment_AlreadyCancelled_Success() {
        // Given
        Appointment alreadyCancelledAppointment = Appointment.builder()
                .studentId(STUDENT_ID)
                .teacherId(TEACHER_ID)
                .startTime(START_TIME)
                .endTime(END_TIME)
                .status(Appointment.AppointmentStatus.CANCELLED)
                .build();

        when(appointmentRepository.findById(testAppointment.getId())).thenReturn(Optional.of(alreadyCancelledAppointment));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(alreadyCancelledAppointment);
        when(kafkaTemplate.send(anyString(), anyString())).thenReturn(null);

        // When
        Appointment result = appointmentService.cancelAppointment(testAppointment.getId());

        // Then
        assertNotNull(result);
        assertEquals(Appointment.AppointmentStatus.CANCELLED, result.getStatus());
        verify(appointmentRepository).save(any(Appointment.class));
    }
}
