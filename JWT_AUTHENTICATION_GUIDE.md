# JWT Authentication Guide

This guide explains the JWT (JSON Web Token) authentication system implemented in the e-commerce service, providing secure authentication and authorization for API endpoints.

## 🎯 Overview

The JWT authentication system provides:
- **Stateless Authentication**: No server-side session storage
- **Token-Based Security**: Secure access tokens with expiration
- **Refresh Token Support**: Long-lived refresh tokens for seamless authentication
- **Role-Based Access Control**: Integration with user roles
- **Transaction Tracking**: JWT tokens include transaction IDs for audit trails
- **Password Security**: BCrypt password hashing

## 🔧 How It Works

### **Authentication Flow**
1. **User Login**: Username/password authentication
2. **Token Generation**: JWT access token + refresh token
3. **Token Validation**: Automatic validation on each request
4. **Token Refresh**: Use refresh token to get new access token
5. **Role-Based Access**: Automatic authorization based on user roles

### **JWT Token Structure**
```json
{
  "sub": "username",
  "iat": 1640995200,
  "exp": 1641081600,
  "transactionId": "TXN-20250727-001",
  "authorities": ["ROLE_ADMIN", "ROLE_USER"]
}
```

## 📋 API Endpoints

### **Authentication Endpoints**

#### **User Login**
```bash
POST /api/auth/login
Content-Type: application/json

{
  "username": "john_doe",
  "password": "password123"
}
```

**Response:**
```json
{
  "accessToken": "eyJhbGciOiJIUzUxMiJ9...",
  "refreshToken": "eyJhbGciOiJIUzUxMiJ9...",
  "tokenType": "Bearer",
  "expiresIn": 86400000,
  "refreshExpiresIn": 604800000,
  "username": "john_doe",
  "roles": ["ADMIN", "USER"],
  "transactionId": "TXN-20250727-001"
}
```

#### **User Registration**
```bash
POST /api/auth/register
Content-Type: application/json

{
  "username": "new_user",
  "password": "password123"
}
```

#### **Token Refresh**
```bash
POST /api/auth/refresh
Content-Type: application/json

{
  "refreshToken": "eyJhbGciOiJIUzUxMiJ9..."
}
```

#### **Health Check**
```bash
GET /api/auth/health
```

## 🚀 Usage Examples

### **1. User Registration and Login**

```bash
# Register a new user
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -H "X-Transaction-ID: AUTH-001" \
  -d '{
    "username": "john_doe",
    "password": "password123"
  }'

# Login with the user
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -H "X-Transaction-ID: AUTH-002" \
  -d '{
    "username": "john_doe",
    "password": "password123"
  }'
```

### **2. Using JWT Token for API Access**

```bash
# Store the token from login response
TOKEN="eyJhbGciOiJIUzUxMiJ9..."

# Use token to access protected endpoints
curl -X GET http://localhost:8080/api/users \
  -H "Authorization: Bearer $TOKEN" \
  -H "X-Transaction-ID: API-001"

# Create a user with authentication
curl -X POST http://localhost:8080/api/users \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -H "X-Transaction-ID: API-002" \
  -d '{
    "username": "jane_doe",
    "active": true
  }'
```

### **3. Role-Based Access**

```bash
# Admin-only endpoint (requires ADMIN role)
curl -X GET http://localhost:8080/api/roles \
  -H "Authorization: Bearer $ADMIN_TOKEN"

# User endpoint (requires USER or ADMIN role)
curl -X GET http://localhost:8080/api/users \
  -H "Authorization: Bearer $USER_TOKEN"
```

### **4. Token Refresh**

```bash
# Refresh expired access token
curl -X POST http://localhost:8080/api/auth/refresh \
  -H "Content-Type: application/json" \
  -H "X-Transaction-ID: REFRESH-001" \
  -d '{
    "refreshToken": "eyJhbGciOiJIUzUxMiJ9..."
  }'
```

## 🔐 Security Configuration

### **Endpoint Security Rules**

```java
// Public endpoints (no authentication required)
.requestMatchers("/api/auth/**").permitAll()
.requestMatchers("/actuator/**").permitAll()
.requestMatchers("/error").permitAll()

// Role-based endpoints
.requestMatchers("/api/users/**").hasAnyRole("ADMIN", "USER")
.requestMatchers("/api/roles/**").hasRole("ADMIN")
.requestMatchers("/api/transactions/**").hasRole("ADMIN")

// All other requests require authentication
.anyRequest().authenticated()
```

### **JWT Configuration**

```yaml
jwt:
  secret: ${JWT_SECRET:your-super-secret-jwt-key-for-development-only-change-in-production}
  expiration: ${JWT_EXPIRATION:86400000} # 24 hours
  refresh-expiration: ${JWT_REFRESH_EXPIRATION:604800000} # 7 days
```

## 🛠️ Implementation Details

### **JWT Token Utility**

