package code.with.vanilson.studentmanagement.bdd.steps;

import code.with.vanilson.studentmanagement.AbstractIntegrationTest;

import code.with.vanilson.studentmanagement.modules.teacher.Teacher;
import code.with.vanilson.studentmanagement.modules.teacher.TeacherRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AppointmentSteps extends AbstractIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private code.with.vanilson.studentmanagement.modules.student.StudentRepository studentRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TestContext testContext;

    private MockMvc mockMvc;
    private ResultActions resultActions;
    private Long teacherId;

    private void initMockMvc() {
        if (mockMvc == null) {
            mockMvc = MockMvcBuilders
                    .webAppContextSetup(context)
                    .apply(springSecurity())
                    .build();
        }
    }

    @Given("I am an authenticated student")
    public void i_am_an_authenticated_student() throws Exception {
        initMockMvc();
        // Ensure at least one student exists to avoid NPE
        if (studentRepository.count() == 0) {
            code.with.vanilson.studentmanagement.modules.student.Student student = new code.with.vanilson.studentmanagement.modules.student.Student();
            student.setFirstName("John");
            student.setLastName("Doe");
            student.setEmail("john.doe@example.com");
            student.setDateOfBirth(java.time.LocalDate.now().minusYears(20));
            student.setAddress("123 Street");
            studentRepository.saveAndFlush(student);
        }
    }

    @Given("a teacher exists with email {string} for appointment")
    public void a_teacher_exists_with_email_for_appointment(String email) throws Exception {
        initMockMvc();

        var existing = teacherRepository.findAll().stream()
                .filter(t -> t.getEmail().equals(email))
                .findFirst();

        if (existing.isPresent()) {
            testContext.setTeacherId(existing.get().getId());
            return;
        }

        Teacher dto = new Teacher();
        dto.setFirstName("Teacher");
        dto.setLastName("Test");
        dto.setEmail(email);
        dto.setExpertise("General");

        mockMvc.perform(post("/api/v1/teachers")
                .with(user("admin").roles("ADMIN"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        // Fetch ID
        var saved = teacherRepository.findAll().stream()
                .filter(t -> t.getEmail().equals(email))
                .findFirst().orElseThrow();
        testContext.setTeacherId(saved.getId());
    }

    @When("I request to schedule an appointment with the teacher at {string}")
    public void i_request_to_schedule_an_appointment_with_the_teacher_at(String dateTimeStr) throws Exception {
        initMockMvc();

        if (testContext.getTeacherId() == null) {
            throw new RuntimeException("teacherId is null in TestContext. Ensure 'a teacher exists' step is called first.");
        }

        var student = studentRepository.findAll().get(0);

        resultActions = mockMvc.perform(post("/api/v1/appointments")
                .param("studentId", student.getId().toString())
                .param("teacherId", testContext.getTeacherId().toString())
                .param("startTime", dateTimeStr)
                .param("endTime", LocalDateTime.parse(dateTimeStr).plusHours(1).toString())
                .with(user("student").roles("STUDENT"))
                .with(csrf()));
    }

    @Then("the appointment should be scheduled successfully")
    public void the_appointment_should_be_scheduled_successfully() throws Exception {
        resultActions.andExpect(status().isOk());
    }

    @Given("the teacher has an appointment at {string}")
    public void the_teacher_has_an_appointment_at(String dateTimeStr) throws Exception {
        // We must ensure the teacher exists for THIS scenario too
        if (testContext.getTeacherId() == null) {
            a_teacher_exists_with_email_for_appointment("teacher@example.com");
        }
        i_request_to_schedule_an_appointment_with_the_teacher_at(dateTimeStr);
        the_appointment_should_be_scheduled_successfully();
    }

    @Then("the scheduling should fail with a conflict")
    public void the_scheduling_should_fail_with_a_conflict() throws Exception {
        resultActions.andExpect(status().isConflict()); // or 400
    }
}
