package com.trego.dto;


import lombok.Data;

import java.util.List;

@Data
public class MainDTO {

  // List<Banner> topBanners;
   List<VendorDTO> offLineTopVendor;
   //List<Banner> middleBanners;
  // List<SubCategory> subCategories;
   List<VendorDTO> onLineTopVendor;
}

