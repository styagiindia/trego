package com.trego.dto;

import lombok.Data;

import java.util.List;

@Data
public class PreOrderDTO {

    private long userId;
    private long orderId;
    private long addressId;
    private Double totalCartValue;
    private Double amountToPay;
    private Double discount;
    private List<CartDTO> carts;

}