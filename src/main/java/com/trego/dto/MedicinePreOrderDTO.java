package com.trego.dto;

import com.trego.dao.entity.Stock;
import lombok.Data;

import java.util.List;

@Data
public class MedicinePreOrderDTO {

    private long id;
    private int mrp;
    private int quantity;
}
