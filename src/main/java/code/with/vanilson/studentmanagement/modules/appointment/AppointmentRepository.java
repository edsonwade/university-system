package code.with.vanilson.studentmanagement.modules.appointment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByStudentId(Long studentId);

    List<Appointment> findByTeacherId(Long teacherId);
}
