package com.trego.dao.impl;

import com.trego.beans.Book;
import com.trego.beans.Medicine;
import com.trego.dao.IMedicineRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

// Spring Data JPA creates CRUD implementation at runtime automatically.
public interface MedicineRepository  extends JpaRepository<Medicine, Long> , IMedicineRepository{


}