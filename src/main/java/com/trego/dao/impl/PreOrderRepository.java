package com.trego.dao.impl;

import com.trego.dao.entity.Medicine;
import com.trego.dao.entity.PreOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PreOrderRepository extends JpaRepository<PreOrder, Long> {
    PreOrder findByUserId(long userId);
    PreOrder findByUserIdAndPaymentStatus(Long userId, String paymentStatus);
    PreOrder findByIdAndPaymentStatus(Long id, String paymentStatus);

/*

    @Query("SELECT p FROM preorders p " +
            "JOIN FETCH p.orders o " +
            "WHERE  p.userId = :userId")
    Page<PreOrder> fetchAllOrdersByUserId(Long userId, Pageable pageable);
*/

        @Query("SELECT p FROM preorders p " +
                "JOIN FETCH p.orders o " +
                "WHERE p.userId = :userId")
        Page<PreOrder> fetchAllOrdersByUserId(@Param("userId") Long userId, Pageable pageable);

}