package code.with.vanilson.studentmanagement.bdd.steps;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class TestContext {
    private Long teacherId;
    private Long studentId;
}
