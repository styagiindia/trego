package com.trego.service;

import com.trego.dao.entity.Order;
import com.trego.dao.entity.OrderItem;
import com.trego.dto.OrderRequestDTO;
import com.trego.dto.OrderValidateRequestDTO;
import com.trego.dto.response.OrderResponseDTO;
import com.trego.dto.response.OrderValidateResponseDTO;
import com.trego.dto.response.PreOrderResponseDTO;

import java.util.List;

public interface IOrderService {

    public OrderResponseDTO placeOrder(OrderRequestDTO orderRequest) throws Exception;

    OrderValidateResponseDTO validateOrder(OrderValidateRequestDTO orderValidateRequestDTO) throws Exception;
}
