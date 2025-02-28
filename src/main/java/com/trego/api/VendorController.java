package com.trego.api;

import com.trego.beans.Stock;
import com.trego.beans.Vendor;
import com.trego.beans.VendorDTO;
import com.trego.service.IStockService;
import com.trego.service.IVendorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class VendorController {

    @Autowired
    IVendorService vendorService;

    @GetMapping("/vendors")
    public List<Vendor> retrieveAllVendors() {
        return vendorService.findAll();
    }


    @GetMapping("/vendors/{id}")
    public VendorDTO retrieveVendorById(@PathVariable Long id) {

        return vendorService.getVendorById(id);
    }

}
