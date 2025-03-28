package com.trego.dao.impl;

import com.trego.dao.entity.PreOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PreOrderRepository extends JpaRepository<PreOrder, Long> {
    PreOrder findByUserId(long userId);
}