package com.trego.service.impl;

import com.google.gson.Gson;
import com.trego.dao.entity.PreOrder;
import com.trego.dao.impl.PreOrderRepository;
import com.trego.dto.PreOrderDTO;
import com.trego.service.IPreOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PreOrderServiceImpl implements IPreOrderService {

    @Autowired
    private PreOrderRepository preOrderRepository;

    @Override
    public PreOrderDTO savePreOrder(PreOrderDTO preOrderRequest) {

        Gson gson = new Gson();
        PreOrder preOrder = preOrderRepository.findByUserId(preOrderRequest.getUserId());
        if(preOrder == null){
            preOrder = new PreOrder();
            preOrder.setUserId(preOrderRequest.getUserId());
            preOrder.setCreatedBy("SYSTEM");
            preOrder.setPayload(gson.toJson(preOrderRequest));

        }else{
            preOrder.setPayload(gson.toJson(preOrderRequest));

        }
        preOrderRepository.save(preOrder);
        PreOrderDTO preOrderResponseDTO =  gson.fromJson(preOrder.getPayload(), PreOrderDTO.class);
        preOrderResponseDTO.setOrderId(preOrder.getId());
        return  preOrderResponseDTO;
    }
}