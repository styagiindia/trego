package com.trego.api;

import com.trego.dto.MedicineDTO;
import com.trego.dto.PreOrderDTO;
import com.trego.dto.response.PreOrderResponseDTO;
import com.trego.service.IPreOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("preorders")
public class PreOrderController {

    @Autowired
    private IPreOrderService preOrderService;

    @PostMapping
    public PreOrderResponseDTO createPreOrder(@RequestBody PreOrderDTO preOrder) {
        return preOrderService.savePreOrder(preOrder);
    }

    @GetMapping("/user/{userId}")
    public PreOrderResponseDTO getOrdersByUserId(@PathVariable Long userId) {

        return preOrderService.getOrdersByUserId(userId);
    }

}