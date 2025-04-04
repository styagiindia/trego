package com.trego.dao.impl;

import com.trego.dao.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {

    List<Stock> findByMedicineId(long id);

    List<Stock> findByVendorId(long id);

    Optional<Stock> findByMedicineIdAndVendorId(long medicineId, long vendorId);
}