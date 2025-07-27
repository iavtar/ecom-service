package com.iavtar.service;

import com.iavtar.domain.entity.User;
import java.util.List;
import java.util.Optional;

public interface UserService {
    
    User createUser(User user);
    
    Optional<User> findById(Long id);
    
    Optional<User> findByUsername(String username);
    
    List<User> findAllUsers();
    
    User updateUser(User user);
    
    void deleteUser(Long id);
    
    boolean existsByUsername(String username);
} 