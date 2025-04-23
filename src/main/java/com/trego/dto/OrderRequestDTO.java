package com.trego.dto;

import com.trego.dto.response.CartResponseDTO;
import lombok.Data;

import java.util.List;

@Data
public class OrderRequestDTO {
  //  private Order order;
   // private List<OrderItem> orderItems;

    // Getters and Setters
//    private long orderId;
    //  private long addressId;


    private long userId;
    AddressDTO address;
    private double totalCartValue;
    private double amountToPay;
    private double discount;
    private List<CartResponseDTO> carts;
}