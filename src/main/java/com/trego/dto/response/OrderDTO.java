package com.trego.dto.response;

import com.trego.dao.entity.Vendor;
import com.trego.dto.VendorDTO;
import lombok.Data;

import java.util.List;

@Data
public class OrderDTO {


    private long  orderId;
    private double totalAmount;

    private String orderStatus;
    private String paymentStatus;
    private String address;
    private String pinCode;
    private VendorDTO vendor;
    private List<OrderItemDTO> orderItemsList;


}