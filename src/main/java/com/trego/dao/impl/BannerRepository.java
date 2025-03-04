package com.trego.dao.impl;

import com.trego.beans.Banner;
import com.trego.beans.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BannerRepository extends JpaRepository<Banner, Long> {

    List<Banner> findByPosition(String position);

}