package com.trego.dao;

import com.trego.beans.Medicine;

import java.util.List;

public interface IMedicineDao {
    List<Medicine> findAll();
}
