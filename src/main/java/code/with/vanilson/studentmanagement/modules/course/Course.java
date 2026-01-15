package code.with.vanilson.studentmanagement.modules.course;

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
@Table(name = "course")
@lombok.EqualsAndHashCode(callSuper = true)
public class Course extends BaseEntity {
    private String title;
    private String description;
    private int credits;
}
