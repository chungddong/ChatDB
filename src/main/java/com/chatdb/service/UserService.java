package com.chatdb.service;

import com.chatdb.entity.User;
import com.chatdb.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }
    
    public Optional<User> getUserByUserId(String userId) {
        return userRepository.findByUserId(userId);
    }
    
    public User saveUser(User user) {
        return userRepository.save(user);
    }
    
    public User createUser(String userId, String password) {
        if (userRepository.existsByUserId(userId)) {
            throw new RuntimeException("User already exists: " + userId);
        }
        User user = new User(userId, password);
        return userRepository.save(user);
    }
    
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
    
    public boolean existsByUserId(String userId) {
        return userRepository.existsByUserId(userId);
    }
}