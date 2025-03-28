package com.trego.dto;

import lombok.Data;

import java.util.List;

@Data
public class PreOrderRequestDTO {

    private long userId;
    private AddressDTO address;
    private Double totalCartValue;
    private Double amountToPay;
    private Double discount;
    private List<CartDTO> carts;

}