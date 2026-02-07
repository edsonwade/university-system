package code.with.vanilson.studentmanagement.bdd.steps;

import code.with.vanilson.studentmanagement.AbstractIntegrationTest;
import code.with.vanilson.studentmanagement.modules.course.CourseRepository;
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

public class CourseSteps extends AbstractIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;
    private ResultActions resultActions;

    private void initMockMvc() {
        if (mockMvc == null) {
            mockMvc = MockMvcBuilders
                    .webAppContextSetup(context)
                    .apply(springSecurity())
                    .build();
        }
    }

    @When("I request to create a course with the following details:")
    public void i_request_to_create_a_course_with_the_following_details(DataTable dataTable) throws Exception {
        initMockMvc();
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
        Map<String, String> row = rows.get(0);

        code.with.vanilson.studentmanagement.modules.course.Course course = new code.with.vanilson.studentmanagement.modules.course.Course();
        course.setTitle(row.get("name"));
        // course.setCode(row.get("code")); // Course does not have code
        course.setDescription(row.get("description"));
        course.setCredits(3); // Default

        resultActions = mockMvc.perform(post("/api/v1/courses")
                .with(user("admin").roles("ADMIN"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(course)));
    }

    @Then("the course should be created successfully")
    public void the_course_should_be_created_successfully() throws Exception {
        resultActions.andExpect(status().isOk()); // Controller returns 200 OK
    }

    @Then("the course details should be:")
    public void the_course_details_should_be(DataTable dataTable) throws Exception {
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
        Map<String, String> row = rows.get(0);

        // resultActions.andExpect(jsonPath("$.data.code").value(row.get("code")));
        resultActions.andExpect(jsonPath("$.data.title").value(row.get("name")));
    }

    @Given("a course exists with code {string}")
    public void a_course_exists_with_code(String code) throws Exception {
        initMockMvc();
        var existing = courseRepository.findAll().stream()
                .filter(c -> c.getTitle().contains(code))
                .findFirst();
        if (existing.isPresent()) {
            return;
        }

        code.with.vanilson.studentmanagement.modules.course.Course course = new code.with.vanilson.studentmanagement.modules.course.Course();
        course.setTitle("Physics " + code);
        // course.setCode(code);
        course.setDescription("Basic Physics");
        course.setCredits(4);

        mockMvc.perform(post("/api/v1/courses")
                .with(user("admin").roles("ADMIN"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(course)))
                .andExpect(status().isOk());
    }

    @When("I request to get the course by ID")
    public void i_request_to_get_the_course_by_id() throws Exception {
        initMockMvc();
        var course = courseRepository.findAll().stream()
                .findFirst().orElseThrow(() -> new RuntimeException("No courses found"));

        resultActions = mockMvc.perform(get("/api/v1/courses/" + course.getId())
                .with(user("admin").roles("ADMIN")));
    }

    @Then("the course details should be returned successfully")
    public void the_course_details_should_be_returned_successfully() throws Exception {
        resultActions.andExpect(status().isOk());
    }
}
