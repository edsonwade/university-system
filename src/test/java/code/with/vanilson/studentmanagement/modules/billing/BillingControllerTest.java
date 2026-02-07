package code.with.vanilson.studentmanagement.modules.billing;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import code.with.vanilson.studentmanagement.config.JwtUtils;

import code.with.vanilson.studentmanagement.config.SecurityConfig;

@WebMvcTest({ BillingController.class, SecurityConfig.class })
class BillingControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private BillingService billingService;

        @MockBean
        private JwtUtils jwtUtils;

        @MockBean
        private UserDetailsService userDetailsService;

        @MockBean
        private AuthenticationProvider authenticationProvider;

        @Test
        @DisplayName("POST /api/v1/billing/invoices - Success (Admin)")
        @WithMockUser(roles = "ADMIN")
        void createInvoice_Success() throws Exception {
                Invoice invoice = Invoice.builder()
                                .studentId(1L)
                                .amount(new BigDecimal("100.00"))
                                .dueDate(LocalDate.of(2024, 12, 31))
                                .status(Invoice.InvoiceStatus.PENDING)
                                .build();

                when(billingService.createInvoice(eq(1L), any(BigDecimal.class), any(LocalDate.class)))
                                .thenReturn(invoice);

                mockMvc.perform(post("/api/v1/billing/invoices")
                                .with(csrf())
                                .param("studentId", "1")
                                .param("amount", "100.00")
                                .param("dueDate", "2024-12-31"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.data.studentId").value(1))
                                .andExpect(jsonPath("$.message").value("Invoice created successfully"));
        }

        @Test
        @DisplayName("POST /api/v1/billing/invoices - Forbidden (User)")
        @WithMockUser(roles = "USER")
        void createInvoice_Forbidden() throws Exception {
                mockMvc.perform(post("/api/v1/billing/invoices")
                                .with(csrf())
                                .param("studentId", "1")
                                .param("amount", "100.00")
                                .param("dueDate", "2024-12-31"))
                                .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("POST /api/v1/billing/invoices/{id}/pay - Success")
        @WithMockUser
        void payInvoice_Success() throws Exception {
                Invoice invoice = Invoice.builder()
                                .studentId(1L)
                                .amount(new BigDecimal("100.00"))
                                .dueDate(LocalDate.of(2024, 12, 31))
                                .status(Invoice.InvoiceStatus.PAID)
                                .build();

                when(billingService.payInvoice(1L)).thenReturn(invoice);

                mockMvc.perform(post("/api/v1/billing/invoices/1/pay")
                                .with(csrf()))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.data.status").value("PAID"))
                                .andExpect(jsonPath("$.message").value("Invoice paid successfully"));
        }

        @Test
        @DisplayName("GET /api/v1/billing/students/{studentId}/invoices - Success")
        @WithMockUser
        void getStudentInvoices_Success() throws Exception {
                Invoice invoice = Invoice.builder()
                                .studentId(1L)
                                .amount(new BigDecimal("100.00"))
                                .dueDate(LocalDate.of(2024, 12, 31))
                                .status(Invoice.InvoiceStatus.PENDING)
                                .build();
                List<Invoice> invoices = Collections.singletonList(invoice);

                when(billingService.getStudentInvoices(1L)).thenReturn(invoices);

                mockMvc.perform(get("/api/v1/billing/students/1/invoices"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.data[0].studentId").value(1))
                                .andExpect(jsonPath("$.message").value("Invoices retrieved successfully"));
        }
}
