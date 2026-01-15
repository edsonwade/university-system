package code.with.vanilson.studentmanagement.modules.billing;

import code.with.vanilson.studentmanagement.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/billing")
@RequiredArgsConstructor
@io.swagger.v3.oas.annotations.tags.Tag(name = "Billing", description = "Endpoints for managing invoices and payments")
public class BillingController {

    private final BillingService service;

    @PostMapping("/invoices")
    @PreAuthorize("hasRole('ADMIN')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Create invoice", description = "Creates a new invoice for a student. Requires ADMIN role.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Invoice created successfully")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - Requires ADMIN role")
    public ResponseEntity<ApiResponse<Invoice>> createInvoice(
            @RequestParam Long studentId,
            @RequestParam BigDecimal amount,
            @RequestParam String dueDate) {
        return ResponseEntity.ok(ApiResponse.success(
                service.createInvoice(studentId, amount, LocalDate.parse(dueDate)),
                "Invoice created successfully"));
    }

    @PostMapping("/invoices/{id}/pay")
    @io.swagger.v3.oas.annotations.Operation(summary = "Pay invoice", description = "Marks an invoice as paid.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Invoice paid successfully")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Invoice not found")
    public ResponseEntity<ApiResponse<Invoice>> payInvoice(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(service.payInvoice(id), "Invoice paid successfully"));
    }

    @GetMapping("/students/{studentId}/invoices")
    @io.swagger.v3.oas.annotations.Operation(summary = "Get student invoices", description = "Retrieves all invoices for a specific student.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Invoices retrieved successfully")
    public ResponseEntity<ApiResponse<List<Invoice>>> getStudentInvoices(@PathVariable Long studentId) {
        return ResponseEntity
                .ok(ApiResponse.success(service.getStudentInvoices(studentId), "Invoices retrieved successfully"));
    }
}
