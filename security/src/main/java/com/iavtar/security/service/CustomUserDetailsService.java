package com.iavtar.security.service;

import com.iavtar.domain.context.TransactionContext;
import com.iavtar.domain.entity.User;
import com.iavtar.infrastructure.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    
    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String transactionId = TransactionContext.getTransactionId();
        logger.info("Loading user details for username: {} with transaction ID: {}", username, transactionId);
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.warn("User not found with username: {} for transaction ID: {}", username, transactionId);
                    return new UsernameNotFoundException("User not found with username: " + username);
                });
        
        if (!user.isActive()) {
            logger.warn("Inactive user attempted to authenticate: {} with transaction ID: {}", username, transactionId);
            throw new UsernameNotFoundException("User is inactive: " + username);
        }
        
        // Convert user roles to Spring Security authorities
        var authorities = user.getRoles().stream()
                .filter(role -> role.isActive())
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
                .collect(Collectors.toList());
        
        // Add default USER role if no roles assigned
        if (authorities.isEmpty()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
            logger.debug("No roles found for user: {}, adding default USER role with transaction ID: {}", username, transactionId);
        }
        
        logger.info("User details loaded successfully for username: {} with {} roles and transaction ID: {}", 
                   username, authorities.size(), transactionId);
        
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword() != null ? user.getPassword() : "") // Handle null passwords
                .authorities(authorities)
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(!user.isActive())
                .build();
    }
} 