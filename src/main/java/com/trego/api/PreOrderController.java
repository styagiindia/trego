package com.trego.api;

import com.trego.dto.PreOrderDTO;
import com.trego.service.IPreOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("pre-orders")
public class PreOrderController {

    @Autowired
    private IPreOrderService preOrderService;

    @PostMapping
    public PreOrderDTO createPreOrder(@RequestBody PreOrderDTO preOrder) {
        return preOrderService.savePreOrder(preOrder);
    }
}