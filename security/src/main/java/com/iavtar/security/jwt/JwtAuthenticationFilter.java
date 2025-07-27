package com.iavtar.security.jwt;

import com.iavtar.domain.context.TransactionContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    
    @Autowired
    private UserDetailsService userDetailsService;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        try {
            final String authHeader = request.getHeader("Authorization");
            String transactionId = TransactionContext.getTransactionId();
            
            String username = null;
            String jwt = null;
            
            // Extract JWT token from Authorization header
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                jwt = authHeader.substring(7);
                username = jwtTokenUtil.extractUsername(jwt);
                
                // Set transaction ID from token if available
                String tokenTransactionId = jwtTokenUtil.extractTransactionId(jwt);
                if (tokenTransactionId != null) {
                    TransactionContext.setTransactionId(tokenTransactionId);
                    transactionId = tokenTransactionId;
                }
                
                logger.debug("JWT token found for user: {} with transaction ID: {}", username, transactionId);
            }
            
            // Validate token and set authentication
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                
                if (jwtTokenUtil.validateToken(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    
                    logger.info("Authentication successful for user: {} with transaction ID: {}", username, transactionId);
                } else {
                    logger.warn("JWT token validation failed for user: {} with transaction ID: {}", username, transactionId);
                }
            }
            
        } catch (Exception e) {
            logger.error("Error processing JWT authentication: {} with transaction ID: {}", 
                        e.getMessage(), TransactionContext.getTransactionId());
        }
        
        filterChain.doFilter(request, response);
    }
} 