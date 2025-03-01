package com.trego.beans;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.util.List;

@Data
public class VendorDTO {

    private Long id;
    private String name;
    private String licence;
    private String gstNumber;
    private String address;
    private String logo;
    private String lat;
    private String lng;
    private List<MedicineDTO> medicines;

}
