package com.trego.dao.impl;

import com.trego.beans.Medicine;
import com.trego.dao.IMedicineDao;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MedicineDaoImpl implements IMedicineDao {

    /**
     * @return
     */
    @Override
    public List<Medicine> findAll() {
        Medicine medicine = new Medicine();
        medicine.setName("aaaaaaaa");

        return List.of(medicine);
    }
}
