package com.trego.service.impl;

import com.trego.beans.Medicine;
import com.trego.dao.IMedicineDao;
import com.trego.service.IMedicineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MedicineServiceImpl implements IMedicineService {

    @Autowired
    IMedicineDao medicineDao;

    @Override
    public List<Medicine> findAll() {

        return medicineDao.findAll();
    }
}
