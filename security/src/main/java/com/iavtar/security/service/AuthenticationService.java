package com.iavtar.security.service;

import com.iavtar.domain.context.TransactionContext;
import com.iavtar.domain.entity.User;
import com.iavtar.infrastructure.repository.UserRepository;
import com.iavtar.security.dto.AuthRequest;
import com.iavtar.security.dto.AuthResponse;
import com.iavtar.security.jwt.JwtTokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthenticationService {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    /**
     * Authenticate user and generate JWT tokens
     */
    public AuthResponse authenticate(AuthRequest authRequest) {
        String transactionId = TransactionContext.getTransactionId();
        logger.info("Authenticating user: {} with transaction ID: {}", authRequest.getUsername(), transactionId);
        
        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
            );
            
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            
            // Generate tokens
            String accessToken = jwtTokenUtil.generateToken(userDetails);
            String refreshToken = jwtTokenUtil.generateRefreshToken(userDetails);
            
            // Get user roles
            Set<String> roles = userDetails.getAuthorities().stream()
                    .map(authority -> authority.getAuthority().replace("ROLE_", ""))
                    .collect(Collectors.toSet());
            
            AuthResponse response = new AuthResponse(
                    accessToken,
                    refreshToken,
                    jwtTokenUtil.getExpirationTime(),
                    jwtTokenUtil.getRefreshExpirationTime(),
                    userDetails.getUsername(),
                    roles,
                    transactionId
            );
            
            logger.info("Authentication successful for user: {} with transaction ID: {}", 
                       userDetails.getUsername(), transactionId);
            
            return response;
            
        } catch (BadCredentialsException e) {
            logger.warn("Authentication failed for user: {} with transaction ID: {}", 
                       authRequest.getUsername(), transactionId);
            throw new RuntimeException("Invalid username or password");
        } catch (Exception e) {
            logger.error("Authentication error for user: {} with transaction ID: {} - Error: {}", 
                        authRequest.getUsername(), transactionId, e.getMessage());
            throw new RuntimeException("Authentication failed: " + e.getMessage());
        }
    }
    
    /**
     * Refresh JWT token
     */
    public AuthResponse refreshToken(String refreshToken) {
        String transactionId = TransactionContext.getTransactionId();
        logger.info("Refreshing token with transaction ID: {}", transactionId);
        
        try {
            // Validate refresh token
            if (!jwtTokenUtil.isRefreshToken(refreshToken)) {
                logger.warn("Invalid refresh token provided with transaction ID: {}", transactionId);
                throw new RuntimeException("Invalid refresh token");
            }
            
            String username = jwtTokenUtil.extractUsername(refreshToken);
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found: " + username));
            
            if (!user.isActive()) {
                logger.warn("Inactive user attempted to refresh token: {} with transaction ID: {}", username, transactionId);
                throw new RuntimeException("User is inactive");
            }
            
            // Create UserDetails for token generation
            UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                    .username(user.getUsername())
                    .password(user.getPassword() != null ? user.getPassword() : "")
                    .authorities(user.getRoles().stream()
                            .filter(role -> role.isActive())
                            .map(role -> new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + role.getName()))
                            .collect(Collectors.toList()))
                    .build();
            
            // Generate new tokens
            String newAccessToken = jwtTokenUtil.generateToken(userDetails);
            String newRefreshToken = jwtTokenUtil.generateRefreshToken(userDetails);
            
            // Get user roles
            Set<String> roles = user.getRoles().stream()
                    .filter(role -> role.isActive())
                    .map(role -> role.getName())
                    .collect(Collectors.toSet());
            
            AuthResponse response = new AuthResponse(
                    newAccessToken,
                    newRefreshToken,
                    jwtTokenUtil.getExpirationTime(),
                    jwtTokenUtil.getRefreshExpirationTime(),
                    user.getUsername(),
                    roles,
                    transactionId
            );
            
            logger.info("Token refresh successful for user: {} with transaction ID: {}", username, transactionId);
            
            return response;
            
        } catch (Exception e) {
            logger.error("Token refresh failed with transaction ID: {} - Error: {}", transactionId, e.getMessage());
            throw new RuntimeException("Token refresh failed: " + e.getMessage());
        }
    }
    
    /**
     * Register new user
     */
    public AuthResponse register(AuthRequest authRequest) {
        String transactionId = TransactionContext.getTransactionId();
        logger.info("Registering new user: {} with transaction ID: {}", authRequest.getUsername(), transactionId);
        
        try {
            // Check if user already exists
            if (userRepository.existsByUsername(authRequest.getUsername())) {
                logger.warn("User already exists: {} with transaction ID: {}", authRequest.getUsername(), transactionId);
                throw new RuntimeException("Username already exists");
            }
            
            // Create new user
            User user = new User();
            user.setUsername(authRequest.getUsername());
            user.setPassword(passwordEncoder.encode(authRequest.getPassword()));
            user.setActive(true);
            user.setTransactionId(transactionId);
            
            User savedUser = userRepository.save(user);
            
            // Create UserDetails for token generation
            UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                    .username(savedUser.getUsername())
                    .password(savedUser.getPassword())
                    .authorities(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_USER"))
                    .build();
            
            // Generate tokens
            String accessToken = jwtTokenUtil.generateToken(userDetails);
            String refreshToken = jwtTokenUtil.generateRefreshToken(userDetails);
            
            AuthResponse response = new AuthResponse(
                    accessToken,
                    refreshToken,
                    jwtTokenUtil.getExpirationTime(),
                    jwtTokenUtil.getRefreshExpirationTime(),
                    savedUser.getUsername(),
                    Set.of("USER"),
                    transactionId
            );
            
            logger.info("User registration successful: {} with transaction ID: {}", savedUser.getUsername(), transactionId);
            
            return response;
            
        } catch (Exception e) {
            logger.error("User registration failed for username: {} with transaction ID: {} - Error: {}", 
                        authRequest.getUsername(), transactionId, e.getMessage());
            throw new RuntimeException("Registration failed: " + e.getMessage());
        }
    }
} 