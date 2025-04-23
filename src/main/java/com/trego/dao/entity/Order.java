package com.trego.dao.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@Data
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
/*

    @Column(nullable = false)
    private Long userId;
*/

/*    @Column(nullable = false)
    private Long vendorId;*/


    @ManyToOne
    @JoinColumn(name = "vendor_id")
    private Vendor vendor;

    @Column(nullable = false)
    private double totalAmount;

    @Column(nullable = false)
    private double discount;

    @Column(nullable = false)
    private String address;

    @Column(nullable = true)
    private String pincode;

    @Column(nullable = true)
    private String lanmark;

    @Column(nullable = true)
    private String city;

    @Column(nullable = true)
    private String name;

    @Column(nullable = true)
    private long mobile;

    @Column(nullable = false)
    private String email;

    @Column(nullable = true)
    private String paymentStatus;

    @Column
    private String paymentMethod;

    @Column(nullable = true)
    private String orderStatus;

    @Column(nullable = true)
    private LocalDateTime createdAt;

    @Column(nullable = true)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems;

    // Getters and Setters
}