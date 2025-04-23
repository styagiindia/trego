package com.trego.api;

import com.trego.dao.entity.Order;
import com.trego.dto.OrderRequestDTO;
import com.trego.dto.response.OrderResponseDTO;
import com.trego.dto.response.PreOrderResponseDTO;
import com.trego.service.IOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private IOrderService orderService;
/*
    @PostMapping
    public ResponseEntity<OrderResponseDTO> placeOrder(@RequestBody OrderRequestDTO orderRequest) {
        Order placedOrder = orderService.placeOrder(orderRequest.getOrder(), orderRequest.getOrderItems());
        return ResponseEntity.ok(placedOrder);
    }*/
    @PostMapping
    public OrderResponseDTO getOrdersByUserId(@RequestBody OrderRequestDTO orderRequest) throws Exception {

        return orderService.placeOrder(orderRequest);
    }
}