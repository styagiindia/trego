package com.trego.dao.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "pre_orders")
public class PreOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String payload;

    @Column(nullable = true, name = "razorpay_order_id")
    private String razorpayOrderId;


    @Column(nullable = true, name = "total_pay_amount")
    private double totalPayAmount;

    @Column(name = "payment_status", nullable = true)
    private String paymentStatus;

    @Column(name = "created_by", nullable = false)
    private String createdBy;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
}