package code.with.vanilson.studentmanagement.modules.billing;

import code.with.vanilson.studentmanagement.AbstractIntegrationTest;
import code.with.vanilson.studentmanagement.modules.student.StudentDto;
import code.with.vanilson.studentmanagement.modules.student.StudentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class BillingIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StudentService studentService;

    @Test
    @DisplayName("Billing Flow - Create and Pay Invoice")
    @WithMockUser(roles = "ADMIN")
    void billingFlow_Success() throws Exception {
        // Create a student first
        StudentDto student = StudentDto.builder()
                .firstName("Billing")
                .lastName("Student")
                .email("billing.student@example.com")
                .dateOfBirth(LocalDate.of(2000, 1, 1))
                .build();
        student = studentService.createStudent(student);
        Long studentId = student.getId();

        // Create Invoice and get ID
        String createResponse = mockMvc.perform(post("/api/v1/billing/invoices")
                .param("studentId", studentId.toString())
                .param("amount", "150.00")
                .param("dueDate", "2024-12-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.amount").value(150.0))
                .andReturn().getResponse().getContentAsString();

        Integer invoiceIdInt = com.jayway.jsonpath.JsonPath.read(createResponse, "$.data.id");
        Long invoiceId = invoiceIdInt.longValue();

        // Get student invoices
        mockMvc.perform(get("/api/v1/billing/students/" + studentId + "/invoices"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].studentId").value(studentId));

        // Pay Invoice
        mockMvc.perform(post("/api/v1/billing/invoices/" + invoiceId + "/pay"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("PAID"));
    }
}
