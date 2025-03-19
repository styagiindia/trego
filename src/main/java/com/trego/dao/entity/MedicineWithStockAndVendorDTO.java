<<<<<<<< HEAD:src/main/java/com/trego/dto/MedicineWithStockAndVendorDTO.java
package com.trego.dto;
========
package com.trego.dao.entity;
>>>>>>>> dev:src/main/java/com/trego/dao/entity/MedicineWithStockAndVendorDTO.java

import com.trego.dao.entity.Stock;
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
