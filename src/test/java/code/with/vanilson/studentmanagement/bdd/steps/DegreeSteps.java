package code.with.vanilson.studentmanagement.bdd.steps;

import code.with.vanilson.studentmanagement.AbstractIntegrationTest;
import code.with.vanilson.studentmanagement.modules.course.DegreeRepository;
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

public class DegreeSteps extends AbstractIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private DegreeRepository degreeRepository;

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

    @When("I request to create a degree with the following details:")
    public void i_request_to_create_a_degree_with_the_following_details(DataTable dataTable) throws Exception {
        initMockMvc();
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
        Map<String, String> row = rows.get(0);

        code.with.vanilson.studentmanagement.modules.course.Degree degree = new code.with.vanilson.studentmanagement.modules.course.Degree();
        degree.setName(row.get("name"));
        // degree.setCode(row.get("code")); // Degree does not have code
        degree.setDurationYears(Integer.parseInt(row.get("duration")));
        degree.setDepartment("General"); // Default

        resultActions = mockMvc.perform(post("/api/v1/degrees")
                .with(user("admin").roles("ADMIN"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(degree)));
    }

    @Then("the degree should be created successfully")
    public void the_degree_should_be_created_successfully() throws Exception {
        resultActions.andExpect(status().isOk()); // Controller returns 200 OK
    }

    @Then("the degree details should be:")
    public void the_degree_details_should_be(DataTable dataTable) throws Exception {
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
        Map<String, String> row = rows.get(0);

        resultActions.andExpect(jsonPath("$.data.name").value(row.get("name")));
    }

    @Given("a degree exists with code {string}")
    public void a_degree_exists_with_code(String code) throws Exception {
        initMockMvc();
        var existing = degreeRepository.findAll().stream()
                .filter(d -> d.getName().contains(code))
                .findFirst();
        if (existing.isPresent()) {
            return;
        }

        code.with.vanilson.studentmanagement.modules.course.Degree degree = new code.with.vanilson.studentmanagement.modules.course.Degree();
        degree.setName("Engineering " + code);
        // degree.setCode(code);
        degree.setDurationYears(4);
        degree.setDepartment("Engineering");

        mockMvc.perform(post("/api/v1/degrees")
                .with(user("admin").roles("ADMIN"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(degree)))
                .andExpect(status().isOk());
    }

    @When("I request to get the degree by ID")
    public void i_request_to_get_the_degree_by_id() throws Exception {
        initMockMvc();
        var degree = degreeRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No degrees found"));

        resultActions = mockMvc.perform(get("/api/v1/degrees/" + degree.getId())
                .with(user("admin").roles("ADMIN")));
    }

    @Then("the degree details should be returned successfully")
    public void the_degree_details_should_be_returned_successfully() throws Exception {
        resultActions.andExpect(status().isOk());
    }
}
