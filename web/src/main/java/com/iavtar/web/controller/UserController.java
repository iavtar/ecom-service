package com.iavtar.web.controller;

import com.iavtar.domain.context.TransactionContext;
import com.iavtar.domain.entity.User;
import com.iavtar.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        String transactionId = TransactionContext.getTransactionId();
        logger.info("Received create user request with transaction ID: {}", transactionId);
        
        User createdUser = userService.createUser(user);
        logger.info("User created successfully with ID: {} and transaction ID: {}", createdUser.getId(), transactionId);
        
        return ResponseEntity.ok(createdUser);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        String transactionId = TransactionContext.getTransactionId();
        logger.info("Received get user by ID request: {} with transaction ID: {}", id, transactionId);
        
        return userService.findById(id)
                .map(user -> {
                    logger.info("User found with ID: {} and transaction ID: {}", id, transactionId);
                    return ResponseEntity.ok(user);
                })
                .orElseGet(() -> {
                    logger.warn("User not found with ID: {} for transaction ID: {}", id, transactionId);
                    return ResponseEntity.notFound().build();
                });
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        String transactionId = TransactionContext.getTransactionId();
        logger.info("Received get user by username request: {} with transaction ID: {}", username, transactionId);
        
        return userService.findByUsername(username)
                .map(user -> {
                    logger.info("User found with username: {} and transaction ID: {}", username, transactionId);
                    return ResponseEntity.ok(user);
                })
                .orElseGet(() -> {
                    logger.warn("User not found with username: {} for transaction ID: {}", username, transactionId);
                    return ResponseEntity.notFound().build();
                });
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        String transactionId = TransactionContext.getTransactionId();
        logger.info("Received get all users request with transaction ID: {}", transactionId);
        
        List<User> users = userService.findAllUsers();
        logger.info("Retrieved {} users with transaction ID: {}", users.size(), transactionId);
        
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        String transactionId = TransactionContext.getTransactionId();
        logger.info("Received update user request for ID: {} with transaction ID: {}", id, transactionId);
        
        user.setId(id);
        User updatedUser = userService.updateUser(user);
        logger.info("User updated successfully with ID: {} and transaction ID: {}", updatedUser.getId(), transactionId);
        
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        String transactionId = TransactionContext.getTransactionId();
        logger.info("Received delete user request for ID: {} with transaction ID: {}", id, transactionId);
        
        userService.deleteUser(id);
        logger.info("User deleted successfully with ID: {} and transaction ID: {}", id, transactionId);
        
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/check-username/{username}")
    public ResponseEntity<Boolean> checkUsernameExists(@PathVariable String username) {
        String transactionId = TransactionContext.getTransactionId();
        logger.info("Received check username request: {} with transaction ID: {}", username, transactionId);
        
        boolean exists = userService.existsByUsername(username);
        logger.info("Username '{}' exists: {} for transaction ID: {}", username, exists, transactionId);
        
        return ResponseEntity.ok(exists);
    }
} 