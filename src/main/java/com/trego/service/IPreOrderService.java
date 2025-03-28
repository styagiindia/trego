package com.trego.service;

import com.trego.dao.entity.Order;
import com.trego.dao.entity.OrderItem;
import com.trego.dao.entity.PreOrder;
import com.trego.dto.PreOrderRequestDTO;
import com.trego.dto.response.PreOrderResponseDTO;

import java.util.List;

public interface IPreOrderService {
    public PreOrderResponseDTO savePreOrder(PreOrderRequestDTO preOrder);
}
