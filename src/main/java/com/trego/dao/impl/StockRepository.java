package com.trego.dao.impl;

import com.trego.beans.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {

    List<Stock> findByMedicineId(long id);

    List<Stock> findByVendorId(long id);
}