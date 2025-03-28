package com.trego.service;

import com.trego.dao.entity.Order;
import com.trego.dao.entity.OrderItem;

import java.util.List;

public interface IOrderService {

    public Order placeOrder(Order order, List<OrderItem> orderItems);
}
