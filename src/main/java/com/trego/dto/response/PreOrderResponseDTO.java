package com.trego.dto.response;

import com.trego.dto.AddressDTO;
import com.trego.dto.CartDTO;
import lombok.Data;

import java.util.List;

@Data
public class PreOrderResponseDTO {

    private long orderId;
    private long userId;
    private AddressDTO addressDTO;
    List<CartDTO> carts;

}