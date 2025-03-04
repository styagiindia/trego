package com.trego.beans;

import com.trego.dto.SubstituteDTO;
import lombok.Data;

import java.util.List;

@Data
public class MedicineWithStockAndVendorDTO {
    private Long id;
    private String name;
    private String manufacturer;
    private String saltComposition;
    private String useOf;
    private String packing;
    private String medicineType;
    private SubstituteDTO substituteDTO;

    private String photo1;
    private List<Stock> stocks;
    //private Vendor vendor;
}
