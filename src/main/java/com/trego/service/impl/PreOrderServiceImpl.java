package com.trego.service.impl;

import com.google.gson.Gson;
import com.trego.dao.entity.PreOrder;
import com.trego.dao.impl.PreOrderRepository;
import com.trego.dto.PreOrderRequestDTO;
import com.trego.dto.response.PreOrderResponseDTO;
import com.trego.service.IPreOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PreOrderServiceImpl implements IPreOrderService {

    @Autowired
    private PreOrderRepository preOrderRepository;

    @Override
    public PreOrderResponseDTO savePreOrder(PreOrderRequestDTO preOrderRequest) {
        PreOrder preOrder = new PreOrder();
        preOrder.setUserId(preOrderRequest.getUserId());
        preOrder.setCreatedBy("SYSTEM");
        Gson gson = new Gson();
        preOrder.setPayload(gson.toJson(preOrderRequest));
        preOrderRepository.save(preOrder);
        PreOrderResponseDTO preOrderResponseDTO =  gson.fromJson(preOrder.getPayload(), PreOrderResponseDTO.class);
        preOrderResponseDTO.setOrderId(preOrder.getId());
        return  preOrderResponseDTO;
    }
}