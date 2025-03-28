package com.trego.api;

import com.trego.dao.entity.PreOrder;
import com.trego.dto.PreOrderRequestDTO;
import com.trego.dto.response.PreOrderResponseDTO;
import com.trego.service.IPreOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("pre-orders")
public class PreOrderController {

    @Autowired
    private IPreOrderService preOrderService;

    @PostMapping
    public PreOrderResponseDTO createPreOrder(@RequestBody PreOrderRequestDTO preOrder) {
        return preOrderService.savePreOrder(preOrder);
      //  PreOrder savedPreOrder = preOrderService.savePreOrder(preOrder);
      //  return ResponseEntity.ok(savedPreOrder);
    }
}