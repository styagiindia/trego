package com.trego.service;

import com.trego.beans.Stock;
import com.trego.beans.Vendor;
import com.trego.beans.VendorDTO;

import java.util.List;

public interface IVendorService {
    List<Vendor> findAll();

    VendorDTO getVendorById(Long id);
}