package com.trego.dao.impl;

import com.trego.dao.entity.Medicine;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

// Spring Data JPA creates CRUD implementation at runtime automatically.
public interface MedicineRepository  extends JpaRepository<Medicine, Long> {

    Page<Medicine> findByNameContainingIgnoreCaseOrNameIgnoreCase(String searchText, String description, Pageable pageable);
}