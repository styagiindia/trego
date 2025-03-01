package com.trego.service.impl;

import com.trego.beans.*;
import com.trego.dao.impl.MedicineRepository;
import com.trego.dao.impl.StockRepository;
import com.trego.dao.impl.VendorRepository;
import com.trego.service.IVendorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class VendorServiceImpl implements IVendorService {

    @Autowired
    private VendorRepository vendorRepository;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private MedicineRepository medicineRepository;

    public List<Vendor> findAll() {
        return vendorRepository.findAll(); // This will fetch all vendors
    }

    @Override
    public VendorDTO getVendorById(Long id) {
        VendorDTO vendorDTO = new VendorDTO();
        Vendor vendor = vendorRepository.findById(id).orElse(null);
        vendorDTO.setId(vendor.getId());
        vendorDTO.setName(vendor.getName());
        vendorDTO.setLogo(vendor.getLogo());
        vendorDTO.setGstNumber(vendor.getGistin());
        vendorDTO.setLicence(vendor.getDruglicense());
        vendorDTO.setAddress(vendor.getAddress());
        vendorDTO.setLat(vendor.getLat());
        vendorDTO.setLng(vendor.getLng());

        List<StockDTO> stockDTOS = new ArrayList<>();
        List<Stock> stocks   = stockRepository.findByVendorId(vendor.getId());
        List<MedicineDTO> medicineDTOList = new ArrayList<>();
        for(Stock stock : stocks){
            Medicine medicine = stock.getMedicine();
            MedicineDTO medicineDTO = new MedicineDTO();
            medicineDTO.setId(medicine.getId());
            medicineDTO.setName(medicine.getName());
            medicineDTO.setManufacturer(medicine.getManufacturer());
            medicineDTO.setMedicineType(medicine.getMedicineType());
            medicineDTO.setDescription(medicine.getDescription());
            medicineDTO.setSaltComposition(medicine.getSaltComposition());
            medicineDTO.setPhoto1(medicine.getPhoto1());

            medicineDTO.setDiscount(stock.getDiscount());
            medicineDTO.setQty(stock.getQty());
            medicineDTO.setMrp(stock.getMrp());
            medicineDTO.setExpiryDate(stock.getExpiryDate());

            medicineDTOList.add(medicineDTO);

        }

        vendorDTO.setMedicines(medicineDTOList);
        return vendorDTO;

    }
}
