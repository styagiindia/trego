package com.trego.beans;

import lombok.Data;

import java.util.List;

@Data
public class MedicineWithStockAndVendorDTO {
    private Long id;
    private String name;
    private String manufacturer;
    private String saltComposition;

    private String medicineType;

    private String photo1;
    private List<Stock> stocks;
    //private Vendor vendor;
}
