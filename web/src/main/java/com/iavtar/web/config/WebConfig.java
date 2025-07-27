package com.iavtar.web.config;

import com.iavtar.web.interceptor.TransactionIdInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web configuration for interceptors and other web-related settings
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Autowired
    private TransactionIdInterceptor transactionIdInterceptor;
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(transactionIdInterceptor)
                .addPathPatterns("/**")  // Apply to all paths
                .excludePathPatterns("/actuator/**", "/error"); // Exclude health checks and error pages
    }
} 