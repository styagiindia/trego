package com.trego.service.impl;

import com.trego.beans.Medicine;
import com.trego.beans.MedicineWithStockAndVendorDTO;
import com.trego.beans.Stock;
import com.trego.dao.impl.MedicineRepository;
import com.trego.dao.impl.StockRepository;
import com.trego.service.IMedicineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MedicineServiceImpl implements IMedicineService {

    @Autowired
    MedicineRepository medicineRepository;

    @Autowired
    StockRepository stockRepository;

    @Override
    public List<MedicineWithStockAndVendorDTO> findAll() {
        List<MedicineWithStockAndVendorDTO> medicineWithStockAndVendorDTOList = new ArrayList<>();
        List<Medicine> medicines = medicineRepository.findAll();
        for (Medicine medicine : medicines) {

            MedicineWithStockAndVendorDTO  medicineWithStockAndVendorDTO = populateMedicineWithStockVendor(medicine);
            List<Stock> stocks = stockRepository.findByMedicineId(medicine.getId());
            medicineWithStockAndVendorDTO.setStocks(stocks);
            medicineWithStockAndVendorDTOList.add(medicineWithStockAndVendorDTO);
        }

        return  medicineWithStockAndVendorDTOList;
    }

    @Override
    public Medicine getMedicineById(Long id) {
        return medicineRepository.findById(id).orElse(null);
    }

    @Override
    public Page<MedicineWithStockAndVendorDTO> searchMedicines(String searchText, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        MedicineWithStockAndVendorDTO medicineWithStockAndVendorDTO = new MedicineWithStockAndVendorDTO();

        Page<Medicine> medicines = medicineRepository.findByNameContainingIgnoreCaseOrNameIgnoreCase(searchText, "", pageable);
        return convertResponse(medicines);

    }

    private Page<MedicineWithStockAndVendorDTO> convertResponse(Page<Medicine> medicines) {
        List<Medicine> tempMedicines = medicines.getContent();
        Page<MedicineWithStockAndVendorDTO> medicineDTOs = medicines.map(medicine -> {
            List<Stock> stocks = stockRepository.findByMedicineId(medicine.getId());
            MedicineWithStockAndVendorDTO medicineWithStockAndVendorDTO = populateMedicineWithStockVendor(medicine);
            medicineWithStockAndVendorDTO.setStocks(stocks);
            return medicineWithStockAndVendorDTO;
        });
        return medicineDTOs;
    }


    private MedicineWithStockAndVendorDTO populateMedicineWithStockVendor(Medicine medicine) {
        MedicineWithStockAndVendorDTO medicineWithStockAndVendorDTO = new MedicineWithStockAndVendorDTO();
        medicineWithStockAndVendorDTO.setId(medicine.getId());
        medicineWithStockAndVendorDTO.setName(medicine.getName());
        medicineWithStockAndVendorDTO.setMedicineType(medicine.getMedicineType());
        medicineWithStockAndVendorDTO.setManufacturer(medicine.getManufacturer());
        medicineWithStockAndVendorDTO.setSaltComposition(medicine.getSaltComposition());
        medicineWithStockAndVendorDTO.setPhoto1(medicine.getPhoto1());
        return  medicineWithStockAndVendorDTO;
    }
}
