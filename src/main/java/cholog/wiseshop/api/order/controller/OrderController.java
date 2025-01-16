package cholog.wiseshop.api.order.controller;

import cholog.wiseshop.api.order.dto.CreateOrderRequest;
import cholog.wiseshop.api.order.dto.ModifyOrderCountRequest;
import cholog.wiseshop.api.order.dto.OrderResponse;
import cholog.wiseshop.api.order.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<Long> createOrder(@RequestBody CreateOrderRequest request) {
        Long orderId = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(orderId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> readOrder(@PathVariable Long id) {
        OrderResponse response = orderService.readOrder(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping
    public ResponseEntity<Void> modifyOrderCount(@RequestBody ModifyOrderCountRequest request) {
        orderService.modifyOrderCount(request);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
