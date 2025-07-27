package com.iavtar.security.jwt;

import com.iavtar.domain.context.TransactionContext;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtTokenUtil {
    
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenUtil.class);
    
    @Value("${jwt.secret:defaultSecretKeyForDevelopmentOnly}")
    private String secret;
    
    @Value("${jwt.expiration:86400000}") // 24 hours in milliseconds
    private Long expiration;
    
    @Value("${jwt.refresh-expiration:604800000}") // 7 days in milliseconds
    private Long refreshExpiration;
    
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }
    
    /**
     * Generate JWT token for user
     */
    public String generateToken(UserDetails userDetails) {
        String transactionId = TransactionContext.getTransactionId();
        logger.info("Generating JWT token for user: {} with transaction ID: {}", userDetails.getUsername(), transactionId);
        
        Map<String, Object> claims = new HashMap<>();
        claims.put("transactionId", transactionId);
        claims.put("authorities", userDetails.getAuthorities());
        
        return createToken(claims, userDetails.getUsername(), expiration);
    }
    
    /**
     * Generate refresh token
     */
    public String generateRefreshToken(UserDetails userDetails) {
        String transactionId = TransactionContext.getTransactionId();
        logger.info("Generating refresh token for user: {} with transaction ID: {}", userDetails.getUsername(), transactionId);
        
        Map<String, Object> claims = new HashMap<>();
        claims.put("transactionId", transactionId);
        claims.put("type", "REFRESH");
        
        return createToken(claims, userDetails.getUsername(), refreshExpiration);
    }
    
    /**
     * Create JWT token with claims
     */
    private String createToken(Map<String, Object> claims, String subject, long expiration) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);
        
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), Jwts.SIG.HS512)
                .compact();
    }
    
    /**
     * Extract username from token
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    
    /**
     * Extract expiration date from token
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    
    /**
     * Extract specific claim from token
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    
    /**
     * Extract all claims from token
     */
    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException e) {
            logger.error("Error extracting claims from token: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Check if token is expired
     */
    public Boolean isTokenExpired(String token) {
        try {
            Date expiration = extractExpiration(token);
            return expiration.before(new Date());
        } catch (JwtException e) {
            logger.error("Error checking token expiration: {}", e.getMessage());
            return true;
        }
    }
    
    /**
     * Validate token for user
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            boolean isValid = (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
            
            if (isValid) {
                logger.debug("Token validation successful for user: {}", username);
            } else {
                logger.warn("Token validation failed for user: {}", username);
            }
            
            return isValid;
        } catch (JwtException e) {
            logger.error("Error validating token: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Extract transaction ID from token
     */
    public String extractTransactionId(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return claims.get("transactionId", String.class);
        } catch (JwtException e) {
            logger.error("Error extracting transaction ID from token: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * Check if token is a refresh token
     */
    public Boolean isRefreshToken(String token) {
        try {
            Claims claims = extractAllClaims(token);
            String type = claims.get("type", String.class);
            return "REFRESH".equals(type);
        } catch (JwtException e) {
            logger.error("Error checking token type: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Get token expiration time in milliseconds
     */
    public long getExpirationTime() {
        return expiration;
    }
    
    /**
     * Get refresh token expiration time in milliseconds
     */
    public long getRefreshExpirationTime() {
        return refreshExpiration;
    }
} 