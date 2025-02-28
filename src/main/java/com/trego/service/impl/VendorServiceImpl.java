package com.trego.service.impl;

import com.trego.beans.Stock;
import com.trego.beans.Vendor;
import com.trego.beans.VendorDTO;
import com.trego.dao.impl.StockRepository;
import com.trego.dao.impl.VendorRepository;
import com.trego.service.IVendorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class VendorServiceImpl implements IVendorService {

    @Autowired
    private VendorRepository vendorRepository;

    @Autowired
    private StockRepository stockRepository;

    public List<Vendor> findAll() {
        return vendorRepository.findAll(); // This will fetch all vendors
    }

    @Override
    public VendorDTO getVendorById(Long id) {
        VendorDTO vendorDTO = new VendorDTO();
        Vendor vendor = vendorRepository.findById(id).orElse(null);
        vendorDTO.setId(vendor.getId());
        vendorDTO.setName(vendor.getName());
        vendorDTO.setLogo(vendor.getLogo());
        vendorDTO.setGstNumber(vendor.getGistin());
        vendorDTO.setLicence(vendor.getDruglicense());
        vendorDTO.setAddress(vendor.getAddress());
        vendorDTO.setLat(vendor.getLat());
        vendorDTO.setLng(vendor.getLng());

        List<Stock> stocks   = stockRepository.findByVendorId(vendor.getId());
        vendorDTO.setOffLineStocks(stocks);
        vendorDTO.setOnLineStocks(new ArrayList<>());
        return vendorDTO;

    }
}
