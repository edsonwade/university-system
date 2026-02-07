package code.with.vanilson.studentmanagement.modules.billing;

import code.with.vanilson.studentmanagement.common.exception.ResourceBadRequestException;
import code.with.vanilson.studentmanagement.common.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("BillingService Unit Tests")
class BillingServiceTest {

    @Mock
    private InvoiceRepository invoiceRepository;

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @InjectMocks
    private BillingService billingService;

    private Invoice testInvoice;
    private List<Invoice> testInvoices;
    private final Long STUDENT_ID = 1L;
    private final BigDecimal VALID_AMOUNT = new BigDecimal("100.00");
    private final LocalDate DUE_DATE = LocalDate.now().plusDays(30);

    @BeforeEach
    void setUp() {
        testInvoice = Invoice.builder()

                .studentId(STUDENT_ID)
                .amount(VALID_AMOUNT)
                .dueDate(DUE_DATE)
                .status(Invoice.InvoiceStatus.PENDING)
                .build();

        testInvoices = Arrays.asList(
                testInvoice,
                Invoice.builder()

                        .studentId(STUDENT_ID)
                        .amount(new BigDecimal("200.00"))
                        .dueDate(LocalDate.now().plusDays(60))
                        .status(Invoice.InvoiceStatus.PAID)
                        .build()
        );
    }

    @Test
    @DisplayName("Should create invoice successfully")
    void createInvoice_Success() {
        // Given
        when(invoiceRepository.save(any(Invoice.class))).thenReturn(testInvoice);
        when(kafkaTemplate.send(anyString(), anyString())).thenReturn(null);

        // When
        Invoice result = billingService.createInvoice(STUDENT_ID, VALID_AMOUNT, DUE_DATE);

        // Then
        assertNotNull(result);
        assertEquals(STUDENT_ID, result.getStudentId());
        assertEquals(VALID_AMOUNT, result.getAmount());
        assertEquals(DUE_DATE, result.getDueDate());
        assertEquals(Invoice.InvoiceStatus.PENDING, result.getStatus());
        verify(invoiceRepository).save(any(Invoice.class));
        verify(kafkaTemplate).send(eq("billing-events"), contains("Invoice created for student: " + STUDENT_ID));
    }

    @Test
    @DisplayName("Should throw exception when creating invoice with null amount")
    void createInvoice_NullAmount_ThrowsException() {
        // When & Then
        ResourceBadRequestException exception = assertThrows(
                ResourceBadRequestException.class,
                () -> billingService.createInvoice(STUDENT_ID, null, DUE_DATE)
        );
        assertEquals("billing.invalid_amount", exception.getMessage());
        verify(invoiceRepository, never()).save(any(Invoice.class));
        verify(kafkaTemplate, never()).send(anyString(), anyString());
    }

    @Test
    @DisplayName("Should throw exception when creating invoice with zero amount")
    void createInvoice_ZeroAmount_ThrowsException() {
        // When & Then
        ResourceBadRequestException exception = assertThrows(
                ResourceBadRequestException.class,
                () -> billingService.createInvoice(STUDENT_ID, BigDecimal.ZERO, DUE_DATE)
        );
        assertEquals("billing.invalid_amount", exception.getMessage());
        verify(invoiceRepository, never()).save(any(Invoice.class));
        verify(kafkaTemplate, never()).send(anyString(), anyString());
    }

    @Test
    @DisplayName("Should throw exception when creating invoice with negative amount")
    void createInvoice_NegativeAmount_ThrowsException() {
        // When & Then
        ResourceBadRequestException exception = assertThrows(
                ResourceBadRequestException.class,
                () -> billingService.createInvoice(STUDENT_ID, new BigDecimal("-50.00"), DUE_DATE)
        );
        assertEquals("billing.invalid_amount", exception.getMessage());
        verify(invoiceRepository, never()).save(any(Invoice.class));
        verify(kafkaTemplate, never()).send(anyString(), anyString());
    }

    @Test
    @DisplayName("Should pay invoice successfully")
    void payInvoice_Success() {
        // Given
        Invoice paidInvoice = Invoice.builder()

                .studentId(STUDENT_ID)
                .amount(VALID_AMOUNT)
                .dueDate(DUE_DATE)
                .status(Invoice.InvoiceStatus.PAID)
                .build();

        when(invoiceRepository.findById(testInvoice.getId())).thenReturn(Optional.of(testInvoice));
        when(invoiceRepository.save(any(Invoice.class))).thenReturn(paidInvoice);
        when(kafkaTemplate.send(anyString(), anyString())).thenReturn(null);

        // When
        Invoice result = billingService.payInvoice(testInvoice.getId());

        // Then
        assertNotNull(result);
        assertEquals(Invoice.InvoiceStatus.PAID, result.getStatus());
        verify(invoiceRepository).findById(testInvoice.getId());
        verify(invoiceRepository).save(any(Invoice.class));
        verify(kafkaTemplate).send(eq("billing-events"), contains("Invoice paid: " + testInvoice.getId()));
    }

