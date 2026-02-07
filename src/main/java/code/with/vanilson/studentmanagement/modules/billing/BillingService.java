package code.with.vanilson.studentmanagement.modules.billing;

import code.with.vanilson.studentmanagement.common.exception.ResourceBadRequestException;
import code.with.vanilson.studentmanagement.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BillingService {

    private final InvoiceRepository invoiceRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Transactional
    public Invoice createInvoice(Long studentId, BigDecimal amount, LocalDate dueDate) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResourceBadRequestException("billing.invalid_amount", amount);
        }
        Invoice invoice = Invoice.builder()
                .studentId(studentId)
                .amount(amount)
                .dueDate(dueDate)
                .status(Invoice.InvoiceStatus.PENDING)
                .build();

        Invoice savedInvoice = invoiceRepository.save(invoice);

        // Publish event
        kafkaTemplate.send("billing-events", "Invoice created for student: " + studentId + ", Amount: " + amount);

        return savedInvoice;
    }

    @Transactional
    public Invoice payInvoice(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("billing.invoice_not_found", invoiceId));

        if (invoice.getStatus() == Invoice.InvoiceStatus.PAID) {
            throw new ResourceBadRequestException("billing.invoice_already_paid", invoiceId);
        }

        invoice.setStatus(Invoice.InvoiceStatus.PAID);
        Invoice updatedInvoice = invoiceRepository.save(invoice);

        // Publish event
        kafkaTemplate.send("billing-events", "Invoice paid: " + invoiceId);

        return updatedInvoice;
    }

    public List<Invoice> getStudentInvoices(Long studentId) {
        return invoiceRepository.findByStudentId(studentId);
    }

    @Scheduled(cron = "0 0 0 * * *") // Run every day at midnight
    @Transactional
    public void applyLatePenalties() {
        List<Invoice> allInvoices = invoiceRepository.findAll();
        LocalDate today = LocalDate.now();

        for (Invoice invoice : allInvoices) {
            if (invoice.getStatus() == Invoice.InvoiceStatus.PENDING && today.isAfter(invoice.getDueDate())) {
                invoice.setStatus(Invoice.InvoiceStatus.OVERDUE);

                // Penalty: +10% per week after the 10th (or after due date)
                long weeksLate = ChronoUnit.WEEKS.between(invoice.getDueDate(), today);
                if (weeksLate > 0) {
                    BigDecimal penalty = invoice.getAmount().multiply(new BigDecimal("0.10"))
                            .multiply(new BigDecimal(weeksLate));
                    invoice.setPenaltyAmount(penalty);
                }

                invoiceRepository.save(invoice);
                kafkaTemplate.send("billing-events", "Penalty applied to invoice: " + invoice.getId());
            }
        }
    }
}
