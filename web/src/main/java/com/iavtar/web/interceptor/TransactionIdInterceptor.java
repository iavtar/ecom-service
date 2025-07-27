package com.iavtar.web.interceptor;

import com.iavtar.domain.context.TransactionContext;
import com.iavtar.domain.util.TransactionIdGenerator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Interceptor to handle transaction IDs from HTTP requests
 */
@Component
public class TransactionIdInterceptor implements HandlerInterceptor {
    
    private static final Logger logger = LoggerFactory.getLogger(TransactionIdInterceptor.class);
    
    public static final String TRANSACTION_ID_HEADER = "X-Transaction-ID";
    public static final String CORRELATION_ID_HEADER = "X-Correlation-ID";
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // Check for transaction ID in headers
        String transactionId = request.getHeader(TRANSACTION_ID_HEADER);
        
        // If not found, check for correlation ID (common in microservices)
        if (transactionId == null || transactionId.trim().isEmpty()) {
            transactionId = request.getHeader(CORRELATION_ID_HEADER);
        }
        
        // If still not found, generate a new one
        if (transactionId == null || transactionId.trim().isEmpty()) {
            transactionId = TransactionIdGenerator.generateTransactionId();
            logger.debug("Generated new transaction ID: {}", transactionId);
        } else {
            logger.debug("Using provided transaction ID: {}", transactionId);
        }
        
        // Set the transaction ID in context
        TransactionContext.setTransactionId(transactionId);
        
        // Add transaction ID to response headers for client tracking
        response.addHeader(TRANSACTION_ID_HEADER, transactionId);
        
        return true;
    }
    
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // Clean up the transaction context
        TransactionContext.clear();
    }
} 