package com.trego.service;

import com.trego.beans.Medicine;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IMedicineService {
    List<Medicine> findAll();

    Page<Medicine> searchMedicines(String searchText, int page, int size);
}
