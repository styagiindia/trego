package com.trego.dto;

import com.trego.dao.entity.Stock;
import lombok.Data;

import java.util.List;

@Data
public class MedicineDTO {

    private long id;
    private String name;
    private String manufacturer;
    private String saltComposition;
    private String medicineType;
    private String description;
    private String photo1;

    private String useOf;
    private String packing;
    private List<Stock> offLineStocks;
    private List<Stock> onLineStocks;
    private int mrp;
    private int discount;
    private int qty;
    private String expiryDate;


    private Integer quantity;
    private Double actualPrice;
    private String useFor;
    private String image;
    private String strip;
    private Double offeredPrice;
}
