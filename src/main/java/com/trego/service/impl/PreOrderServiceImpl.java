package com.trego.service.impl;

import com.google.gson.Gson;
import com.trego.dao.entity.PreOrder;
import com.trego.dao.entity.Stock;
import com.trego.dao.impl.PreOrderRepository;
import com.trego.dao.impl.StockRepository;
import com.trego.dto.CartDTO;
import com.trego.dto.PreOrderDTO;
import com.trego.service.IPreOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PreOrderServiceImpl implements IPreOrderService {

    @Autowired
    private PreOrderRepository preOrderRepository;

    @Autowired
    private StockRepository stockRepository;

    @Override
    public PreOrderDTO savePreOrder(PreOrderDTO preOrderRequest) {


        calculateTotalCartValue(preOrderRequest);
        calculateAmountToPay(preOrderRequest);

        Gson gson = new Gson();
        PreOrder preOrder = preOrderRepository.findByUserId(preOrderRequest.getUserId());
        if(preOrder == null){
            preOrder = new PreOrder();
            preOrder.setUserId(preOrderRequest.getUserId());
            preOrder.setCreatedBy("SYSTEM");
            preOrder.setPayload(gson.toJson(preOrderRequest));

        }else{
            preOrder.setPayload(gson.toJson(preOrderRequest));

        }
        preOrderRepository.save(preOrder);

        PreOrderDTO preOrderResponseDTO =  gson.fromJson(preOrder.getPayload(), PreOrderDTO.class);

        preOrderResponseDTO.setOrderId(preOrder.getId());
        return  preOrderResponseDTO;
    }

    @Override
    public PreOrderDTO getOrdersByUserId(Long userId) {
        Gson gson = new Gson();
        PreOrderDTO preOrderResponseDTO = new PreOrderDTO();
        Optional<PreOrder> preOrder = Optional.ofNullable(preOrderRepository.findByUserId(userId));
        if (preOrder.isPresent()) {
                PreOrder tempPreOrder = preOrder.get();
                preOrderResponseDTO = gson.fromJson(tempPreOrder.getPayload(), PreOrderDTO.class);
                preOrderResponseDTO.setOrderId(tempPreOrder.getId());

        }
        return  preOrderResponseDTO;
    }


    public PreOrderDTO calculateAmountToPay( PreOrderDTO preOrderResponseDTO) {
        List<CartDTO> carts = preOrderResponseDTO.getCarts();
        double amountToPay =  carts.stream()
                .flatMap(cart -> cart.getMedicine().stream()
                        .map(medicine -> {
                            long vendorId = cart.getVendorId();
                            long medicineId = medicine.getId();
                            int quantity = medicine.getQuantity();

                            Optional<Stock> optionalStock = stockRepository.findByMedicineIdAndVendorId(medicineId, vendorId);
                            if(optionalStock.isPresent()){
                                Stock stock = optionalStock.get();
                                int price = stock.getMrp();
                                int discountPercentage = stock.getDiscount();;
                                double totalCartValue=  price * quantity;
                                totalCartValue = totalCartValue - (totalCartValue * discountPercentage / 100.0);
                                return  totalCartValue;
                            }else {
                                return  0.0;
                            }

                        }))
                .mapToDouble(Double::doubleValue) // Map to double for summing
                .sum(); // Calculate total value
        preOrderResponseDTO.setAmountToPay(amountToPay);
        return  preOrderResponseDTO;
    }

    private void calculateTotalCartValue(PreOrderDTO preOrderResponseDTO) {

        double totalCartValue =  preOrderResponseDTO.getCarts().stream()
                .flatMap(cart -> cart.getMedicine().stream()
                        .map(medicine -> {
                            long vendorId = cart.getVendorId();
                            long medicineId = medicine.getId();
                            int quantity = medicine.getQuantity();

                            Optional<Stock> optionalStock = stockRepository.findByMedicineIdAndVendorId(medicineId, vendorId);
                            if(optionalStock.isPresent()){

                                Stock stock = optionalStock.get();
                                int price = stock.getMrp();
                                medicine.setMrp(price);
                                int discountPercentage = stock.getDiscount();;
                                double tempTotalCartValue =  price * quantity;
                                return  tempTotalCartValue;
                            }else {
                                return  0.0;
                            }

                        }))
                .mapToDouble(Double::doubleValue) // Map to double for summing
                .sum(); // Calculate total value
        preOrderResponseDTO.setTotalCartValue(totalCartValue);

    }

}