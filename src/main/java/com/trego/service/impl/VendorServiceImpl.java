package com.trego.service.impl;

import com.trego.beans.Vendor;
import com.trego.dao.impl.VendorRepository;
import com.trego.service.IVendorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class VendorServiceImpl implements IVendorService {

    @Autowired
    private VendorRepository vendorRepository;

    public List<Vendor> findAll() {
        return vendorRepository.findAll(); // This will fetch all vendors
    }
}
