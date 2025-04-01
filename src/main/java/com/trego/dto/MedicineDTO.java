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
    private List<Stock> offLineStocks;
    private List<Stock> onLineStocks;
    private double mrp;
    private double discount;
    private int qty;
    private String expiryDate;
    private Double actualPrice;
    private String image;
    private String strip;
    private Double offeredPrice;
}
