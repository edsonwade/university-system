package code.with.vanilson.studentmanagement.modules.billing;

import code.with.vanilson.studentmanagement.common.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "invoice")
@lombok.EqualsAndHashCode(callSuper = true)
public class Invoice extends BaseEntity {

    private Long studentId;
    private BigDecimal amount;
    private LocalDate dueDate;
    private BigDecimal penaltyAmount;

    @Enumerated(EnumType.STRING)
    private InvoiceStatus status;

    public enum InvoiceStatus {
        PENDING,
        PAID,
        OVERDUE,
        CANCELLED
    }
}
