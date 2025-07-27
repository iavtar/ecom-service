package com.iavtar.service.impl;

import com.iavtar.domain.context.TransactionContext;
import com.iavtar.domain.entity.Role;
import com.iavtar.domain.entity.User;
import com.iavtar.infrastructure.repository.RoleRepository;
import com.iavtar.infrastructure.repository.UserRepository;
import com.iavtar.service.RoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class RoleServiceImpl implements RoleService {
    
    private static final Logger logger = LoggerFactory.getLogger(RoleServiceImpl.class);
    
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    
    @Autowired
    public RoleServiceImpl(RoleRepository roleRepository, UserRepository userRepository) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }
    
    @Override
    public Role createRole(Role role) {
        String transactionId = TransactionContext.getTransactionId();
        logger.info("Creating role with name: {} and transaction ID: {}", role.getName(), transactionId);
        
        if (roleRepository.existsByName(role.getName())) {
            logger.warn("Role already exists with name: {} for transaction ID: {}", role.getName(), transactionId);
            throw new RuntimeException("Role already exists with name: " + role.getName());
        }
        
        role.setTransactionId(transactionId);
        Role savedRole = roleRepository.save(role);
        logger.info("Role created successfully with ID: {} and transaction ID: {}", savedRole.getId(), transactionId);
        return savedRole;
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Role> findById(Long id) {
        String transactionId = TransactionContext.getTransactionId();
        logger.info("Finding role by ID: {} with transaction ID: {}", id, transactionId);
        
        Optional<Role> role = roleRepository.findById(id);
        if (role.isPresent()) {
            logger.info("Role found with ID: {} and transaction ID: {}", id, transactionId);
        } else {
            logger.warn("Role not found with ID: {} for transaction ID: {}", id, transactionId);
        }
        return role;
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Role> findByName(String name) {
        String transactionId = TransactionContext.getTransactionId();
        logger.info("Finding role by name: {} with transaction ID: {}", name, transactionId);
        
        Optional<Role> role = roleRepository.findByName(name);
        if (role.isPresent()) {
            logger.info("Role found with name: {} and transaction ID: {}", name, transactionId);
        } else {
            logger.warn("Role not found with name: {} for transaction ID: {}", name, transactionId);
        }
        return role;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Role> findAllRoles() {
        String transactionId = TransactionContext.getTransactionId();
        logger.info("Finding all roles with transaction ID: {}", transactionId);
        
        List<Role> roles = roleRepository.findAll();
        logger.info("Found {} roles with transaction ID: {}", roles.size(), transactionId);
        return roles;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Role> findActiveRoles() {
        String transactionId = TransactionContext.getTransactionId();
        logger.info("Finding active roles with transaction ID: {}", transactionId);
        
        List<Role> roles = roleRepository.findByActiveTrue();
        logger.info("Found {} active roles with transaction ID: {}", roles.size(), transactionId);
        return roles;
    }
    
    @Override
    public Role updateRole(Role role) {
        String transactionId = TransactionContext.getTransactionId();
        logger.info("Updating role with ID: {} and transaction ID: {}", role.getId(), transactionId);
        
        if (!roleRepository.existsById(role.getId())) {
            logger.error("Role not found with ID: {} for transaction ID: {}", role.getId(), transactionId);
            throw new RuntimeException("Role not found with id: " + role.getId());
        }
        
        role.setTransactionId(transactionId);
        Role updatedRole = roleRepository.save(role);
        logger.info("Role updated successfully with ID: {} and transaction ID: {}", updatedRole.getId(), transactionId);
        return updatedRole;
    }
    
    @Override
    public void deleteRole(Long id) {
        String transactionId = TransactionContext.getTransactionId();
        logger.info("Deleting role with ID: {} and transaction ID: {}", id, transactionId);
        
        if (!roleRepository.existsById(id)) {
            logger.error("Role not found with ID: {} for transaction ID: {}", id, transactionId);
            throw new RuntimeException("Role not found with id: " + id);
        }
        
        roleRepository.deleteById(id);
        logger.info("Role deleted successfully with ID: {} and transaction ID: {}", id, transactionId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        String transactionId = TransactionContext.getTransactionId();
        logger.debug("Checking if role exists by name: {} with transaction ID: {}", name, transactionId);
        
        boolean exists = roleRepository.existsByName(name);
        logger.debug("Role '{}' exists: {} for transaction ID: {}", name, exists, transactionId);
        return exists;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Role> findByNames(Set<String> names) {
        String transactionId = TransactionContext.getTransactionId();
        logger.info("Finding roles by names: {} with transaction ID: {}", names, transactionId);
        
        List<Role> roles = roleRepository.findByNameIn(names);
        logger.info("Found {} roles for names: {} with transaction ID: {}", roles.size(), names, transactionId);
        return roles;
    }
    
    @Override
    public User assignRolesToUser(Long userId, Set<String> roleNames) {
        String transactionId = TransactionContext.getTransactionId();
        logger.info("Assigning roles: {} to user ID: {} with transaction ID: {}", roleNames, userId, transactionId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        List<Role> roles = roleRepository.findByNameIn(roleNames);
        Set<String> foundRoleNames = roles.stream().map(Role::getName).collect(Collectors.toSet());
        
        // Check for missing roles
        Set<String> missingRoles = roleNames.stream()
                .filter(name -> !foundRoleNames.contains(name))
                .collect(Collectors.toSet());
        
        if (!missingRoles.isEmpty()) {
            logger.warn("Some roles not found: {} for transaction ID: {}", missingRoles, transactionId);
            throw new RuntimeException("Roles not found: " + missingRoles);
        }
        
        // Assign roles to user
        roles.forEach(user::addRole);
        user.setTransactionId(transactionId);
        
        User savedUser = userRepository.save(user);
        logger.info("Roles assigned successfully to user ID: {} with transaction ID: {}", userId, transactionId);
        return savedUser;
    }
    
    @Override
    public User removeRolesFromUser(Long userId, Set<String> roleNames) {
        String transactionId = TransactionContext.getTransactionId();
        logger.info("Removing roles: {} from user ID: {} with transaction ID: {}", roleNames, userId, transactionId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        List<Role> roles = roleRepository.findByNameIn(roleNames);
        
        // Remove roles from user
        roles.forEach(user::removeRole);
        user.setTransactionId(transactionId);
        
        User savedUser = userRepository.save(user);
        logger.info("Roles removed successfully from user ID: {} with transaction ID: {}", userId, transactionId);
        return savedUser;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Role> getUserRoles(Long userId) {
        String transactionId = TransactionContext.getTransactionId();
        logger.info("Getting roles for user ID: {} with transaction ID: {}", userId, transactionId);
        
        List<Role> roles = roleRepository.findActiveRolesByUserId(userId);
        logger.info("Found {} roles for user ID: {} with transaction ID: {}", roles.size(), userId, transactionId);
        return roles;
    }
    
    @Override
    @Transactional(readOnly = true)
    public Set<String> getUserRoleNames(Long userId) {
        String transactionId = TransactionContext.getTransactionId();
        logger.info("Getting role names for user ID: {} with transaction ID: {}", userId, transactionId);
        
        Set<String> roleNames = roleRepository.findRoleNamesByUserId(userId);
        logger.info("Found {} role names for user ID: {} with transaction ID: {}", roleNames.size(), userId, transactionId);
        return roleNames;
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean userHasRole(Long userId, String roleName) {
        String transactionId = TransactionContext.getTransactionId();
        logger.debug("Checking if user ID: {} has role: {} with transaction ID: {}", userId, roleName, transactionId);
        
        boolean hasRole = userRepository.hasRole(userId, roleName);
        logger.debug("User ID: {} has role '{}': {} for transaction ID: {}", userId, roleName, hasRole, transactionId);
        return hasRole;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<User> findUsersByRoleName(String roleName) {
        String transactionId = TransactionContext.getTransactionId();
        logger.info("Finding users by role name: {} with transaction ID: {}", roleName, transactionId);
        
        List<User> users = userRepository.findByRoleName(roleName);
        logger.info("Found {} users with role: {} and transaction ID: {}", users.size(), roleName, transactionId);
        return users;
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countUsersByRoleName(String roleName) {
        String transactionId = TransactionContext.getTransactionId();
        logger.info("Counting users by role name: {} with transaction ID: {}", roleName, transactionId);
        
        long count = roleRepository.countUsersByRoleName(roleName);
        logger.info("Found {} users with role: {} and transaction ID: {}", count, roleName, transactionId);
        return count;
    }
} 