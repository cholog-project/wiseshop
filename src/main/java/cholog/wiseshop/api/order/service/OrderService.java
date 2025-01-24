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
        List<Product> findProducts = productRepository.findProductsByCampaignId(
            request.campaignId());
        if (findProducts.isEmpty()) {
            throw new IllegalArgumentException("상품이 존재하지 않습니다.");
        }
        Product product = findProducts.get(0);
        Stock stock = product.getStock();
        if (!stock.hasQuantity(request.orderQuantity())) {
            throw new IllegalArgumentException(
                String.format("주문 가능한 수량을 초과하였습니다. 주문 가능한 수량 : %d개", stock.getTotalQuantity()));
        }

        Campaign campaign = product.getCampaign();
        if (!campaign.isInProgress()) {
            throw new IllegalArgumentException("현재 캠페인이 진행 중이지 않습니다.");
        }

        Order order = orderRepository.save(request.from(product));
        stock.reduceQuantity(request.orderQuantity());
        campaign.increaseSoldQuantity(request.orderQuantity());
        return order.getId();
    }

    @Transactional(readOnly = true)
    public OrderResponse readOrder(Long id) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("주문 정보가 존재하지 않습니다."));
        return new OrderResponse(order);
    }

    public void modifyOrderCount(Long orderId, ModifyOrderCountRequest request) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new IllegalArgumentException("수정할 상품이 존재하지 않습니다."));
        order.updateCount(request.count());
    }

    public void deleteOrder(Long id) {
        orderRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("삭제할 주문이 존재하지 않습니다."));
        orderRepository.deleteById(id);
    }
}
