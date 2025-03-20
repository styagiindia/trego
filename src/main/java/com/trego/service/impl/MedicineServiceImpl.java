package com.trego.service.impl;

import com.trego.dao.entity.Medicine;
import com.trego.dto.MedicineDTO;
import com.trego.dao.entity.MedicineWithStockAndVendorDTO;
import com.trego.dao.entity.Stock;
import com.trego.dao.impl.MedicineRepository;
import com.trego.dao.impl.StockRepository;
import com.trego.dto.SubstituteDTO;
import com.trego.service.IMedicineService;
import com.trego.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    public MedicineDTO getMedicineById(Long id) {
        Medicine medicine =  medicineRepository.findById(id).orElse(null);

        MedicineDTO medicineDTO = new MedicineDTO();
        medicineDTO.setId(medicine.getId());
        medicineDTO.setMedicineType(medicine.getMedicineType());
        medicineDTO.setName(medicine.getName());
        medicineDTO.setUseOf(medicine.getUseOf());
        medicineDTO.setPacking(medicine.getPacking());
        medicineDTO.setSaltComposition(medicine.getSaltComposition());
        medicineDTO.setManufacturer(medicine.getManufacturer());
        medicineDTO.setDescription(medicine.getDescription());
        medicineDTO.setPhoto1(Constants.LOGO_BASE_URL  + medicine.getPhoto1());

        List<Stock> stocks   = stockRepository.findByMedicineId(medicine.getId());
        medicineDTO.setOffLineStocks(stocks);
        medicineDTO.setOnLineStocks(new ArrayList<>());
        return  medicineDTO;
    }

    @Override
    public Page<MedicineWithStockAndVendorDTO> searchMedicines(String searchText, long vendorId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        MedicineWithStockAndVendorDTO medicineWithStockAndVendorDTO = new MedicineWithStockAndVendorDTO();
        Page<Medicine> medicines = null;
        if(vendorId != 0 ){
            medicines = medicineRepository.findByNameWithVendorId(searchText, vendorId, pageable);

        }else {
             medicines = medicineRepository.findByNameContainingIgnoreCaseOrNameIgnoreCase(searchText, "", pageable);
        }
        return convertResponse(medicines);

    }

    private Page<MedicineWithStockAndVendorDTO> convertResponse(Page<Medicine> medicines) {
        List<Medicine> tempMedicines = medicines.getContent();
        Page<MedicineWithStockAndVendorDTO> medicineDTOs = medicines.map(medicine -> {
          //  List<Stock> stocks = stockRepository.findByMedicineId(medicine.getId());
            MedicineWithStockAndVendorDTO medicineWithStockAndVendorDTO = populateMedicineWithStockVendor(medicine);
            medicineWithStockAndVendorDTO.setStocks(medicine.getStocks());
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
        medicineWithStockAndVendorDTO.setPhoto1(Constants.LOGO_BASE_URL  + medicine.getPhoto1());
        medicineWithStockAndVendorDTO.setUseOf(medicine.getUseOf());
        SubstituteDTO substituteDTO =  new SubstituteDTO();
        substituteDTO.setText("60% Low Price Substitute Avaliable ");
        medicineWithStockAndVendorDTO.setSubstituteDTO(substituteDTO);
        medicineWithStockAndVendorDTO.setPacking(medicine.getPacking());
        return  medicineWithStockAndVendorDTO;
    }
}
