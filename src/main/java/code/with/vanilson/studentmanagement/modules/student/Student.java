package code.with.vanilson.studentmanagement.modules.student;

import code.with.vanilson.studentmanagement.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "student")
@lombok.EqualsAndHashCode(callSuper = true)
public class Student extends BaseEntity {

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(unique = true, nullable = false)
    private String email;

    private LocalDate dateOfBirth;

    private String address;

    private String phoneNumber;

    // Link to User entity for auth if needed, or just keep email consistent
    private Long userId;

    private Long degreeId;
}
