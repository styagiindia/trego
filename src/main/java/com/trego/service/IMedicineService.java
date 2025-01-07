package com.trego.service;

import com.trego.beans.Medicine;

import java.util.List;

public interface IMedicineService {
    List<Medicine> findAll();
}
