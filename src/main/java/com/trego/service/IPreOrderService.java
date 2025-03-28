package com.trego.service;

import com.trego.dto.PreOrderDTO;

public interface IPreOrderService {
    public PreOrderDTO savePreOrder(PreOrderDTO preOrder);

    PreOrderDTO getOrdersByUserId(Long userId);
}
