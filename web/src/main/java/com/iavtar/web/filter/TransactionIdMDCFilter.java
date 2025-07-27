package com.iavtar.web.filter;

import com.iavtar.domain.context.TransactionContext;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Filter to set transaction ID in MDC for logging
 */
@Component
@Order(1)
public class TransactionIdMDCFilter implements Filter {
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        
        try {
            // Get transaction ID from context (set by interceptor)
            String transactionId = TransactionContext.getTransactionId();
            
            // Set in MDC for logging
            MDC.put("transactionId", transactionId);
            
            // Continue with the filter chain
            chain.doFilter(request, response);
            
        } finally {
            // Clean up MDC
            MDC.remove("transactionId");
        }
    }
} 