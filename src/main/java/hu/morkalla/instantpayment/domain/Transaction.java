package hu.morkalla.instantpayment.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name="transaction")
@EqualsAndHashCode(callSuper = false)
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(name="transaction_id")
    private String transactionId;

    @Column(name="source_account_id")
    private Long sourceAccountId;

    @Column(name="target_account_id")
    private Long targetAccountId;

    @Column(name="amount")
    private BigDecimal amount;

    @Column(name="time")
    private LocalDateTime time;

    @Column(name="status")
    private String status;

}
