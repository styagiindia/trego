package com.trego.dto.response;

import com.trego.dto.CartDTO;
import lombok.Data;

import java.util.List;

@Data
public class PreOrderResponseDTO {

    private long userId;
    private long orderId;
    private long addressId;
    private double totalCartValue;
    private double amountToPay;
    private double discount;
    private List<CartResponseDTO> carts;

}