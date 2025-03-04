package com.trego.service.impl;

import com.trego.beans.Category;
import com.trego.dao.impl.*;
import com.trego.dto.*;
import com.trego.service.IMasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MasterServiceImpl implements IMasterService {

    @Autowired
    CategoryRepository categoryRepository;

    @Override
    public List<CategoryDTO> loadCategoriesByType(String type) {
        List<CategoryDTO> categoryDTOs = new ArrayList<>();
         List<Category> categories =  categoryRepository.findByType(type);
         for (Category category : categories){
             CategoryDTO categoryDTO = new CategoryDTO();
             categoryDTO.setId(category.getId());
             categoryDTO.setName(category.getName());
             categoryDTO.setLogo(category.getLogo());
             categoryDTOs.add(categoryDTO);
         }
        return categoryDTOs;
    }
}
