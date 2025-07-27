package com.iavtar.domain.context;

import com.iavtar.domain.util.TransactionIdGenerator;

/**
 * Thread-local context holder for transaction ID
 */
public class TransactionContext {
    
    private static final ThreadLocal<String> transactionIdHolder = new ThreadLocal<>();
    
    /**
     * Sets the transaction ID for the current thread
     * @param transactionId the transaction ID to set
     */
    public static void setTransactionId(String transactionId) {
        transactionIdHolder.set(transactionId);
    }
    
    /**
     * Gets the transaction ID for the current thread
     * @return the current transaction ID, or generates a new one if not set
     */
    public static String getTransactionId() {
        String transactionId = transactionIdHolder.get();
        if (transactionId == null) {
            transactionId = TransactionIdGenerator.generateTransactionId();
            setTransactionId(transactionId);
        }
        return transactionId;
    }
    
    /**
     * Clears the transaction ID for the current thread
     */
    public static void clear() {
        transactionIdHolder.remove();
    }
    
    /**
     * Checks if a transaction ID is set for the current thread
     * @return true if transaction ID is set, false otherwise
     */
    public static boolean hasTransactionId() {
        return transactionIdHolder.get() != null;
    }
} 