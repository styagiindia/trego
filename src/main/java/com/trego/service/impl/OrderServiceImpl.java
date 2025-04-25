package com.trego.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.trego.dao.entity.*;
import com.trego.dao.impl.*;
import com.trego.dto.CartDTO;
import com.trego.dto.MedicineDTO;
import com.trego.dto.OrderRequestDTO;
import com.trego.dto.PreOrderDTO;
import com.trego.dto.response.CartResponseDTO;
import com.trego.dto.response.OrderResponseDTO;
import com.trego.dto.response.PreOrderResponseDTO;
import com.trego.service.IOrderService;
import com.trego.utils.Constants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import com.google.gson.JsonObject;

@Service
public class OrderServiceImpl implements IOrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private PreOrderRepository preOrderRepository;

    @Autowired
    private UserRepository userRepository;


    @Autowired
    private StockRepository stockRepository;


    @Autowired
    private VendorRepository vendorRepository;


    @Autowired
    private MedicineRepository medicineRepository;

    @Autowired
    private AddressRepository addressRepository;


    @Override
    public OrderResponseDTO placeOrder(OrderRequestDTO orderRequest) throws Exception {
        OrderResponseDTO orderResponseDTO = new OrderResponseDTO();

        orderResponseDTO.setUserId(orderRequest.getUserId());
        PreOrder preOrder = preOrderRepository.findById(orderRequest.getPreOrderId()).get();

        Gson gson = new Gson();
        PreOrderResponseDTO preOrderResponseDTO =  gson.fromJson(preOrder.getPayload(), PreOrderResponseDTO.class);
        preOrderResponseDTO.setOrderId(preOrder.getId());
        populateCartResponse(preOrderResponseDTO);

        String razorpayOrderId = null;
        if(StringUtils.isEmpty(preOrder.getRazorpayOrderId()) || !(preOrderResponseDTO.getAmountToPay()> 0 && Double.compare(preOrderResponseDTO.getAmountToPay(), preOrder.getTotalPayAmount()) == 0)){
            razorpayOrderId = createRazorPayOrder(orderRequest, preOrderResponseDTO);
            preOrder.setRazorpayOrderId(razorpayOrderId);
            preOrder.setTotalPayAmount(preOrderResponseDTO.getAmountToPay());
            preOrder.setPaymentStatus("unpaid");
        }else{
            razorpayOrderId = preOrder.getRazorpayOrderId();
        }

        preOrderResponseDTO.getCarts().forEach(cart -> {
            Order order = populateOrder(preOrderResponseDTO);
            Vendor vendor = new Vendor();
            vendor.setId(cart.getVendorId());
            order.setVendor(vendor);
            order.setPreOrder(preOrder);
            // Save the order
            Order savedOrder = orderRepository.save(order);
            cart.setOrderId(savedOrder.getId());
            List<OrderItem> orderItems = cart.getMedicine().stream()
                    .map(medicine -> {
                        OrderItem item = new OrderItem();
                        Medicine med = new Medicine();
                        med.setId(medicine.getId());
                        item.setMedicine(med);
                        item.setQty(medicine.getQty());
                        item.setMrp(medicine.getMrp());
                        item.setOrderStatus("pending");
                        item.setOrder(savedOrder);  // Ensure the order reference is set
                        return item;
                    })
                    .collect(Collectors.toList());
            // Save all order items in one go
            orderItemRepository.saveAll(orderItems);
        });

        preOrderRepository.save(preOrder);

        orderResponseDTO.setRazorpayOrderId(razorpayOrderId);
        orderResponseDTO.setAmountToPay(preOrderResponseDTO.getAmountToPay());

        return orderResponseDTO;
    }


    public String createRazorPayOrder(OrderRequestDTO orderRequest, PreOrderResponseDTO preOrderResponseDTO) throws Exception {
        String keyId = "rzp_test_oZBGm1luIG1Rpl"; // Replace with actual key
        String keySecret = "S0Pxnueo7AdCYS2HFIa7LXK6"; // Replace with actual key
        String credentials = keyId + ":" + keySecret;
        String encodedAuth = Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
        // API URL
        String url = "https://api.razorpay.com/v1/orders";
        // Create a JSON object
        JsonObject jsonObject = new JsonObject();
        // Add fields to the JSON object
        jsonObject.addProperty("amount", preOrderResponseDTO.getAmountToPay() * 100);
        jsonObject.addProperty("currency", "INR");
        jsonObject.addProperty("receipt", "receipt#123");

        // Create a nested JSON object
        JsonObject notes = new JsonObject();
        notes.addProperty("userId", preOrderResponseDTO.getUserId());

        // Add the nested JSON object to the main object
        jsonObject.add("notes", notes);

        // Create HTTP Client
        HttpClient client = HttpClient.newHttpClient();
        // Create HTTP Request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .header("Authorization", "Basic " + encodedAuth)
                .POST(HttpRequest.BodyPublishers.ofString(jsonObject.toString()))
                .build();

        // Send Request
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Print Response
        System.out.println("Response Code: " + response.statusCode());
        System.out.println("Response Body: " + response.body());
        String orderId = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(response.body());

            // Extract individual fields
            orderId = rootNode.get("id").asText();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return orderId;
    }

    private void populateCartResponse(PreOrderResponseDTO preOrderResponseDTO) {
        List<CartResponseDTO> cartDTOs = preOrderResponseDTO.getCarts().stream().map(cart -> {
        List<MedicineDTO> medicines = cart.getMedicine().stream()
                    .map(medicine -> {
                        Optional<Stock> optionalStock = stockRepository.findByMedicineIdAndVendorId(medicine.getId(), cart.getVendorId());
                        return optionalStock.map(stock -> populateMedicalDTO(medicine, stock)).orElse(null);
                    })
                    .filter(Objects::nonNull) // Filters out null values from the stream
                    .collect(Collectors.toList());
            cart.setMedicine(medicines);
           return cart;
        }).collect(Collectors.toList());

        double totalCartValue = getTotalCartValue(cartDTOs);
        preOrderResponseDTO.setTotalCartValue(totalCartValue);
        preOrderResponseDTO.setAmountToPay(totalCartValue - getDiscount(cartDTOs));
        preOrderResponseDTO.setCarts(cartDTOs);
    }

    private MedicineDTO populateMedicalDTO(MedicineDTO medicineDTO, Stock stock) {

        Medicine tempMedicine = medicineRepository.findById(medicineDTO.getId()).orElse(null);
        medicineDTO.setId(tempMedicine.getId());
        medicineDTO.setMrp(stock.getMrp());
        medicineDTO.setId(tempMedicine.getId());
        medicineDTO.setName(tempMedicine.getName());
        medicineDTO.setManufacturer(tempMedicine.getManufacturer());
        medicineDTO.setMedicineType(tempMedicine.getMedicineType());
        medicineDTO.setUseOf(tempMedicine.getUseOf());
        medicineDTO.setStrip(tempMedicine.getPacking());
        medicineDTO.setImage(Constants.LOGO_BASE_URL  + Constants.MEDICINES_BASE_URL + tempMedicine.getPhoto1());
        medicineDTO.setSaltComposition(tempMedicine.getSaltComposition());
        medicineDTO.setPhoto1(Constants.LOGO_BASE_URL  + Constants.MEDICINES_BASE_URL + tempMedicine.getPhoto1());
        medicineDTO.setDiscount(stock.getDiscount());
        medicineDTO.setMrp(stock.getMrp());
        medicineDTO.setActualPrice(stock.getMrp());
        medicineDTO.setExpiryDate(stock.getExpiryDate());
        return medicineDTO;
    }

    private static double getTotalCartValue(List<CartResponseDTO> cartDTOs) {
        return cartDTOs.stream()
                .flatMap(cart -> cart.getMedicine().stream())
                .mapToDouble(medicine -> medicine.getMrp() * medicine.getQty())
                .sum();
    }

    private static double getDiscount(List<CartResponseDTO> cartDTOs) {
        return cartDTOs.stream()
                .flatMap(cart -> cart.getMedicine().stream())
                .mapToDouble(medicine -> (medicine.getMrp() * medicine.getQty()) * medicine.getDiscount() / 100.0)
                .sum();
    }


    public PreOrderDTO calculateAmountToPay(PreOrderDTO preOrderResponseDTO) {
        List<CartDTO> carts = preOrderResponseDTO.getCarts();
        double amountToPay =  carts.stream()
                .flatMap(cart -> cart.getMedicine().stream()
                        .map(medicine -> {
                            long vendorId = cart.getVendorId();
                            long medicineId = medicine.getId();
                            int qty = medicine.getQty();

                            Optional<Stock> optionalStock = stockRepository.findByMedicineIdAndVendorId(medicineId, vendorId);
                            if(optionalStock.isPresent()){
                                Stock stock = optionalStock.get();
                                double price = stock.getMrp();
                                double discountPercentage = stock.getDiscount();;
                                double totalCartValue=  price * qty;
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
                            int qty = medicine.getQty();

                            Optional<Stock> optionalStock = stockRepository.findByMedicineIdAndVendorId(medicineId, vendorId);
                            if(optionalStock.isPresent()){

                                Stock stock = optionalStock.get();
                                double price = stock.getMrp();
                                medicine.setMrp(price);
                                double discountPercentage = stock.getDiscount();;
                                double tempTotalCartValue =  price * qty;
                                return  tempTotalCartValue;
                            }else {
                                return  0.0;
                            }

                        }))
                .mapToDouble(Double::doubleValue) // Map to double for summing
                .sum(); // Calculate total value
        preOrderResponseDTO.setTotalCartValue(totalCartValue);

    }

    private Order populateOrder(PreOrderResponseDTO orderRequest) {

        Address address = addressRepository.findById(orderRequest.getAddressId()).get();
        Order order = new Order();
        User user = userRepository.findById(orderRequest.getUserId()).get();
        order.setEmail(user.getEmail());
        order.setMobile(user.getMobile());
        order.setUser(user);
        order.setPincode(address.getPincode());
        order.setLanmark(address.getLandmark());
        order.setName(user.getName());
        order.setOrderStatus("new");
        order.setCity(address.getCity());
        order.setAddress(address.getAddress()); // Assuming AddressDTO can be converted
        order.setTotalAmount(orderRequest.getTotalCartValue());
        order.setPaymentMethod("other");
        order.setPaymentStatus("unpaid");
        order.setDiscount(orderRequest.getDiscount());

        return  order;
    }
}