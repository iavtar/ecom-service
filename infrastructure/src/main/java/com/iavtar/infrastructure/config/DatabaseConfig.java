package com.iavtar.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackages = "com.iavtar.infrastructure.repository")
@EnableTransactionManagement
public class DatabaseConfig {
    // Database configuration is handled by Spring Boot auto-configuration
    // This class enables JPA repositories and transaction management
} 