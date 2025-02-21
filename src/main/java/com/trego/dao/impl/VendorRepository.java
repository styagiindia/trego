package com.trego.dao.impl;

import com.trego.beans.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VendorRepository extends JpaRepository<Vendor, Long> {
    // Custom queries can be defined here, if necessary
}