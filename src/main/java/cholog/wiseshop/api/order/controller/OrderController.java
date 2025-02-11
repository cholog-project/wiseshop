package cholog.wiseshop.api.order.controller;

import cholog.wiseshop.api.order.dto.request.CreateOrderRequest;
import cholog.wiseshop.api.order.dto.request.CreateOrderSessionRequest;
import cholog.wiseshop.api.order.dto.response.OrderResponse;
import cholog.wiseshop.api.order.service.OrderService;
import cholog.wiseshop.common.auth.Auth;
import cholog.wiseshop.db.member.Member;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1")
@RestController
public class OrderController {

    private final Logger logger = LoggerFactory.getLogger(OrderController.class);

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/orders/session")
    public ResponseEntity<Void> createOrderSession(HttpSession session,
                                                   @RequestBody CreateOrderSessionRequest request) {
        session.setAttribute(request.paymentOrderId(),request.amount());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/orders")
    public ResponseEntity<Long> createOrder(@Auth Member member, @RequestBody CreateOrderRequest request) {
        Long orderId = orderService.createOrder(request, member);
        return ResponseEntity.status(HttpStatus.CREATED).body(orderId);
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<OrderResponse> readOrder(@PathVariable Long id) {
        OrderResponse response = orderService.readOrder(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/orders")
    public ResponseEntity<List<OrderResponse>> readMemberOrders(@Auth Member member) {
        List<OrderResponse> response = orderService.readMemberOrders(member);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
