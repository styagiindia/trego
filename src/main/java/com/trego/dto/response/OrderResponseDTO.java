package com.trego.dto.response;

import com.trego.dto.AddressDTO;
import lombok.Data;

import java.util.List;

@Data
public class OrderResponseDTO {

    
    private long userId;
    private String razorpayOrderId;
    private double amountToPay;
   // private List<CartResponseDTO> carts;
}