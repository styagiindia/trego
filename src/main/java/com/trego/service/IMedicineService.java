package com.trego.service;

import com.trego.beans.Medicine;
import com.trego.beans.MedicineWithStockAndVendorDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IMedicineService {
    List<MedicineWithStockAndVendorDTO> findAll();

    Medicine getMedicineById(Long id);

    Page<MedicineWithStockAndVendorDTO> searchMedicines(String searchText, int page, int size);

}
