package com.trego.service.impl;

import com.trego.beans.Medicine;
import com.trego.beans.Stock;
import com.trego.dao.impl.StockRepository;
import com.trego.service.IStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class StockServiceImpl implements IStockService {

    @Autowired
    private StockRepository stockRepository;


    @Override
    public List<Stock> findAll() {
        return stockRepository.findAll();
    }



}
