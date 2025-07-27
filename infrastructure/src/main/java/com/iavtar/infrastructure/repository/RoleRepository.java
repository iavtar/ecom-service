package com.iavtar.infrastructure.repository;

import com.iavtar.domain.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    
    /**
     * Find role by name
     */
    Optional<Role> findByName(String name);
    
    /**
     * Check if role exists by name
     */
    boolean existsByName(String name);
    
    /**
     * Find all active roles
     */
    List<Role> findByActiveTrue();
    
    /**
     * Find roles by names
     */
    List<Role> findByNameIn(Set<String> names);
    
    /**
     * Find roles by active status
     */
    List<Role> findByActive(boolean active);
    
    /**
     * Find roles by transaction ID
     */
    List<Role> findByTransactionId(String transactionId);
    
    /**
     * Find roles by user ID (using join query)
     */
    @Query("SELECT r FROM Role r JOIN r.users u WHERE u.id = :userId AND r.active = true")
    List<Role> findActiveRolesByUserId(@Param("userId") Long userId);
    
    /**
     * Find role names by user ID
     */
    @Query("SELECT r.name FROM Role r JOIN r.users u WHERE u.id = :userId AND r.active = true")
    Set<String> findRoleNamesByUserId(@Param("userId") Long userId);
    
    /**
     * Count users by role name
     */
    @Query("SELECT COUNT(u) FROM User u JOIN u.roles r WHERE r.name = :roleName AND r.active = true")
    long countUsersByRoleName(@Param("roleName") String roleName);
    
    /**
     * Find roles created in a date range
     */
    @Query("SELECT r FROM Role r WHERE r.createdAt BETWEEN :startDate AND :endDate")
    List<Role> findByCreatedAtBetween(@Param("startDate") java.time.LocalDateTime startDate, 
                                     @Param("endDate") java.time.LocalDateTime endDate);
} 