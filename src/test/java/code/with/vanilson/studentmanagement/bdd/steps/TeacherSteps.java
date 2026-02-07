package code.with.vanilson.studentmanagement.bdd.steps;

import code.with.vanilson.studentmanagement.AbstractIntegrationTest;
import code.with.vanilson.studentmanagement.modules.teacher.TeacherRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.Map;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TeacherSteps extends AbstractIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private TeacherRepository teacherRepository;

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

    @When("I request to create a teacher with the following details:")
    public void i_request_to_create_a_teacher_with_the_following_details(DataTable dataTable) throws Exception {
        initMockMvc();
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
        Map<String, String> row = rows.get(0);

        code.with.vanilson.studentmanagement.modules.teacher.Teacher teacher = new code.with.vanilson.studentmanagement.modules.teacher.Teacher();
        teacher.setFirstName(row.get("name"));
        teacher.setLastName("Doe"); // Defaulting as not in datatable
        teacher.setEmail(row.get("email"));
        teacher.setExpertise("General"); // Defaulting

        resultActions = mockMvc.perform(post("/api/v1/teachers")
                .with(user("admin").roles("ADMIN"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(teacher)));
    }

    @Then("the teacher should be created successfully")
    public void the_teacher_should_be_created_successfully() throws Exception {
        resultActions.andExpect(status().isOk());
    }

    @Then("the teacher details should be:")
    public void the_teacher_details_should_be(DataTable dataTable) throws Exception {
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
        Map<String, String> row = rows.get(0);

        resultActions.andExpect(jsonPath("$.data.email").value(row.get("email")));
    }

    @Given("a teacher exists with email {string}")
    public void a_teacher_exists_with_email(String email) throws Exception {
        initMockMvc();

        var existing = teacherRepository.findAll().stream()
                .filter(t -> t.getEmail().equals(email))
                .findFirst();

        if (existing.isPresent()) {
            testContext.setTeacherId(existing.get().getId());
            return;
        }

        code.with.vanilson.studentmanagement.modules.teacher.Teacher teacher = new code.with.vanilson.studentmanagement.modules.teacher.Teacher();
        teacher.setFirstName("Bob");
        teacher.setLastName("Smith");
        teacher.setEmail(email);
        teacher.setExpertise("Math");

        mockMvc.perform(post("/api/v1/teachers")
                .with(user("admin").roles("ADMIN"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(teacher)))
                .andExpect(status().isOk());

        var saved = teacherRepository.findAll().stream()
                .filter(t -> t.getEmail().equals(email))
                .findFirst().orElseThrow();
        testContext.setTeacherId(saved.getId());
    }


    @When("I request to get the teacher by ID")
    public void i_request_to_get_the_teacher_by_id() throws Exception {
        initMockMvc();
        if (testContext.getTeacherId() == null) {
            throw new RuntimeException("teacherId is null in TestContext. Ensure 'a teacher exists' step is called first.");
        }

        resultActions = mockMvc.perform(get("/api/v1/teachers/" + testContext.getTeacherId())
                .with(user("admin").roles("ADMIN")));
    }

    @Then("the teacher details should be returned successfully")
    public void the_teacher_details_should_be_returned_successfully() throws Exception {
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.firstName").value("Bob"));
    }
}
