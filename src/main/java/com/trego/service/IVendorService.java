package com.trego.service;

import com.trego.beans.Stock;
import com.trego.beans.Vendor;

import java.util.List;

public interface IVendorService {
    List<Vendor> findAll();
}