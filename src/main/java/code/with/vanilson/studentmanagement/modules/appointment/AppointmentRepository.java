package code.with.vanilson.studentmanagement.modules.appointment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByStudentId(Long studentId);

    List<Appointment> findByTeacherId(Long teacherId);

    @org.springframework.data.jpa.repository.Query("SELECT a FROM Appointment a WHERE a.teacherId = :teacherId " +
            "AND ((a.startTime < :endTime AND a.endTime > :startTime)) " +
            "AND a.status = 'SCHEDULED'")
    List<Appointment> findOverlappingAppointments(
            @org.springframework.data.repository.query.Param("teacherId") Long teacherId,
            @org.springframework.data.repository.query.Param("startTime") java.time.LocalDateTime startTime,
            @org.springframework.data.repository.query.Param("endTime") java.time.LocalDateTime endTime);
}
