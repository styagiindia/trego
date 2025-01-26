package com.trego.api;

import com.trego.beans.Medicine;
import com.trego.service.IMedicineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
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


    @GetMapping("/medicines/search")
    public Page<Medicine> searchProducts(
            @RequestParam String searchText,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return medicineService.searchMedicines(searchText, page, size);
    }

}
