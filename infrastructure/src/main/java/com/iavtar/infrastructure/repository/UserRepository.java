package com.iavtar.infrastructure.repository;

import com.iavtar.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByUsername(String username);
    
    boolean existsByUsername(String username);
    
    /**
     * Find users by role name
     */
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName AND r.active = true")
    List<User> findByRoleName(@Param("roleName") String roleName);
    
    /**
     * Find users by role names (users having any of the specified roles)
     */
    @Query("SELECT DISTINCT u FROM User u JOIN u.roles r WHERE r.name IN :roleNames AND r.active = true")
    List<User> findByRoleNames(@Param("roleNames") Set<String> roleNames);
    
    /**
     * Find users by transaction ID
     */
    List<User> findByTransactionId(String transactionId);
    
    /**
     * Find active users
     */
    List<User> findByActiveTrue();
    
    /**
     * Find users by active status
     */
    List<User> findByActive(boolean active);
    
    /**
     * Check if user has a specific role
     */
    @Query("SELECT COUNT(u) > 0 FROM User u JOIN u.roles r WHERE u.id = :userId AND r.name = :roleName AND r.active = true")
    boolean hasRole(@Param("userId") Long userId, @Param("roleName") String roleName);
    
    /**
     * Find users created in a date range
     */
    @Query("SELECT u FROM User u WHERE u.createdAt BETWEEN :startDate AND :endDate")
    List<User> findByCreatedAtBetween(@Param("startDate") java.time.LocalDateTime startDate, 
                                     @Param("endDate") java.time.LocalDateTime endDate);
} 