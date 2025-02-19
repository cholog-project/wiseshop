package cholog.wiseshop.db.payment;

import cholog.wiseshop.db.order.Order;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Table(name = "payment")
@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String paymentKey;

    private String paymentOrderId;

    private Long totalAmount;

    @Enumerated(EnumType.STRING)
    @JsonProperty("status")
    private PaymentState state;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id")
    private Order order;

    private LocalDateTime requestedAt;

    private LocalDateTime approvedAt;

    public Payment(
        String paymentKey,
        String paymentOrderId,
        Long totalAmount,
        PaymentState state,
        Order order,
        LocalDateTime requestedAt,
        LocalDateTime approvedAt
    ) {
        this.paymentKey = paymentKey;
        this.paymentOrderId = paymentOrderId;
        this.totalAmount = totalAmount;
        this.state = state;
        this.order = order;
        this.requestedAt = requestedAt;
        this.approvedAt = approvedAt;
    }

    protected Payment() {
    }

    public void addOrder(Order order) {
        this.order = order;
    }

    public Long getId() {
        return id;
    }

    public String getPaymentKey() {
        return paymentKey;
    }

    public String getPaymentOrderId() {
        return paymentOrderId;
    }

    public Long getTotalAmount() {
        return totalAmount;
    }

    public PaymentState getState() {
        return state;
    }

    public Order getOrder() {
        return order;
    }

    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }

    public LocalDateTime getApprovedAt() {
        return approvedAt;
    }
}
