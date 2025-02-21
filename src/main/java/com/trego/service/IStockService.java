package com.trego.service;

import com.trego.beans.Medicine;
import com.trego.beans.Stock;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IStockService {
    List<Stock> findAll();
}