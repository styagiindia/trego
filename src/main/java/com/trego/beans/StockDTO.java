package com.trego.beans;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;


@Data
public class StockDTO {

    private long id;
    private int mrp;
    private int discount;
    private int qty;
    private String expiryDate;
    private MedicineDTO medicine;

}
