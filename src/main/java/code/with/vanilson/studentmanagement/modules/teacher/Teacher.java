package code.with.vanilson.studentmanagement.modules.teacher;

import code.with.vanilson.studentmanagement.common.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "teacher")
@lombok.EqualsAndHashCode(callSuper = true)
public class Teacher extends BaseEntity {
    private String firstName;
    private String lastName;
    private String email;
    private String expertise;
}
