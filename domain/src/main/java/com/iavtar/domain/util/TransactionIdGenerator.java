package com.iavtar.domain.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Utility class for generating unique transaction IDs
 */
public class TransactionIdGenerator {
    
    private static final AtomicLong sequence = new AtomicLong(0);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    
    /**
     * Generates a unique transaction ID with format: TXN-YYYYMMDDHHMMSS-XXXXX
     * @return unique transaction ID
     */
    public static String generateTransactionId() {
        LocalDateTime now = LocalDateTime.now();
        String timestamp = now.format(formatter);
        long seq = sequence.incrementAndGet() % 100000;
        return String.format("TXN-%s-%05d", timestamp, seq);
    }
    
    /**
     * Generates a transaction ID with custom prefix
     * @param prefix custom prefix for the transaction ID
     * @return unique transaction ID with custom prefix
     */
    public static String generateTransactionId(String prefix) {
        LocalDateTime now = LocalDateTime.now();
        String timestamp = now.format(formatter);
        long seq = sequence.incrementAndGet() % 100000;
        return String.format("%s-%s-%05d", prefix, timestamp, seq);
    }
    
    /**
     * Validates if a string is a valid transaction ID format
     * @param transactionId the transaction ID to validate
     * @return true if valid format, false otherwise
     */
    public static boolean isValidTransactionId(String transactionId) {
        if (transactionId == null || transactionId.trim().isEmpty()) {
            return false;
        }
        // Basic format validation: PREFIX-YYYYMMDDHHMMSS-XXXXX
        return transactionId.matches("^[A-Z]+-\\d{14}-\\d{5}$");
    }
} 