package code.with.vanilson.studentmanagement.bdd.steps;

import code.with.vanilson.studentmanagement.AbstractIntegrationTest;
import code.with.vanilson.studentmanagement.modules.auth.AuthenticationRequest;
import code.with.vanilson.studentmanagement.modules.auth.RegisterRequest;
import code.with.vanilson.studentmanagement.modules.auth.Role;
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
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthSteps extends AbstractIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;
    private ResultActions resultActions;

    // We need to initialize MockMvc here as well or share it.
    // For simplicity, we init it in each step class or use a shared state object.
    // Since Cucumber creates new instances, we can init in a @Before or lazy init.

    private void initMockMvc() {
        if (mockMvc == null) {
            mockMvc = MockMvcBuilders
                    .webAppContextSetup(context)
                    .apply(springSecurity())
                    .build();
        }
    }

    @When("I register with the following details:")
    public void i_register_with_the_following_details(DataTable dataTable) throws Exception {
        initMockMvc();
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
        Map<String, String> row = rows.get(0);

        RegisterRequest request = RegisterRequest.builder()
                .firstname(row.get("firstname"))
                .lastname(row.get("lastname"))
                .email(row.get("email"))
                .password(row.get("password"))
                .role(Role.valueOf(row.get("role")))
                .build();

        resultActions = mockMvc.perform(post("/api/v1/auth/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));
    }

    @Then("the registration should be successful")
    public void the_registration_should_be_successful() throws Exception {
        resultActions.andExpect(status().isOk());
    }

    @Then("I should receive an access token")
    public void i_should_receive_an_access_token() throws Exception {
        resultActions.andExpect(jsonPath("$.data.accessToken").exists());
    }

    @Given("a user exists with email {string}")
    public void a_user_exists_with_email(String email) throws Exception {
        initMockMvc();
        // Register the user only if they don't exist
        RegisterRequest request = RegisterRequest.builder()
                .firstname("Existing")
                .lastname("User")
                .email(email)
                .password("password123")
                .role(Role.USER)
                .build();

        mockMvc.perform(post("/api/v1/auth/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));
    }

    @Then("the registration should fail with a conflict or bad request")
    public void the_registration_should_fail_with_a_conflict_or_bad_request() throws Exception {
        resultActions.andExpect(status().is4xxClientError());
    }

    @Given("a user exists with email {string} and password {string}")
    public void a_user_exists_with_email_and_password(String email, String password) throws Exception {
        initMockMvc();
        RegisterRequest request = RegisterRequest.builder()
                .firstname("Login")
                .lastname("User")
                .email(email)
                .password(password)
                .role(Role.USER)
                .build();

        mockMvc.perform(post("/api/v1/auth/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));
    }

    @When("I authenticate with email {string} and password {string}")
    public void i_authenticate_with_email_and_password(String email, String password) throws Exception {
        initMockMvc();
        AuthenticationRequest request = AuthenticationRequest.builder()
                .email(email)
                .password(password)
                .build();

        resultActions = mockMvc.perform(post("/api/v1/auth/authenticate")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));
    }

    @Then("the authentication should be successful")
    public void the_authentication_should_be_successful() throws Exception {
        resultActions.andExpect(status().isOk());
    }

    @Then("the authentication should fail with unauthorized status")
    public void the_authentication_should_fail_with_unauthorized_status() throws Exception {
        resultActions.andExpect(status().isUnauthorized()); // 401
    }
}
