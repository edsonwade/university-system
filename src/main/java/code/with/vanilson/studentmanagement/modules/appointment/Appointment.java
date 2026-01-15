package code.with.vanilson.studentmanagement.modules.appointment;

import code.with.vanilson.studentmanagement.common.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "appointment")
@lombok.EqualsAndHashCode(callSuper = true)
public class Appointment extends BaseEntity {

    private Long studentId;
    private Long teacherId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    private AppointmentStatus status;

    public enum AppointmentStatus {
        SCHEDULED,
        CANCELLED,
        COMPLETED
    }
}
