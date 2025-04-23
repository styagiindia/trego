package com.trego.dto.response;

import com.trego.dto.AddressDTO;
import lombok.Data;

import java.util.List;

@Data
public class OrderResponseDTO {

    
    private long userId;
    private String razorpayOrderId;
   /* private double totalCartValue;
    private double amountToPay;
    private double discount;*/
    private List<CartResponseDTO> carts;
}