package com.trego.dto;

import com.trego.dao.entity.Order;
import com.trego.dao.entity.OrderItem;
import lombok.Data;

import java.util.List;

@Data
public class OrderRequest {
    private Order order;
    private List<OrderItem> orderItems;

    // Getters and Setters
}