package cholog.wiseshop.api.order.service;

import cholog.wiseshop.api.order.dto.request.CreateOrderRequest;
import cholog.wiseshop.api.order.dto.response.OrderResponse;
import cholog.wiseshop.api.payment.dto.PaymentRequest;
import cholog.wiseshop.api.payment.service.PaymentClient;
import cholog.wiseshop.db.campaign.Campaign;
import cholog.wiseshop.db.member.Member;
import cholog.wiseshop.db.order.Order;
import cholog.wiseshop.db.order.OrderRepository;
import cholog.wiseshop.db.payment.Payment;
import cholog.wiseshop.db.payment.PaymentRepository;
import cholog.wiseshop.db.product.Product;
import cholog.wiseshop.db.product.ProductRepository;
import cholog.wiseshop.db.stock.Stock;
import cholog.wiseshop.exception.WiseShopErrorCode;
import cholog.wiseshop.exception.WiseShopException;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    private final PaymentClient paymentClient;
    private final PaymentRepository paymentRepository;

    public OrderService(OrderRepository orderRepository,
                        ProductRepository productRepository,
                        PaymentClient paymentClient,
                        PaymentRepository paymentRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.paymentClient = paymentClient;
        this.paymentRepository = paymentRepository;
    }

    public Long createOrder(CreateOrderRequest request, Member member, HttpSession session) {
        Product product = productRepository.findById(request.productId())
            .orElseThrow(() -> new WiseShopException(WiseShopErrorCode.PRODUCT_NOT_FOUND));
        Campaign campaign = product.getCampaign();
        validateCampaignState(campaign);
        Stock stock = product.getStock();
        validateQuantity(campaign, stock, request.orderQuantity());
        Member campaignOwner = campaign.getMember();
        validateOrderOwner(campaignOwner, member);
        Order order = orderRepository.save(request.from(product, member));
        campaign.increaseSoldQuantity(request.orderQuantity());

        validatePaymentRequest(request, session);
        Payment payment = paymentClient.confirm(
            new PaymentRequest(request.paymentOrderId(), request.amount(), request.paymentKey()));
        payment.addOrder(order);
        paymentRepository.save(payment);

        return order.getId();
    }

    @Transactional(readOnly = true)
    public OrderResponse readOrder(Long id) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new WiseShopException(WiseShopErrorCode.ORDER_NOT_FOUND));
        return new OrderResponse(order);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> readMemberOrders(Member member) {
        return orderRepository.findByMemberId(member.getId())
            .stream().map(OrderResponse::new).toList();
    }

	public void deleteOrder(Long id) {
		orderRepository.findById(id)
			.orElseThrow(() -> new WiseShopException(WiseShopErrorCode.ORDER_NOT_FOUND));
		orderRepository.deleteById(id);
	}

    public void validateQuantity(Campaign campaign, Stock stock, int orderQuantity) {
        int remainQuantity = stock.getTotalQuantity() - campaign.getSoldQuantity();

        if (remainQuantity - orderQuantity < 0) {
            throw new WiseShopException(WiseShopErrorCode.ORDER_LIMIT_EXCEED, remainQuantity);
        }
    }

    public void validateCampaignState(Campaign campaign) {
        if (!campaign.isInProgress()) {
            throw new WiseShopException(WiseShopErrorCode.CAMPAIGN_NOT_IN_PROGRESS);
        }
    }

    public void validateOrderOwner(Member campaignOwner, Member orderMember) {
        if (Objects.equals(campaignOwner.getId(), orderMember.getId())) {
            throw new WiseShopException(WiseShopErrorCode.ORDER_NOT_AVAILABLE);
        }
    }

    private void validatePaymentRequest(CreateOrderRequest request, HttpSession session) {
        Long amount = (Long) session.getAttribute(request.paymentOrderId());
        if (amount == null || !Objects.equals(amount, request.amount())) {
            throw new WiseShopException(WiseShopErrorCode.PAYMENT_NOT_MATCHED);
        }
    }
}
