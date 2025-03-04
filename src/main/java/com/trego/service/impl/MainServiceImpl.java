package com.trego.service.impl;

import com.trego.beans.Medicine;
import com.trego.beans.MedicineWithStockAndVendorDTO;
import com.trego.beans.Stock;
import com.trego.beans.Vendor;
import com.trego.dao.impl.MedicineRepository;
import com.trego.dao.impl.StockRepository;
import com.trego.dao.impl.VendorRepository;
import com.trego.dto.MainDTO;
import com.trego.dto.MedicineDTO;
import com.trego.dto.StockDTO;
import com.trego.dto.VendorDTO;
import com.trego.service.IMainService;
import com.trego.service.IMedicineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Service
public class MainServiceImpl implements IMainService {

    @Autowired
    MedicineRepository medicineRepository;

    @Autowired
    StockRepository stockRepository;

    @Autowired
    VendorRepository vendorRepository;

    @Override
    public MainDTO loadAll(String address,  long lat, long lng) {
        MainDTO mainDTO = new MainDTO();
        List<VendorDTO> topOfflineVendors = new ArrayList<>();
        List<VendorDTO> topOnlineVendors = new ArrayList<>();


        List<Vendor> vendors = vendorRepository.findAll();
        for (Vendor vendor : vendors){

            VendorDTO vendorDTO = new VendorDTO();
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
            List<MedicineDTO> medicineDTOList = populateMedicineDTOs(stocks);
            vendorDTO.setMedicines(medicineDTOList);

            if(topOnlineVendors.size() < 5){
                topOfflineVendors.add(vendorDTO);
                topOnlineVendors.add(vendorDTO);
            }
        }
        mainDTO.setOffLineTopVendor(topOfflineVendors);
        mainDTO.setOnLineTopVendor(topOnlineVendors);
        return mainDTO;
    }

    private static List<MedicineDTO> populateMedicineDTOs(List<Stock> stocks) {
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
        return medicineDTOList;
    }
}
