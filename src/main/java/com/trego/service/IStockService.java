package com.trego.service;

import com.trego.dao.entity.Stock;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IStockService {
    List<Stock> findAll();
}