package com.trego.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.trego.dao.entity.*;
import com.trego.dao.impl.*;
import com.trego.dto.*;
import com.trego.dto.response.*;
import com.trego.service.IOrderService;
import com.trego.utils.Constants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.*;
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
       // populateCartResponse(preOrderResponseDTO);

        String razorpayOrderId = null;
        if(StringUtils.isEmpty(preOrder.getRazorpayOrderId()) || !(preOrderResponseDTO.getAmountToPay()> 0 && Double.compare(preOrderResponseDTO.getAmountToPay(), preOrder.getTotalPayAmount()) == 0)){
            razorpayOrderId = createRazorPayOrder(orderRequest, preOrderResponseDTO);
            preOrder.setRazorpayOrderId(razorpayOrderId);
            preOrder.setTotalPayAmount(preOrderResponseDTO.getAmountToPay());
            preOrder.setPaymentStatus("unpaid");

            preOrder.setAddressId(orderRequest.getAddressId());
            preOrderRepository.save(preOrder);
        }else{
            razorpayOrderId = preOrder.getRazorpayOrderId();
        }


        orderResponseDTO.setRazorpayOrderId(razorpayOrderId);
        orderResponseDTO.setAmountToPay(preOrderResponseDTO.getAmountToPay());

        return orderResponseDTO;
    }

    @Override
    public OrderValidateResponseDTO validateOrder(OrderValidateRequestDTO orderValidateRequestDTO) throws Exception {
        OrderValidateResponseDTO validateResponseDTO = new OrderValidateResponseDTO();
        boolean isValidate = verifyRazorPayOrder(orderValidateRequestDTO);
        if(isValidate){
            PreOrder preOrder = preOrderRepository.findById(orderValidateRequestDTO.getOrderId()).get();
            preOrder.setPaymentStatus("paid");

            Gson gson = new Gson();
            PreOrderResponseDTO preOrderResponseDTO =  gson.fromJson(preOrder.getPayload(), PreOrderResponseDTO.class);
            preOrderResponseDTO.setOrderId(preOrder.getId());
            populateCartResponse(preOrderResponseDTO);

            if(preOrderResponseDTO.getAddressId() == 0){
                preOrderResponseDTO.setAddressId(preOrder.getAddressId());
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
        }
        validateResponseDTO.setValidate(isValidate);
        validateResponseDTO.setRazorpayOrderId(orderValidateRequestDTO.getRazorpayOrderId());
        validateResponseDTO.setRazorpayPaymentId(orderValidateRequestDTO.getRazorpayPaymentId());
        return validateResponseDTO;
    }

    @Override
    public Page<OrderResponseDTO> fetchAllOrders(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PreOrder>  preOrdersList =  preOrderRepository.fetchAllOrdersByUserId(userId, pageable);
        // Map PreOrder entities to OrderResponseDTO
        Page<OrderResponseDTO> responseDTOPage = preOrdersList.map(preOrder -> {
            OrderResponseDTO responseDTO = new OrderResponseDTO();

            Gson gson = new Gson();
            PreOrderResponseDTO preOrderResponseDTO =  gson.fromJson(preOrder.getPayload(), PreOrderResponseDTO.class);
            responseDTO.setUserId(preOrder.getUserId());
            responseDTO.setRazorpayOrderId(preOrder.getRazorpayOrderId());
            responseDTO.setOrderId(preOrder.getId());
            responseDTO.setMobileNo(preOrder.getMobileNo());
            Optional<Address> addressOpt = addressRepository.findById(preOrder.getAddressId());

            if (addressOpt.isPresent()) {
                Address address = addressOpt.get();
                AddressDTO addressDTO  =  new AddressDTO(
                        address.getId(),
                        address.getAddress(),
                        address.getCity(),
                        address.getLandmark(),
                        address.getPincode(),
                        address.getLat(),
                        address.getLng(), address.getUser().getId() , address.getMobileNo() , address.getName(), address.getAddressType());
                responseDTO.setAddress(addressDTO);
            }



            responseDTO.setAmountToPay(preOrder.getTotalPayAmount());
            responseDTO.setTotalCartValue(preOrderResponseDTO.getTotalCartValue());
            responseDTO.setDiscount(preOrderResponseDTO.getDiscount());
            responseDTO.setPaymentStatus(preOrder.getPaymentStatus());
            responseDTO.setCreateDate(preOrder.getCreatedAt());

            List<OrderDTO> orderDTO = populateOrders(preOrder);
            responseDTO.setOrders(orderDTO);
            return responseDTO;
        });

        return responseDTOPage;

    }

    @Override
    public CancelOrderResponseDTO cancelOrders(CancelOrderRequestDTO request) throws Exception {
        List<Long> orderIds = request.getOrders();
        List<Long> subOrderIds = request.getSubOrders();
        if (orderIds.isEmpty() && subOrderIds.isEmpty()) {
            return new CancelOrderResponseDTO("No orders found to cancel", List.of(), List.of());
        }
        if(!orderIds.isEmpty()) {
            orderIds.forEach(orderId -> {
                System.out.println("Processing order ID: " + orderId);
                // Add logic here to update order status, fetch details, etc.
                PreOrder preOrder =   preOrderRepository.findById(orderId).get();
                subOrderIds.addAll(preOrder.getOrders().stream()
                        .map(Order::getId) // Assuming Order has a getId() method
                        .collect(Collectors.toList()));
                preOrderRepository.updateOrderStatus(orderIds, "cancelled");

            });

        }
        if(!subOrderIds.isEmpty()) {
            orderRepository.updateOrderStatusAndReason(subOrderIds, "cancelled", request.getReason(), request.getReasonId());
        }
        return new CancelOrderResponseDTO("Orders and sub-orders cancelled successfully", orderIds, subOrderIds);
    }

    private List<OrderDTO> populateOrders(PreOrder preOrder) {
        List<OrderDTO> orderDTOList = new ArrayList<>();
// Iterate over orders in PreOrder
        preOrder.getOrders().forEach(order -> {
            OrderDTO orderDTO = new OrderDTO();

            // Populate fields of OrderDTO based on Order entity
            orderDTO.setOrderId(order.getId());
            orderDTO.setPaymentStatus(order.getPaymentStatus());
            orderDTO.setOrderStatus(order.getOrderStatus());
            orderDTO.setTotalAmount(order.getTotalAmount());
            orderDTO.setAddress(order.getAddress());
            orderDTO.setPinCode(order.getPincode());
            orderDTO.setCreateDate(order.getCreatedAt());
            orderDTO.setCancelReason(order.getCancelReason());
            orderDTO.setCancelReasonId(order.getCancelReasonId());
            VendorDTO vendorDTO = new VendorDTO();
            vendorDTO.setId(order.getVendor().getId());
            vendorDTO.setName(order.getVendor().getName());

            if(order.getVendor().getCategory().equalsIgnoreCase("retail")) {
                vendorDTO.setLogo(Constants.LOGO_BASE_URL + Constants.OFFLINE_BASE_URL+ order.getVendor().getLogo());
            }else{
                vendorDTO.setLogo(Constants.LOGO_BASE_URL + Constants.ONLINE_BASE_URL+ order.getVendor().getLogo());
            }

            orderDTO.setVendor(vendorDTO);

            // Populate OrderItems list
            List<OrderItemDTO> orderItemsList = new ArrayList<>();
            order.getOrderItems().forEach(orderItem -> {
                OrderItemDTO orderItemDTO = new OrderItemDTO();
                orderItemDTO.setItemId(orderItem.getId());
                orderItemDTO.setQty(orderItem.getQty());
                orderItemDTO.setMrp(orderItem.getMrp());
               /* MedicineDTO medicineDTO = new MedicineDTO();
                medicineDTO.setName(orderItem.getMedicine().getName());*/

                Map<String, Object> medicineDetails = new HashMap<>();
                medicineDetails.put("medicineId" , orderItem.getMedicine().getId());
                medicineDetails.put("medicineName" , orderItem.getMedicine().getName());
                medicineDetails.put("packing" , orderItem.getMedicine().getPacking());
                medicineDetails.put("medicineLogo" ,Constants.LOGO_BASE_URL  + Constants.MEDICINES_BASE_URL +  orderItem.getMedicine().getPhoto1());

                orderItemDTO.setMedicine(medicineDetails);
                orderItemsList.add(orderItemDTO);
            });

            // Set the populated OrderItems list in OrderDTO
            orderDTO.setOrderItemsList(orderItemsList);

            // Add the OrderDTO to the main list
            orderDTOList.add(orderDTO);
        });

        return orderDTOList;
    }


    public boolean verifyRazorPayOrder(OrderValidateRequestDTO orderValidateRequestDTO) throws Exception {
        String keyId = "rzp_test_oZBGm1luIG1Rpl"; // Replace with actual key
        String keySecret = "S0Pxnueo7AdCYS2HFIa7LXK6"; // Replace with actual key
        String credentials = keyId + ":" + keySecret;
        String encodedAuth = Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
        // API URL
        String url = "https://api.razorpay.com/v1/orders/"+orderValidateRequestDTO.getRazorpayOrderId()+"/payments";

        boolean isValidate = false;
        // Create HTTP Client
        HttpClient client = HttpClient.newHttpClient();
        // Create HTTP Request
        // Create HTTP Request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .header("Authorization", "Basic " + encodedAuth)
                .GET() // Change method to GET
                .build();

        // Send Request
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Print Response
        System.out.println("verifyRazorPayOrder Response Code: " + response.statusCode());
        System.out.println("verifyRazorPayOrder Response Body: " + response.body());
        String orderId = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(response.body());

            // Access the "items" array
            JsonNode itemsNode = rootNode.get("items");
            if (itemsNode == null || !itemsNode.isArray()) {
                System.out.println("No 'items' field found or it's not an array.");
                return false;
            }

            // Iterate through the "items" array
            Iterator<JsonNode> elements = itemsNode.elements();
            while (elements.hasNext()) {
                JsonNode itemNode = elements.next();
                JsonNode idNode = itemNode.get("id");
                JsonNode amountNode = itemNode.get("amount");

                // Check if the "id" matches
                if (idNode != null && orderValidateRequestDTO.getRazorpayPaymentId().equals(idNode.asText()) ) {
                    System.out.println("Valid 'id' found: " + orderValidateRequestDTO.getRazorpayPaymentId());
                    isValidate =  true;
                }
            }

        } catch (Exception e) {
            System.err.println("Error while parsing JSON or validating 'id': " + e.getMessage());


        }
        return isValidate;
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
        BigDecimal amount = new BigDecimal(preOrderResponseDTO.getAmountToPay())
                .setScale(2, BigDecimal.ROUND_HALF_UP);
        int convertedAmount = amount.multiply(BigDecimal.valueOf(100)).intValue();


        jsonObject.addProperty("amount",  convertedAmount);
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
        order.setTotalAmount(orderRequest.getAmountToPay());
        order.setPaymentMethod("other");
        order.setPaymentStatus("paid");
        order.setDiscount(orderRequest.getDiscount());

        return  order;
    }
}