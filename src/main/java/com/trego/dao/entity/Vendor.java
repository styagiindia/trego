package com.trego.dao.entity;


import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity(name = "vendors")
public class Vendor {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private  String druglicense;
    private String gistin;
    private String category;
    private String logo;
    private String lat;
    private String lng;
    private String address;


    @OneToMany(mappedBy = "vendor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Stock> stocks; // One-to-many relationship with Stock
}
