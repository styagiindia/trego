package com.trego.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.trego.dao.entity.Order;
import com.trego.dto.AddressDTO;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponseDTO {

    
    private long userId;
    private String razorpayOrderId;
    private double amountToPay;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDate;
    private  String paymentStatus;
    private List<OrderDTO> orders;


}