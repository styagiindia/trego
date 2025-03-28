package com.trego.api;

import com.trego.dao.entity.Order;
import com.trego.dto.OrderRequest;
import com.trego.service.IOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private IOrderService orderService;

    @PostMapping
    public ResponseEntity<Order> placeOrder(@RequestBody OrderRequest orderRequest) {
        Order placedOrder = orderService.placeOrder(orderRequest.getOrder(), orderRequest.getOrderItems());
        return ResponseEntity.ok(placedOrder);
    }
}