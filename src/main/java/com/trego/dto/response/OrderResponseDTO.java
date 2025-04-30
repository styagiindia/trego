package com.trego.dto.response;

import com.trego.dao.entity.Order;
import com.trego.dto.AddressDTO;
import lombok.Data;

import java.util.List;

@Data
public class OrderResponseDTO {

    
    private long userId;
    private String razorpayOrderId;
    private double amountToPay;

    private  String paymentStatus;
    private List<OrderDTO> orders;


}