    @Test
    @DisplayName("Should throw exception when paying non-existent invoice")
    void payInvoice_NotFound_ThrowsException() {
        // Given
        Long nonExistentId = 999L;
        when(invoiceRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> billingService.payInvoice(nonExistentId)
        );
        assertEquals("billing.invoice_not_found", exception.getMessage());
        verify(invoiceRepository).findById(nonExistentId);
        verify(invoiceRepository, never()).save(any(Invoice.class));
        verify(kafkaTemplate, never()).send(anyString(), anyString());
    }

    @Test
    @DisplayName("Should throw exception when paying already paid invoice")
    void payInvoice_AlreadyPaid_ThrowsException() {
        // Given
        Invoice alreadyPaidInvoice = Invoice.builder()

                .studentId(STUDENT_ID)
                .amount(VALID_AMOUNT)
                .dueDate(DUE_DATE)
                .status(Invoice.InvoiceStatus.PAID)
                .build();

        when(invoiceRepository.findById(alreadyPaidInvoice.getId())).thenReturn(Optional.of(alreadyPaidInvoice));

        // When & Then
        ResourceBadRequestException exception = assertThrows(
                ResourceBadRequestException.class,
                () -> billingService.payInvoice(alreadyPaidInvoice.getId())
        );
        assertEquals("billing.invoice_already_paid", exception.getMessage());
        verify(invoiceRepository).findById(alreadyPaidInvoice.getId());
        verify(invoiceRepository, never()).save(any(Invoice.class));
        verify(kafkaTemplate, never()).send(anyString(), anyString());
    }

    @Test
    @DisplayName("Should get student invoices successfully")
    void getStudentInvoices_Success() {
        // Given
        when(invoiceRepository.findByStudentId(STUDENT_ID)).thenReturn(testInvoices);

        // When
        List<Invoice> result = billingService.getStudentInvoices(STUDENT_ID);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(testInvoices.get(0).getId(), result.get(0).getId());
        assertEquals(testInvoices.get(1).getId(), result.get(1).getId());
        verify(invoiceRepository).findByStudentId(STUDENT_ID);
    }

    @Test
    @DisplayName("Should return empty list when student has no invoices")
    void getStudentInvoices_EmptyList() {
        // Given
        when(invoiceRepository.findByStudentId(STUDENT_ID)).thenReturn(Collections.emptyList());

        // When
        List<Invoice> result = billingService.getStudentInvoices(STUDENT_ID);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(invoiceRepository).findByStudentId(STUDENT_ID);
    }

    @Test
    @DisplayName("Should apply late penalties to overdue invoices")
    void applyLatePenalties_OverdueInvoices() {
        // Given
        LocalDate pastDueDate = LocalDate.now().minusDays(15); // 2 weeks overdue
        Invoice overdueInvoice = Invoice.builder()

                .studentId(STUDENT_ID)
                .amount(new BigDecimal("100.00"))
                .dueDate(pastDueDate)
                .status(Invoice.InvoiceStatus.PENDING)
                .build();

        List<Invoice> allInvoices = Collections.singletonList(overdueInvoice);
        when(invoiceRepository.findAll()).thenReturn(allInvoices);
        when(invoiceRepository.save(any(Invoice.class))).thenReturn(overdueInvoice);
        when(kafkaTemplate.send(anyString(), anyString())).thenReturn(null);

        // When
        billingService.applyLatePenalties();

        // Then
        verify(invoiceRepository).findAll();
        verify(invoiceRepository).save(argThat(invoice ->
                invoice.getStatus() == Invoice.InvoiceStatus.OVERDUE &&
                        invoice.getPenaltyAmount() != null &&
                        invoice.getPenaltyAmount().compareTo(BigDecimal.ZERO) > 0
        ));
        verify(kafkaTemplate).send(eq("billing-events"), contains("Penalty applied to invoice: " + overdueInvoice.getId()));
    }

    @Test
    @DisplayName("Should not apply penalties to pending invoices not yet due")
    void applyLatePenalties_NotDueInvoices() {
        // Given
        Invoice futureInvoice = Invoice.builder()

                .studentId(STUDENT_ID)
                .amount(new BigDecimal("100.00"))
                .dueDate(LocalDate.now().plusDays(10))
                .status(Invoice.InvoiceStatus.PENDING)
                .build();

        List<Invoice> allInvoices = Collections.singletonList(futureInvoice);
        when(invoiceRepository.findAll()).thenReturn(allInvoices);

        // When
        billingService.applyLatePenalties();

        // Then
        verify(invoiceRepository).findAll();
        verify(invoiceRepository, never()).save(any(Invoice.class));
        verify(kafkaTemplate, never()).send(anyString(), anyString());
    }

    @Test
    @DisplayName("Should not apply penalties to already paid invoices")
    void applyLatePenalties_PaidInvoices() {
        // Given
        Invoice paidInvoice = Invoice.builder()

                .studentId(STUDENT_ID)
                .amount(new BigDecimal("100.00"))
                .dueDate(LocalDate.now().minusDays(15))
                .status(Invoice.InvoiceStatus.PAID)
                .build();

        List<Invoice> allInvoices = Collections.singletonList(paidInvoice);
        when(invoiceRepository.findAll()).thenReturn(allInvoices);

        // When
        billingService.applyLatePenalties();

        // Then
        verify(invoiceRepository).findAll();
        verify(invoiceRepository, never()).save(any(Invoice.class));
        verify(kafkaTemplate, never()).send(anyString(), anyString());
    }

