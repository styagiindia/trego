package com.trego.service.impl;

import com.trego.beans.Medicine;
import com.trego.dao.IMedicineRepository;
import com.trego.service.IMedicineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MedicineServiceImpl implements IMedicineService {

    @Autowired
    IMedicineRepository medicineRepository;

    @Override
    public List<Medicine> findAll() {
        return medicineRepository.findAll();
    }
}
