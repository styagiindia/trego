package com.trego.api;

import com.trego.beans.Medicine;
import com.trego.service.IMedicineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MedicineController {

    @Autowired
    IMedicineService medicineService;

    @GetMapping("/medicines")
    public List<Medicine> retrieveMedicines() {
        return medicineService.findAll();
    }
}