    @Test
    @DisplayName("Should handle repository exception during invoice creation")
    void createInvoice_RepositoryException_ThrowsException() {
        // Given
        when(invoiceRepository.save(any(Invoice.class))).thenThrow(new RuntimeException("Database error"));

        // When & Then
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> billingService.createInvoice(STUDENT_ID, VALID_AMOUNT, DUE_DATE)
        );
        assertEquals("Database error", exception.getMessage());
        verify(invoiceRepository).save(any(Invoice.class));
        verify(kafkaTemplate, never()).send(anyString(), anyString());
    }

    @Test
    @DisplayName("Should handle repository exception during invoice payment")
    void payInvoice_RepositoryException_ThrowsException() {
        // Given
        when(invoiceRepository.findById(testInvoice.getId())).thenReturn(Optional.of(testInvoice));
        when(invoiceRepository.save(any(Invoice.class))).thenThrow(new RuntimeException("Database error"));

        // When & Then
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> billingService.payInvoice(testInvoice.getId())
        );
        assertEquals("Database error", exception.getMessage());
        verify(invoiceRepository).findById(testInvoice.getId());
        verify(invoiceRepository).save(any(Invoice.class));
        verify(kafkaTemplate, never()).send(anyString(), anyString());
    }

    @Test
    @DisplayName("Should handle repository exception during get student invoices")
    void getStudentInvoices_RepositoryException_ThrowsException() {
        // Given
        when(invoiceRepository.findByStudentId(STUDENT_ID)).thenThrow(new RuntimeException("Database error"));

        // When & Then
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> billingService.getStudentInvoices(STUDENT_ID)
        );
        assertEquals("Database error", exception.getMessage());
        verify(invoiceRepository).findByStudentId(STUDENT_ID);
    }

    @Disabled
    @Test
    @DisplayName("Should handle Kafka exception during invoice creation")
    void createInvoice_KafkaException_StillCreatesInvoice() {
        // Given
        when(invoiceRepository.save(any(Invoice.class))).thenReturn(testInvoice);
        when(kafkaTemplate.send(anyString(), anyString())).thenThrow(new RuntimeException("Kafka error"));

        // When
        Invoice result = billingService.createInvoice(STUDENT_ID, VALID_AMOUNT, DUE_DATE);

        // Then
        assertNotNull(result);
        assertEquals(STUDENT_ID, result.getStudentId());
        verify(invoiceRepository).save(any(Invoice.class));
        verify(kafkaTemplate).send(anyString(), anyString());
    }
    @Disabled
    @Test
    @DisplayName("Should handle Kafka exception during invoice payment")
    void payInvoice_KafkaException_StillPaysInvoice() {
        // Given
        Invoice paidInvoice = Invoice.builder()

                .studentId(STUDENT_ID)
                .amount(VALID_AMOUNT)
                .dueDate(DUE_DATE)
                .status(Invoice.InvoiceStatus.PAID)
                .build();

        when(invoiceRepository.findById(testInvoice.getId())).thenReturn(Optional.of(testInvoice));
        when(invoiceRepository.save(any(Invoice.class))).thenReturn(paidInvoice);
        when(kafkaTemplate.send(anyString(), anyString())).thenThrow(new RuntimeException("Kafka error"));

        // When
        Invoice result = billingService.payInvoice(testInvoice.getId());

        // Then
        assertNotNull(result);
        assertEquals(Invoice.InvoiceStatus.PAID, result.getStatus());
        verify(invoiceRepository).save(any(Invoice.class));
        verify(kafkaTemplate).send(anyString(), anyString());
    }

    @Test
    @DisplayName("Should calculate correct penalty for multiple weeks overdue")
    void applyLatePenalties_MultipleWeeksOverdue() {
        // Given
        LocalDate threeWeeksAgo = LocalDate.now().minusWeeks(3);
        Invoice overdueInvoice = Invoice.builder()

                .studentId(STUDENT_ID)
                .amount(new BigDecimal("100.00"))
                .dueDate(threeWeeksAgo)
                .status(Invoice.InvoiceStatus.PENDING)
                .build();

        List<Invoice> allInvoices = Collections.singletonList(overdueInvoice);
        when(invoiceRepository.findAll()).thenReturn(allInvoices);
        when(invoiceRepository.save(any(Invoice.class))).thenReturn(overdueInvoice);
        when(kafkaTemplate.send(anyString(), anyString())).thenReturn(null);

        // When
        billingService.applyLatePenalties();

        // Then
        verify(invoiceRepository).save(argThat(invoice -> {
            BigDecimal expectedPenalty = new BigDecimal("100.00").multiply(new BigDecimal("0.10")).multiply(new BigDecimal("3"));
            return invoice.getPenaltyAmount().compareTo(expectedPenalty) == 0;
        }));
    }
}