package com.iavtar.web.controller;

import com.iavtar.domain.context.TransactionContext;
import com.iavtar.domain.entity.Role;
import com.iavtar.domain.entity.User;
import com.iavtar.service.RoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/roles")
public class RoleController {
    
    private static final Logger logger = LoggerFactory.getLogger(RoleController.class);
    
    private final RoleService roleService;
    
    @Autowired
    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }
    
    @PostMapping
    public ResponseEntity<Role> createRole(@RequestBody Role role) {
        String transactionId = TransactionContext.getTransactionId();
        logger.info("Received create role request with transaction ID: {}", transactionId);
        
        Role createdRole = roleService.createRole(role);
        logger.info("Role created successfully with ID: {} and transaction ID: {}", createdRole.getId(), transactionId);
        
        return ResponseEntity.ok(createdRole);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Role> getRoleById(@PathVariable Long id) {
        String transactionId = TransactionContext.getTransactionId();
        logger.info("Received get role by ID request: {} with transaction ID: {}", id, transactionId);
        
        return roleService.findById(id)
                .map(role -> {
                    logger.info("Role found with ID: {} and transaction ID: {}", id, transactionId);
                    return ResponseEntity.ok(role);
                })
                .orElseGet(() -> {
                    logger.warn("Role not found with ID: {} for transaction ID: {}", id, transactionId);
                    return ResponseEntity.notFound().build();
                });
    }
    
    @GetMapping("/name/{name}")
    public ResponseEntity<Role> getRoleByName(@PathVariable String name) {
        String transactionId = TransactionContext.getTransactionId();
        logger.info("Received get role by name request: {} with transaction ID: {}", name, transactionId);
        
        return roleService.findByName(name)
                .map(role -> {
                    logger.info("Role found with name: {} and transaction ID: {}", name, transactionId);
                    return ResponseEntity.ok(role);
                })
                .orElseGet(() -> {
                    logger.warn("Role not found with name: {} for transaction ID: {}", name, transactionId);
                    return ResponseEntity.notFound().build();
                });
    }
    
    @GetMapping
    public ResponseEntity<List<Role>> getAllRoles() {
        String transactionId = TransactionContext.getTransactionId();
        logger.info("Received get all roles request with transaction ID: {}", transactionId);
        
        List<Role> roles = roleService.findAllRoles();
        logger.info("Retrieved {} roles with transaction ID: {}", roles.size(), transactionId);
        
        return ResponseEntity.ok(roles);
    }
    
    @GetMapping("/active")
    public ResponseEntity<List<Role>> getActiveRoles() {
        String transactionId = TransactionContext.getTransactionId();
        logger.info("Received get active roles request with transaction ID: {}", transactionId);
        
        List<Role> roles = roleService.findActiveRoles();
        logger.info("Retrieved {} active roles with transaction ID: {}", roles.size(), transactionId);
        
        return ResponseEntity.ok(roles);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Role> updateRole(@PathVariable Long id, @RequestBody Role role) {
        String transactionId = TransactionContext.getTransactionId();
        logger.info("Received update role request for ID: {} with transaction ID: {}", id, transactionId);
        
        role.setId(id);
        Role updatedRole = roleService.updateRole(role);
        logger.info("Role updated successfully with ID: {} and transaction ID: {}", updatedRole.getId(), transactionId);
        
        return ResponseEntity.ok(updatedRole);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
        String transactionId = TransactionContext.getTransactionId();
        logger.info("Received delete role request for ID: {} with transaction ID: {}", id, transactionId);
        
        roleService.deleteRole(id);
        logger.info("Role deleted successfully with ID: {} and transaction ID: {}", id, transactionId);
        
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/check-name/{name}")
    public ResponseEntity<Boolean> checkRoleNameExists(@PathVariable String name) {
        String transactionId = TransactionContext.getTransactionId();
        logger.info("Received check role name request: {} with transaction ID: {}", name, transactionId);
        
        boolean exists = roleService.existsByName(name);
        logger.info("Role name '{}' exists: {} for transaction ID: {}", name, exists, transactionId);
        
        return ResponseEntity.ok(exists);
    }
    
    @PostMapping("/users/{userId}/assign")
    public ResponseEntity<User> assignRolesToUser(@PathVariable Long userId, @RequestBody Set<String> roleNames) {
        String transactionId = TransactionContext.getTransactionId();
        logger.info("Received assign roles request for user ID: {} with roles: {} and transaction ID: {}", 
                   userId, roleNames, transactionId);
        
        User updatedUser = roleService.assignRolesToUser(userId, roleNames);
        logger.info("Roles assigned successfully to user ID: {} with transaction ID: {}", userId, transactionId);
        
        return ResponseEntity.ok(updatedUser);
    }
    
    @PostMapping("/users/{userId}/remove")
    public ResponseEntity<User> removeRolesFromUser(@PathVariable Long userId, @RequestBody Set<String> roleNames) {
        String transactionId = TransactionContext.getTransactionId();
        logger.info("Received remove roles request for user ID: {} with roles: {} and transaction ID: {}", 
                   userId, roleNames, transactionId);
        
        User updatedUser = roleService.removeRolesFromUser(userId, roleNames);
        logger.info("Roles removed successfully from user ID: {} with transaction ID: {}", userId, transactionId);
        
        return ResponseEntity.ok(updatedUser);
    }
    
    @GetMapping("/users/{userId}")
    public ResponseEntity<List<Role>> getUserRoles(@PathVariable Long userId) {
        String transactionId = TransactionContext.getTransactionId();
        logger.info("Received get user roles request for user ID: {} with transaction ID: {}", userId, transactionId);
        
        List<Role> roles = roleService.getUserRoles(userId);
        logger.info("Retrieved {} roles for user ID: {} with transaction ID: {}", roles.size(), userId, transactionId);
        
        return ResponseEntity.ok(roles);
    }
    
    @GetMapping("/users/{userId}/names")
    public ResponseEntity<Set<String>> getUserRoleNames(@PathVariable Long userId) {
        String transactionId = TransactionContext.getTransactionId();
        logger.info("Received get user role names request for user ID: {} with transaction ID: {}", userId, transactionId);
        
        Set<String> roleNames = roleService.getUserRoleNames(userId);
        logger.info("Retrieved {} role names for user ID: {} with transaction ID: {}", roleNames.size(), userId, transactionId);
        
        return ResponseEntity.ok(roleNames);
    }
    
    @GetMapping("/users/{userId}/has-role/{roleName}")
    public ResponseEntity<Boolean> userHasRole(@PathVariable Long userId, @PathVariable String roleName) {
        String transactionId = TransactionContext.getTransactionId();
        logger.info("Received check user has role request for user ID: {} and role: {} with transaction ID: {}", 
                   userId, roleName, transactionId);
        
        boolean hasRole = roleService.userHasRole(userId, roleName);
        logger.info("User ID: {} has role '{}': {} for transaction ID: {}", userId, roleName, hasRole, transactionId);
        
        return ResponseEntity.ok(hasRole);
    }
    
    @GetMapping("/name/{roleName}/users")
    public ResponseEntity<List<User>> getUsersByRoleName(@PathVariable String roleName) {
        String transactionId = TransactionContext.getTransactionId();
        logger.info("Received get users by role name request: {} with transaction ID: {}", roleName, transactionId);
        
        List<User> users = roleService.findUsersByRoleName(roleName);
        logger.info("Retrieved {} users with role: {} and transaction ID: {}", users.size(), roleName, transactionId);
        
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/name/{roleName}/count")
    public ResponseEntity<Map<String, Object>> countUsersByRoleName(@PathVariable String roleName) {
        String transactionId = TransactionContext.getTransactionId();
        logger.info("Received count users by role name request: {} with transaction ID: {}", roleName, transactionId);
        
        long count = roleService.countUsersByRoleName(roleName);
        Map<String, Object> response = Map.of(
            "roleName", roleName,
            "userCount", count,
            "transactionId", transactionId
        );
        
        logger.info("Found {} users with role: {} and transaction ID: {}", count, roleName, transactionId);
        
        return ResponseEntity.ok(response);
    }
} 