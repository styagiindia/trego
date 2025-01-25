package com.trego.service.impl;

import com.trego.beans.Medicine;
import com.trego.dao.impl.MedicineRepository;
import com.trego.service.IMedicineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MedicineServiceImpl implements IMedicineService {

    @Autowired
    MedicineRepository medicineRepository;

    @Override
    public List<Medicine> findAll() {
        return medicineRepository.findAll();
    }

    @Override
    public Page<Medicine> searchMedicines(String search, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return medicineRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(search, search, pageable);
      
    }


}
