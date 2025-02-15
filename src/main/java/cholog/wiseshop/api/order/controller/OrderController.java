package cholog.wiseshop.api.order.controller;

import cholog.wiseshop.api.order.dto.request.CreateOrderRequest;
import cholog.wiseshop.api.order.dto.response.MemberOrderListResponse;
import cholog.wiseshop.api.order.dto.response.MemberOrderResponse;
import cholog.wiseshop.api.order.service.OrderService;
import cholog.wiseshop.common.auth.Auth;
import cholog.wiseshop.db.member.Member;
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

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/orders")
    public ResponseEntity<Long> createOrder(@Auth Member member, @RequestBody CreateOrderRequest request) {
        Long orderId = orderService.createOrder(request, member);
        return ResponseEntity.status(HttpStatus.CREATED).body(orderId);
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<MemberOrderResponse> readOrder(@Auth Member member, @PathVariable Long id) {
        MemberOrderResponse response = orderService.readOrder(member, id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/orders")
    public ResponseEntity<MemberOrderListResponse> readMemberOrders(@Auth Member member) {
        MemberOrderListResponse response = orderService.readMemberOrders(member);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
