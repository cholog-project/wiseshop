package cholog.wiseshop.api.order.service;

import cholog.wiseshop.api.order.dto.CreateOrderRequest;
import cholog.wiseshop.api.order.dto.ModifyOrderCountRequest;
import cholog.wiseshop.api.order.dto.OrderResponse;
import cholog.wiseshop.db.order.Order;
import cholog.wiseshop.db.order.OrderRepository;
import cholog.wiseshop.db.product.Product;
import cholog.wiseshop.db.product.ProductRepository;
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
        Product orderProduct = productRepository.findById(request.productId())
                .orElseThrow(() -> new IllegalArgumentException("주문할 상품이 존재하지 않습니다."));
        Order order = orderRepository.save(request.from(orderProduct));
        return order.getId();
    }

    @Transactional(readOnly = true)
    public OrderResponse readOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("주문 정보가 존재하지 않습니다."));
        return new OrderResponse(order);
    }

    public void modifyOrderCount(ModifyOrderCountRequest request) {
        Order order = orderRepository.findById(request.id())
                .orElseThrow(() -> new IllegalArgumentException("수정할 상품이 존재하지 않습니다."));
        order.updateCount(request.count());
    }

    public void deleteOrder(Long id) {
        orderRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("삭제할 주문이 존재하지 않습니다."));
        orderRepository.deleteById(id);
    }
}
