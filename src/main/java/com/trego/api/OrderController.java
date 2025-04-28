package com.trego.api;

import com.trego.dao.entity.Order;
import com.trego.dto.OrderRequestDTO;
import com.trego.dto.OrderValidateRequestDTO;
import com.trego.dto.response.OrderResponseDTO;
import com.trego.dto.response.OrderValidateResponseDTO;
import com.trego.dto.response.PreOrderResponseDTO;
import com.trego.service.IOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private IOrderService orderService;

    @PostMapping
    public OrderResponseDTO placeOrder(@RequestBody OrderRequestDTO orderRequest) throws Exception {
        return orderService.placeOrder(orderRequest);
    }

    @PostMapping("/validateOrder")
    public OrderValidateResponseDTO validateOrder(@RequestBody OrderValidateRequestDTO orderValidateRequestDTO) throws Exception {
        return orderService.validateOrder(orderValidateRequestDTO);
    }
}