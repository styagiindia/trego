package com.trego.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trego.dao.entity.*;
import com.trego.dao.impl.OrderItemRepository;
import com.trego.dao.impl.OrderRepository;
import com.trego.dao.impl.UserRepository;
import com.trego.dto.OrderRequestDTO;
import com.trego.dto.response.OrderResponseDTO;
import com.trego.service.IOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements IOrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Order placeOrder(Order order, List<OrderItem> orderItems) {
        // Save the order
        Order savedOrder = orderRepository.save(order);

        // Save the order items
        orderItems.forEach(item -> item.setOrder(savedOrder));
        orderItemRepository.saveAll(orderItems);

        return savedOrder;
    }

    @Override
    public OrderResponseDTO placeOrder(OrderRequestDTO orderRequest) throws Exception {
        OrderResponseDTO orderResponseDTO = new OrderResponseDTO();

        orderResponseDTO.setUserId(orderRequest.getUserId());


        orderRequest.getCarts().forEach(cart -> {

            Order order = populateOrder(orderRequest);
            Vendor vendor = new Vendor();
            vendor.setId(cart.getVendorId());
            order.setVendor(vendor);
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
        String razorpayOrderId = createOrder(orderRequest);
        orderResponseDTO.setRazorpayOrderId(razorpayOrderId);
        orderResponseDTO.setCarts(orderRequest.getCarts());
        return orderResponseDTO;
    }

    public String createOrder(OrderRequestDTO orderRequest) throws Exception {
        String keyId = "rzp_test_oZBGm1luIG1Rpl"; // Replace with actual key
        String keySecret = "S0Pxnueo7AdCYS2HFIa7LXK6"; // Replace with actual key
        String credentials = keyId + ":" + keySecret;
        String encodedAuth = Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));

        // API URL
        String url = "https://api.razorpay.com/v1/orders";

        // JSON Payload
        String jsonPayload = "{"
                + "\"amount\": " + orderRequest.getAmountToPay() + ","
                + "\"currency\": \"INR\","
                + "\"receipt\": \"\""
                + "}";

        // Create HTTP Client
        HttpClient client = HttpClient.newHttpClient();

        // Create HTTP Request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .header("Authorization", "Basic " + encodedAuth)
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
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
    private Order populateOrder(OrderRequestDTO orderRequest) {

        Order order = new Order();
        User user = userRepository.findById(orderRequest.getUserId()).get();
        order.setEmail(user.getEmail());
        order.setMobile(user.getMobile());
        order.setUser(user);
        order.setPincode(orderRequest.getAddress().getPincode());
        order.setLanmark(orderRequest.getAddress().getLandmark());
        order.setName(user.getName());
        order.setOrderStatus("new");
        order.setCity(orderRequest.getAddress().getCity());
        order.setAddress(orderRequest.getAddress().getAddress()); // Assuming AddressDTO can be converted
         order.setTotalAmount(orderRequest.getTotalCartValue());
        order.setPaymentMethod("other");
        order.setPaymentStatus("unpaid");
        order.setDiscount(orderRequest.getDiscount());

        return  order;
    }
}