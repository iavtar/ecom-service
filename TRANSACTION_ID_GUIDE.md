# Transaction ID Guide

This guide explains how to use transaction IDs in the e-commerce service for request tracking, logging, and audit purposes.

## 🎯 What is a Transaction ID?

A **Transaction ID** is a unique identifier that tracks a request throughout its lifecycle across your application. It helps with:

- **Request Tracing**: Follow a request through all services
- **Logging**: Correlate log messages for a specific request
- **Debugging**: Easily identify all operations related to a request
- **Audit Trail**: Track who did what and when
- **Performance Monitoring**: Measure request processing times

## 🔧 How It Works

### **Transaction ID Format**
```
TXN-YYYYMMDDHHMMSS-XXXXX
```
- **TXN**: Fixed prefix
- **YYYYMMDDHHMMSS**: Timestamp (14 digits)
- **XXXXX**: Sequential number (5 digits)

**Example**: `TXN-20250727140945-00001`

### **Automatic Generation**
1. **HTTP Request** comes in
2. **Interceptor** checks for `X-Transaction-ID` header
3. **If not found**: Generates new transaction ID
4. **If found**: Uses provided transaction ID
5. **Context** stores transaction ID for the request lifecycle
6. **Logging** includes transaction ID in all log messages
7. **Response** includes transaction ID in headers

## 📋 Usage Examples

### **1. Making API Requests**

#### **Without Transaction ID (Auto-generated)**
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"username": "john_doe", "active": true}'
```

#### **With Custom Transaction ID**
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -H "X-Transaction-ID: CUSTOM-20250727-001" \
  -d '{"username": "john_doe", "active": true}'
```

#### **Using Correlation ID (Alternative)**
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -H "X-Correlation-ID: CORR-12345" \
  -d '{"username": "john_doe", "active": true}'
```

### **2. Response Headers**
The response will include the transaction ID:
```http
HTTP/1.1 200 OK
X-Transaction-ID: TXN-20250727140945-00001
Content-Type: application/json
```

### **3. Log Output**
All log messages will include the transaction ID:
```
[ecom-service] [TXN-20250727140945-00001] 2025-07-27 14:09:45 [http-nio-8080-exec-1] INFO  c.i.s.i.UserServiceImpl - Creating user with transaction ID: TXN-20250727140945-00001
[ecom-service] [TXN-20250727140945-00001] 2025-07-27 14:09:45 [http-nio-8080-exec-1] INFO  c.i.s.i.UserServiceImpl - User created successfully with ID: 1 and transaction ID: TXN-20250727140945-00001
```

## 🛠️ Implementation Details

### **Components**

1. **TransactionIdGenerator**: Generates unique transaction IDs
2. **TransactionContext**: Thread-local storage for transaction ID
3. **TransactionIdInterceptor**: HTTP interceptor for header processing
4. **TransactionIdMDCFilter**: Sets transaction ID in logging context
5. **WebConfig**: Registers the interceptor

### **Flow Diagram**
```
HTTP Request
    ↓
TransactionIdInterceptor
    ↓
Check Headers (X-Transaction-ID, X-Correlation-ID)
    ↓
Generate or Use Transaction ID
    ↓
Set in TransactionContext
    ↓
Set in MDC for Logging
    ↓
Process Request
    ↓
Add Transaction ID to Response Headers
    ↓
Clear Context
```

## 🔍 Monitoring and Debugging

### **Finding Logs for a Transaction**
```bash
# Search logs for a specific transaction ID
grep "TXN-20250727140945-00001" application.log

# Search for all transactions in a time period
grep "TXN-20250727" application.log
```

### **Database Queries**
```sql
-- Find all operations for a transaction ID
SELECT * FROM user WHERE transaction_id = 'TXN-20250727140945-00001';

-- Find recent transactions
SELECT transaction_id, username, created_at 
FROM user 
WHERE transaction_id LIKE 'TXN-20250727%'
ORDER BY created_at DESC;
```

## 🚀 Advanced Usage

### **Custom Transaction ID Prefixes**
```java
// Generate with custom prefix
String transactionId = TransactionIdGenerator.generateTransactionId("ORDER");
// Result: ORDER-20250727140945-00001
```

### **Validation**
```java
// Validate transaction ID format
boolean isValid = TransactionIdGenerator.isValidTransactionId("TXN-20250727140945-00001");
```

### **Context Access**
```java
// Get current transaction ID
String transactionId = TransactionContext.getTransactionId();

// Check if transaction ID is set
boolean hasTransactionId = TransactionContext.hasTransactionId();
```

## 🔧 Configuration

### **Logging Pattern**
The logging pattern includes transaction ID:
```yaml
logging:
  pattern:
    console: "[ecom-service] [%X{transactionId}] %d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
```

### **Interceptor Configuration**
```java
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(transactionIdInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/actuator/**", "/error");
    }
}
```

## 🎯 Best Practices

### **1. Always Include Transaction ID**
- Include `X-Transaction-ID` in all API calls
- Use consistent transaction ID across microservices
- Pass transaction ID in async operations

### **2. Logging**
- Include transaction ID in all log messages
- Use structured logging for better parsing
- Log transaction ID at start and end of operations

### **3. Error Handling**
- Include transaction ID in error responses
- Log transaction ID with error details
- Maintain transaction ID across retries

### **4. Performance**
- Transaction ID generation is lightweight
- Minimal overhead on request processing
- Automatic cleanup prevents memory leaks

## 🔍 Troubleshooting

### **Common Issues**

1. **Transaction ID Not in Logs**
   - Check if MDC filter is properly configured
   - Verify logging pattern includes `%X{transactionId}`

2. **Transaction ID Not Persisted**
   - Ensure entity has `transactionId` field
   - Check if service sets transaction ID before saving

3. **Transaction ID Not in Response Headers**
   - Verify interceptor is registered
   - Check if response headers are being set

### **Debug Commands**
```bash
# Check if transaction ID is being generated
curl -v http://localhost:8080/api/users

# Verify transaction ID in database
mysql -u root -p -e "SELECT transaction_id, username FROM ecom_service_dev.user;"
```

## 📚 Integration with Monitoring Tools

### **ELK Stack**
```json
{
  "transaction_id": "TXN-20250727140945-00001",
  "timestamp": "2025-07-27T14:09:45",
  "service": "ecom-service",
  "operation": "createUser"
}
```

### **Jaeger/Zipkin**
Transaction ID can be used as trace ID for distributed tracing.

### **Prometheus/Grafana**
Track metrics by transaction ID for performance analysis. 