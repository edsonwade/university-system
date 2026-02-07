package code.with.vanilson.studentmanagement.bdd.steps;

import code.with.vanilson.studentmanagement.AbstractIntegrationTest;
import code.with.vanilson.studentmanagement.modules.billing.Invoice;
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

import java.math.BigDecimal;

import static code.with.vanilson.studentmanagement.modules.billing.Invoice.InvoiceStatus.PENDING;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class BillingSteps extends AbstractIntegrationTest {
    @Autowired
    private code.with.vanilson.studentmanagement.modules.student.StudentRepository studentRepository;

    @Autowired
    private code.with.vanilson.studentmanagement.modules.billing.InvoiceRepository invoiceRepository;

    @Autowired
    private TestContext testContext;

    @Autowired
    private WebApplicationContext context;

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

    @When("I request to pay {string} for {string}")
    public void i_request_to_pay_for(String amount, String reason) throws Exception {
        initMockMvc();

        // Ensure student exists
        if (studentRepository.count() == 0) {
            code.with.vanilson.studentmanagement.modules.student.Student student =
                    new code.with.vanilson.studentmanagement.modules.student.Student();
            student.setFirstName("John");
            student.setLastName("Doe");
            student.setEmail("john.doe@example.com");
            student.setDateOfBirth(java.time.LocalDate.now().minusYears(20));
            student.setAddress("123 Street");
            studentRepository.saveAndFlush(student);
        }
        var student = studentRepository.findAll().get(0);

        BigDecimal amt = new BigDecimal(amount);
        if (amt.compareTo(BigDecimal.ZERO) <= 0) {
            // Test "Payment with invalid amount" by trying to create an invoice with invalid amount
            resultActions = mockMvc.perform(post("/api/v1/billing/invoices")
                    .param("studentId", student.getId().toString())
                    .param("amount", amount)
                    .param("dueDate", java.time.LocalDate.now().plusDays(30).toString())
                    .with(user("admin").roles("ADMIN"))
                    .with(csrf()));
        } else {
            // Success path: create invoice first, then pay it
            mockMvc.perform(post("/api/v1/billing/invoices")
                            .param("studentId", student.getId().toString())
                            .param("amount", amount)
                            .param("dueDate", java.time.LocalDate.now().plusDays(30).toString())
                            .with(user("admin").roles("ADMIN"))
                            .with(csrf()))
                    .andExpect(status().isOk());

            var invoice = invoiceRepository.findAll().stream()
                    .filter(i -> i.getStudentId().equals(student.getId()))
                    .findFirst().orElseThrow();

            resultActions = mockMvc.perform(post("/api/v1/billing/invoices/" + invoice.getId() + "/pay")
                    .with(user("student").roles("STUDENT"))
                    .with(csrf()));
        }
    }

    @Then("the payment should be processed successfully")
    public void the_payment_should_be_processed_successfully() throws Exception {
        resultActions.andExpect(status().isOk());
    }

    @Then("the payment should fail with a bad request")
    public void the_payment_should_fail_with_a_bad_request() throws Exception {
        resultActions.andExpect(status().isBadRequest());
    }
}
