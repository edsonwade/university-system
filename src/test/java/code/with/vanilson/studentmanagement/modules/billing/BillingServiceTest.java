package code.with.vanilson.studentmanagement.modules.billing;

import code.with.vanilson.studentmanagement.common.exception.ResourceBadRequestException;
import code.with.vanilson.studentmanagement.common.exception.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BillingServiceTest {

    @Mock
    private InvoiceRepository invoiceRepository;

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @InjectMocks
    private BillingService service;

    @Test
    @DisplayName("Create Invoice - Should return Invoice and send Kafka message")
    void createInvoice_ShouldReturnInvoiceAndSendKafkaMessage() {
        // Arrange
        Long studentId = 1L;
        BigDecimal amount = new BigDecimal("100.00");
        LocalDate dueDate = LocalDate.now().plusDays(30);

        Invoice invoice = Invoice.builder()
                .studentId(studentId)
                .amount(amount)
                .dueDate(dueDate)
                .status(Invoice.InvoiceStatus.PENDING)
                .build();
        invoice.setId(1L);

        when(invoiceRepository.save(any(Invoice.class))).thenReturn(invoice);

        // Act
        Invoice result = service.createInvoice(studentId, amount, dueDate);

        // Assert
        assertNotNull(result);
        assertEquals(Invoice.InvoiceStatus.PENDING, result.getStatus());
        verify(invoiceRepository, times(1)).save(any(Invoice.class));
        verify(kafkaTemplate, times(1)).send(eq("billing-events"), anyString());
    }

    @Test
    @DisplayName("Pay Invoice - Should return Paid Invoice and send Kafka message when invoice exists and pending")
    void payInvoice_ShouldReturnPaidInvoiceAndSendKafkaMessage_WhenInvoiceExistsAndPending() {
        // Arrange
        Long invoiceId = 1L;
        Invoice invoice = Invoice.builder()
                .status(Invoice.InvoiceStatus.PENDING)
                .build();
        invoice.setId(invoiceId);

        when(invoiceRepository.findById(invoiceId)).thenReturn(Optional.of(invoice));
        when(invoiceRepository.save(any(Invoice.class))).thenReturn(invoice);

        // Act
        Invoice result = service.payInvoice(invoiceId);

        // Assert
        assertNotNull(result);
        assertEquals(Invoice.InvoiceStatus.PAID, result.getStatus());
        verify(invoiceRepository, times(1)).findById(invoiceId);
        verify(invoiceRepository, times(1)).save(invoice);
        verify(kafkaTemplate, times(1)).send(eq("billing-events"), contains("Invoice paid"));
    }

    @Test
    @DisplayName("Pay Invoice - Should throw ResourceNotFoundException when invoice does not exist")
    void payInvoice_ShouldThrowResourceNotFoundException_WhenInvoiceDoesNotExist() {
        // Arrange
        Long invoiceId = 1L;
        when(invoiceRepository.findById(invoiceId)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            service.payInvoice(invoiceId);
        });

        assertEquals("billing.invoice_not_found", exception.getMessage());
        assertArrayEquals(new Object[] { invoiceId }, exception.getArgs());
        verify(invoiceRepository, times(1)).findById(invoiceId);
        verify(kafkaTemplate, never()).send(anyString(), anyString());
    }

    @Test
    @DisplayName("Pay Invoice - Should throw ResourceBadRequestException when invoice already paid")
    void payInvoice_ShouldThrowResourceBadRequestException_WhenInvoiceAlreadyPaid() {
        // Arrange
        Long invoiceId = 1L;
        Invoice invoice = Invoice.builder()
                .status(Invoice.InvoiceStatus.PAID)
                .build();
        invoice.setId(invoiceId);

        when(invoiceRepository.findById(invoiceId)).thenReturn(Optional.of(invoice));

        // Act & Assert
        ResourceBadRequestException exception = assertThrows(ResourceBadRequestException.class, () -> {
            service.payInvoice(invoiceId);
        });

        assertEquals("billing.invoice_already_paid", exception.getMessage());
        assertArrayEquals(new Object[] { invoiceId }, exception.getArgs());
        verify(invoiceRepository, times(1)).findById(invoiceId);
        verify(invoiceRepository, never()).save(any(Invoice.class));
    }

    @Test
    @DisplayName("Get Student Invoices - Should return list of Invoices")
    void getStudentInvoices_ShouldReturnListOfInvoices() {
        // Arrange
        Long studentId = 1L;
        Invoice invoice1 = Invoice.builder().studentId(studentId).build();
        invoice1.setId(1L);
        Invoice invoice2 = Invoice.builder().studentId(studentId).build();
        invoice2.setId(2L);
        when(invoiceRepository.findByStudentId(studentId)).thenReturn(List.of(invoice1, invoice2));

        // Act
        List<Invoice> result = service.getStudentInvoices(studentId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(invoiceRepository, times(1)).findByStudentId(studentId);
    }

    @Test
    @DisplayName("Apply Late Penalties - Should apply penalty and send Kafka message when invoice is overdue")
    void applyLatePenalties_ShouldApplyPenaltyAndSendKafkaMessage_WhenInvoiceIsOverdue() {
        // Arrange
        LocalDate dueDate = LocalDate.now().minusDays(14); // 2 weeks late
        Invoice invoice = Invoice.builder()
                .status(Invoice.InvoiceStatus.PENDING)
                .dueDate(dueDate)
                .amount(new BigDecimal("100.00"))
                .build();
        invoice.setId(1L);

        when(invoiceRepository.findAll()).thenReturn(List.of(invoice));

        // Act
        service.applyLatePenalties();

        // Assert
        assertEquals(Invoice.InvoiceStatus.OVERDUE, invoice.getStatus());
        assertNotNull(invoice.getPenaltyAmount());
        // 10% per week for 2 weeks = 20% of 100 = 20.00
        assertEquals(0, new BigDecimal("20.00").compareTo(invoice.getPenaltyAmount()));
        verify(invoiceRepository, times(1)).save(invoice);
        verify(kafkaTemplate, times(1)).send(eq("billing-events"), contains("Penalty applied"));
    }
}
