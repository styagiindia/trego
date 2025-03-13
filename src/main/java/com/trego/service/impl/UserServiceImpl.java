package com.trego.service.impl;

import com.trego.dao.entity.User;
import com.trego.dao.impl.UserRepository;
import com.trego.dto.UserDTO;
import com.trego.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public User createUser(UserDTO userDTO) {
        User user = new User();
        user.setName(userDTO.getName());
        user.setPassword(userDTO.getPassword());
        user.setEmail(userDTO.getEmail());
        user.setRole(userDTO.getRole());
        return userRepository.save(user);
    }


    @Override
    public Optional<User> getUserById(Long id) {

        return userRepository.findById(id);
    }


    @Override
    public List<User> getAllUsers() {

        return userRepository.findAll();
    }


    @Override
    public User updateUser(Long id, User userDetails) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        user.setName(userDetails.getName());
        user.setEmail(userDetails.getEmail());
        user.setPassword(userDetails.getPassword());
        user.setPasswordHint(userDetails.getPasswordHint());
        user.setUpdatedAt(userDetails.getUpdatedAt());
        return userRepository.save(user);
    }


    @Override
    public void deleteUser(Long id) {

        userRepository.deleteById(id);
    }
}
