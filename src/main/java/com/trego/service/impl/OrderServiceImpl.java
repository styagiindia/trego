package com.trego.service.impl;

import com.trego.dao.entity.Order;
import com.trego.dao.entity.OrderItem;
import com.trego.dao.impl.OrderItemRepository;
import com.trego.dao.impl.OrderRepository;
import com.trego.service.IOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class OrderServiceImpl implements IOrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Override
    public Order placeOrder(Order order, List<OrderItem> orderItems) {
        // Save the order
        Order savedOrder = orderRepository.save(order);

        // Save the order items
        orderItems.forEach(item -> item.setOrder(savedOrder));
        orderItemRepository.saveAll(orderItems);

        return savedOrder;
    }
}