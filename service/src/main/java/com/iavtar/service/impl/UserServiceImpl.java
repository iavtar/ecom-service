package com.iavtar.service.impl;

import com.iavtar.domain.context.TransactionContext;
import com.iavtar.domain.entity.User;
import com.iavtar.service.UserService;
import com.iavtar.infrastructure.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    
    private final UserRepository userRepository;
    
    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    @Override
    public User createUser(User user) {
        String transactionId = TransactionContext.getTransactionId();
        logger.info("Creating user with transaction ID: {}", transactionId);
        
        if (userRepository.existsByUsername(user.getUsername())) {
            logger.warn("Username already exists: {} for transaction ID: {}", user.getUsername(), transactionId);
            throw new RuntimeException("Username already exists: " + user.getUsername());
        }
        
        // Set transaction ID on the user entity
        user.setTransactionId(transactionId);
        
        User savedUser = userRepository.save(user);
        logger.info("User created successfully with ID: {} and transaction ID: {}", savedUser.getId(), transactionId);
        return savedUser;
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        String transactionId = TransactionContext.getTransactionId();
        logger.info("Finding user by ID: {} with transaction ID: {}", id, transactionId);
        
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            logger.info("User found with ID: {} and transaction ID: {}", id, transactionId);
        } else {
            logger.warn("User not found with ID: {} for transaction ID: {}", id, transactionId);
        }
        return user;
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        String transactionId = TransactionContext.getTransactionId();
        logger.info("Finding user by username: {} with transaction ID: {}", username, transactionId);
        
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            logger.info("User found with username: {} and transaction ID: {}", username, transactionId);
        } else {
            logger.warn("User not found with username: {} for transaction ID: {}", username, transactionId);
        }
        return user;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<User> findAllUsers() {
        String transactionId = TransactionContext.getTransactionId();
        logger.info("Finding all users with transaction ID: {}", transactionId);
        
        List<User> users = userRepository.findAll();
        logger.info("Found {} users with transaction ID: {}", users.size(), transactionId);
        return users;
    }
    
    @Override
    public User updateUser(User user) {
        String transactionId = TransactionContext.getTransactionId();
        logger.info("Updating user with ID: {} and transaction ID: {}", user.getId(), transactionId);
        
        if (!userRepository.existsById(user.getId())) {
            logger.error("User not found with ID: {} for transaction ID: {}", user.getId(), transactionId);
            throw new RuntimeException("User not found with id: " + user.getId());
        }
        
        // Set transaction ID for the update operation
        user.setTransactionId(transactionId);
        
        User updatedUser = userRepository.save(user);
        logger.info("User updated successfully with ID: {} and transaction ID: {}", updatedUser.getId(), transactionId);
        return updatedUser;
    }
    
    @Override
    public void deleteUser(Long id) {
        String transactionId = TransactionContext.getTransactionId();
        logger.info("Deleting user with ID: {} and transaction ID: {}", id, transactionId);
        
        if (!userRepository.existsById(id)) {
            logger.error("User not found with ID: {} for transaction ID: {}", id, transactionId);
            throw new RuntimeException("User not found with id: " + id);
        }
        
        userRepository.deleteById(id);
        logger.info("User deleted successfully with ID: {} and transaction ID: {}", id, transactionId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        String transactionId = TransactionContext.getTransactionId();
        logger.debug("Checking if username exists: {} with transaction ID: {}", username, transactionId);
        
        boolean exists = userRepository.existsByUsername(username);
        logger.debug("Username '{}' exists: {} for transaction ID: {}", username, exists, transactionId);
        return exists;
    }
} 