package com.trego.service.impl;

import com.trego.beans.*;
import com.trego.dao.impl.*;
import com.trego.dto.MainDTO;
import com.trego.dto.MedicineDTO;
import com.trego.dto.StockDTO;
import com.trego.dto.VendorDTO;
import com.trego.service.IMainService;
import com.trego.service.IMasterService;
import com.trego.service.IMedicineService;
import com.trego.utils.Constants;
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

    @Autowired
    BannerRepository bannerRepository;

    @Autowired
    IMasterService masterService;

    @Override
    public MainDTO loadAll( double lat, double lng) {
        MainDTO mainDTO = new MainDTO();

        List<Banner> topBanners = bannerRepository.findByPosition("top");
        mainDTO.setTopBanners(topBanners);
        List<Banner> middleBanners = bannerRepository.findByPosition("middle");
        mainDTO.setMiddleBanners(middleBanners);

        List<VendorDTO> topOfflineVendors = new ArrayList<>();
        List<VendorDTO> topOnlineVendors = new ArrayList<>();
        List<Vendor> vendors = vendorRepository.findAll();
        for (Vendor vendor : vendors){

            VendorDTO vendorDTO = populateVendorDTO(vendor);
            List<StockDTO> stockDTOS = new ArrayList<>();
            List<Stock> stocks   = stockRepository.findByVendorId(vendor.getId());
            List<MedicineDTO> medicineDTOList = populateMedicineDTOs(stocks);
            vendorDTO.setMedicines(medicineDTOList);

            if(topOnlineVendors.size() < 5){
                topOfflineVendors.add(vendorDTO);
                topOnlineVendors.add(vendorDTO);
            }
        }

        mainDTO.setSubCategories(masterService.loadCategoriesByType("medicine"));
        mainDTO.setOffLineTopVendor(topOfflineVendors);
        mainDTO.setOnLineTopVendor(topOnlineVendors);
        return mainDTO;
    }

    private static VendorDTO populateVendorDTO(Vendor vendor) {
        VendorDTO vendorDTO = new VendorDTO();
        vendorDTO.setId(vendor.getId());
        vendorDTO.setName(vendor.getName());
        if(vendor.getCategory().equalsIgnoreCase("retail")) {
            vendorDTO.setLogo(Constants.LOGO_BASE_URL + Constants.OFFLINE_BASE_URL+ vendor.getLogo());
        }else{
            vendorDTO.setLogo(Constants.LOGO_BASE_URL + Constants.ONLINE_BASE_URL+ vendor.getLogo());
        }
        vendorDTO.setGstNumber(vendor.getGistin());
        vendorDTO.setLicence(vendor.getDruglicense());
        vendorDTO.setAddress(vendor.getAddress());
        vendorDTO.setLat(vendor.getLat());
        vendorDTO.setLng(vendor.getLng());
        return vendorDTO;
    }

    private static List<MedicineDTO> populateMedicineDTOs(List<Stock> stocks) {
        List<MedicineDTO> medicineDTOList = new ArrayList<>();
        for(Stock stock : stocks){
            Medicine medicine = stock.getMedicine();
            MedicineDTO medicineDTO = new MedicineDTO();
            medicineDTO.setName(medicine.getName());
            medicineDTO.setMedicineType(medicine.getMedicineType());
            medicineDTO.setManufacturer(medicine.getManufacturer());
            medicineDTO.setSaltComposition(medicine.getSaltComposition());
            medicineDTO.setPhoto1(Constants.LOGO_BASE_URL + Constants.MEDICINES_BASE_URL + medicine.getPhoto1());
            medicineDTO.setUseOf(medicine.getUseOf());
            medicineDTO.setPacking(medicine.getPacking());
            medicineDTO.setDiscount(stock.getDiscount());
            medicineDTO.setQty(stock.getQty());
            medicineDTO.setMrp(stock.getMrp());

            medicineDTO.setExpiryDate(stock.getExpiryDate());
            medicineDTOList.add(medicineDTO);
        }
        return medicineDTOList;
    }
}
