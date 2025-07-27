package com.iavtar.service;

import com.iavtar.domain.entity.Role;
import com.iavtar.domain.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface RoleService {
    
    /**
     * Create a new role
     */
    Role createRole(Role role);
    
    /**
     * Find role by ID
     */
    Optional<Role> findById(Long id);
    
    /**
     * Find role by name
     */
    Optional<Role> findByName(String name);
    
    /**
     * Find all roles
     */
    List<Role> findAllRoles();
    
    /**
     * Find all active roles
     */
    List<Role> findActiveRoles();
    
    /**
     * Update role
     */
    Role updateRole(Role role);
    
    /**
     * Delete role
     */
    void deleteRole(Long id);
    
    /**
     * Check if role exists by name
     */
    boolean existsByName(String name);
    
    /**
     * Find roles by names
     */
    List<Role> findByNames(Set<String> names);
    
    /**
     * Assign roles to user
     */
    User assignRolesToUser(Long userId, Set<String> roleNames);
    
    /**
     * Remove roles from user
     */
    User removeRolesFromUser(Long userId, Set<String> roleNames);
    
    /**
     * Get user roles
     */
    List<Role> getUserRoles(Long userId);
    
    /**
     * Get user role names
     */
    Set<String> getUserRoleNames(Long userId);
    
    /**
     * Check if user has role
     */
    boolean userHasRole(Long userId, String roleName);
    
    /**
     * Find users by role name
     */
    List<User> findUsersByRoleName(String roleName);
    
    /**
     * Count users by role name
     */
    long countUsersByRoleName(String roleName);
} 