```java
@Component
public class JwtTokenUtil {
    // Generate JWT token
    public String generateToken(UserDetails userDetails)
    
    // Generate refresh token
    public String generateRefreshToken(UserDetails userDetails)
    
    // Validate token
    public Boolean validateToken(String token, UserDetails userDetails)
    
    // Extract username from token
    public String extractUsername(String token)
    
    // Extract transaction ID from token
    public String extractTransactionId(String token)
}
```

### **Authentication Filter**

```java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    // Intercepts all requests
    // Extracts JWT from Authorization header
    // Validates token and sets authentication context
}
```

### **User Details Service**

```java
@Service
public class CustomUserDetailsService implements UserDetailsService {
    // Loads user details from database
    // Converts user roles to Spring Security authorities
    // Handles inactive users
}
```

## 🔍 Token Management

### **Token Types**

1. **Access Token**: Short-lived (24 hours), used for API access
2. **Refresh Token**: Long-lived (7 days), used to get new access tokens

### **Token Claims**

- **sub**: Username (subject)
- **iat**: Issued at timestamp
- **exp**: Expiration timestamp
- **transactionId**: Transaction ID for audit trails
- **authorities**: User roles/permissions
- **type**: Token type (REFRESH for refresh tokens)

### **Token Validation**

```java
// Automatic validation on each request
if (jwtTokenUtil.validateToken(token, userDetails)) {
    // Set authentication context
    SecurityContextHolder.getContext().setAuthentication(authToken);
}
```

## 🔧 Best Practices

### **1. Token Security**
- Use strong JWT secrets in production
- Set appropriate token expiration times
- Store refresh tokens securely
- Implement token blacklisting if needed

### **2. Password Security**
- Use BCrypt password hashing
- Enforce strong password policies
- Implement password reset functionality

### **3. Role Management**
- Assign minimal required roles
- Use role hierarchy if needed
- Audit role assignments regularly

### **4. Error Handling**
- Don't expose sensitive information in error messages
- Log authentication failures for monitoring
- Implement rate limiting for auth endpoints

## 🎯 Common Use Cases

### **1. Frontend Integration**

```javascript
// Login
const loginResponse = await fetch('/api/auth/login', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({ username, password })
});

const { accessToken, refreshToken } = await loginResponse.json();

// Store tokens
localStorage.setItem('accessToken', accessToken);
localStorage.setItem('refreshToken', refreshToken);

// Use token for API calls
const apiResponse = await fetch('/api/users', {
  headers: { 'Authorization': `Bearer ${accessToken}` }
});
```

### **2. Token Refresh Logic**

```javascript
// Interceptor for automatic token refresh
axios.interceptors.response.use(
  response => response,
  async error => {
    if (error.response.status === 401) {
      const refreshToken = localStorage.getItem('refreshToken');
      const refreshResponse = await fetch('/api/auth/refresh', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ refreshToken })
      });
      
      if (refreshResponse.ok) {
        const { accessToken } = await refreshResponse.json();
        localStorage.setItem('accessToken', accessToken);
        // Retry original request
        return axios.request(error.config);
      }
    }
    return Promise.reject(error);
  }
);
```

### **3. Role-Based UI**

```javascript
// Check user roles
const hasRole = (role) => {
  const token = localStorage.getItem('accessToken');
  const payload = JSON.parse(atob(token.split('.')[1]));
  return payload.authorities.includes(`ROLE_${role}`);
};

// Conditional rendering
if (hasRole('ADMIN')) {
  showAdminPanel();
}
```

## 🔍 Monitoring and Debugging

### **Token Debugging**

```bash
# Decode JWT token (for debugging only)
echo "eyJhbGciOiJIUzUxMiJ9..." | cut -d. -f2 | base64 -d | jq

# Check token expiration
curl -X POST http://localhost:8080/api/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{"refreshToken": "your-refresh-token"}'
```

### **Security Logs**

```bash
# Monitor authentication logs
grep "Authentication" application.log

# Monitor JWT token operations
grep "JWT" application.log

# Monitor failed authentications
grep "Authentication failed" application.log
```

## 🚀 Production Considerations

### **1. Environment Variables**
```bash
# Set strong JWT secret
export JWT_SECRET="your-super-strong-secret-key-here"

# Configure token expiration
export JWT_EXPIRATION=3600000  # 1 hour
export JWT_REFRESH_EXPIRATION=2592000000  # 30 days
```

### **2. Security Headers**
```java
// Add security headers
http.headers(headers -> headers
    .frameOptions().deny()
    .contentTypeOptions()
    .httpStrictTransportSecurity(hsts -> hsts.maxAgeInSeconds(31536000))
);
```

### **3. Rate Limiting**
```java
// Implement rate limiting for auth endpoints
@RateLimiter(name = "auth")
@PostMapping("/login")
public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest authRequest) {
    // Implementation
}
```

The JWT authentication system is now fully integrated and ready for use! 🚀 