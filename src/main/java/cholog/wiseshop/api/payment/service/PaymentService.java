package cholog.wiseshop.api.payment.service;

import cholog.wiseshop.api.payment.dto.PaymentRequest;
import cholog.wiseshop.common.client.PaymentClient;
import cholog.wiseshop.db.order.Order;
import cholog.wiseshop.db.order.OrderRepository;
import cholog.wiseshop.db.payment.Payment;
import cholog.wiseshop.db.payment.PaymentRepository;
import cholog.wiseshop.db.payment.PaymentState;
import cholog.wiseshop.exception.WiseShopErrorCode;
import cholog.wiseshop.exception.WiseShopException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);
    private final PaymentClient paymentClient;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentClient paymentClient, OrderRepository orderRepository,
        PaymentRepository paymentRepository) {
        this.paymentClient = paymentClient;
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
    }

    public Long confirmPayment(PaymentRequest request) {
        try {
            var response = paymentClient.confirm(request);
            if (response == null) {
                log.warn("response is null request: {}", request);
                throw new WiseShopException(WiseShopErrorCode.PAYMENT_FAILED);
            }
            Long orderId = extractOrderId(response.orderId());
            Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new WiseShopException(WiseShopErrorCode.PAYMENT_FAILED));
            log.info("payment Success payment Response : {} ", response);
            var payment = paymentRepository.save(
                new Payment(
                    response.paymentKey(),
                    response.orderId(),
                    response.totalAmount(),
                    PaymentState.valueOf(response.state()),
                    order,
                    response.requestedAt(),
                    response.approvedAt()
                ));

            return payment.getId();
        } catch (IllegalArgumentException e) {
            log.warn(e.getMessage());
            throw e;
        } catch (Exception e) {
            log.warn(e.getMessage());
            throw new WiseShopException(WiseShopErrorCode.PAYMENT_FAILED);
        }
    }


    private Long extractOrderId(String paymentOrderId) {
        String pattern = "wise-(.*?)-.*";

        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(paymentOrderId);
        if (matcher.matches()) {
            return Long.parseLong(matcher.group(1));
        } else {
            throw new WiseShopException(WiseShopErrorCode.INVALID_PAYMENT_ORDER_ID);
        }
    }
}
