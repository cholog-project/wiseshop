package cholog.wiseshop.api.order.service;

import cholog.wiseshop.api.order.dto.request.CreateOrderRequest;
import cholog.wiseshop.api.order.dto.request.ModifyOrderCountRequest;
import cholog.wiseshop.api.order.dto.response.OrderResponse;
import cholog.wiseshop.db.campaign.Campaign;
import cholog.wiseshop.db.order.Order;
import cholog.wiseshop.db.order.OrderRepository;
import cholog.wiseshop.db.product.Product;
import cholog.wiseshop.db.product.ProductRepository;
import cholog.wiseshop.db.stock.Stock;
import cholog.wiseshop.exception.WiseShopErrorCode;
import cholog.wiseshop.exception.WiseShopException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    public Long createOrder(CreateOrderRequest request) {
        Product product = productRepository.findById(request.productId())
            .orElseThrow(() -> new WiseShopException(WiseShopErrorCode.PRODUCT_NOT_FOUND));
        Stock stock = product.getStock();
        if (!stock.hasQuantity(request.orderQuantity())) {
            throw new WiseShopException(WiseShopErrorCode.ORDER_LIMIT_EXCEED, stock.getTotalQuantity());
        }

        Campaign campaign = product.getCampaign();
        if (!campaign.isInProgress()) {
            throw new WiseShopException(WiseShopErrorCode.CAMPAIGN_NOT_IN_PROGRESS);
        }

        Order order = orderRepository.save(request.from(product));
        stock.reduceQuantity(request.orderQuantity());
        campaign.increaseSoldQuantity(request.orderQuantity());
        return order.getId();
    }

    @Transactional(readOnly = true)
    public OrderResponse readOrder(Long id) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new WiseShopException(WiseShopErrorCode.ORDER_NOT_FOUND));
        return new OrderResponse(order);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> readOrders() {
        return orderRepository.findAll().stream().map(OrderResponse::new).toList();
    }

    public void modifyOrderCount(Long orderId, ModifyOrderCountRequest request) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new WiseShopException(WiseShopErrorCode.ORDER_NOT_FOUND));
        order.updateCount(request.count());
    }

    public void deleteOrder(Long id) {
        orderRepository.findById(id)
            .orElseThrow(() -> new WiseShopException(WiseShopErrorCode.ORDER_NOT_FOUND));
        orderRepository.deleteById(id);
    }
}
