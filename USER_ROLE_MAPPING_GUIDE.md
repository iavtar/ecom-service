# User-Role Mapping Guide

This guide explains the one-to-many user to role mapping system in the e-commerce service, allowing users to have multiple roles for flexible access control.

## 🎯 Overview

The user-role mapping system provides:
- **Many-to-Many Relationship**: Users can have multiple roles, roles can be assigned to multiple users
- **Flexible Access Control**: Dynamic role assignment and removal
- **Role Management**: Create, update, delete, and manage roles
- **Transaction Tracking**: All operations include transaction IDs for audit trails
- **Active/Inactive Roles**: Soft deletion with active status management

## 🏗️ Database Schema

### **Tables Structure**
```sql
-- Users table
CREATE TABLE user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255),
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    transaction_id VARCHAR(255)
);

-- Roles table
CREATE TABLE roles (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) UNIQUE NOT NULL,
    description VARCHAR(500),
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    transaction_id VARCHAR(255)
);

-- Junction table for many-to-many relationship
CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (role_id) REFERENCES roles(id)
);
```

## 📋 API Endpoints

### **Role Management**

#### **Create Role**
```bash
POST /api/roles
Content-Type: application/json
X-Transaction-ID: ROLE-20250727-001

{
  "name": "ADMIN",
  "description": "Administrator with full access",
  "active": true
}
```

#### **Get Role by ID**
```bash
GET /api/roles/{id}
X-Transaction-ID: ROLE-20250727-001
```

#### **Get Role by Name**
```bash
GET /api/roles/name/{name}
X-Transaction-ID: ROLE-20250727-001
```

#### **Get All Roles**
```bash
GET /api/roles
X-Transaction-ID: ROLE-20250727-001
```

#### **Get Active Roles**
```bash
GET /api/roles/active
X-Transaction-ID: ROLE-20250727-001
```

#### **Update Role**
```bash
PUT /api/roles/{id}
Content-Type: application/json
X-Transaction-ID: ROLE-20250727-001

{
  "name": "ADMIN",
  "description": "Updated administrator description",
  "active": true
}
```

#### **Delete Role**
```bash
DELETE /api/roles/{id}
X-Transaction-ID: ROLE-20250727-001
```

#### **Check Role Name Exists**
```bash
GET /api/roles/check-name/{name}
X-Transaction-ID: ROLE-20250727-001
```

### **User-Role Assignment**

#### **Assign Roles to User**
```bash
POST /api/roles/users/{userId}/assign
Content-Type: application/json
X-Transaction-ID: USER-ROLE-20250727-001

["ADMIN", "USER", "MODERATOR"]
```

#### **Remove Roles from User**
```bash
POST /api/roles/users/{userId}/remove
Content-Type: application/json
X-Transaction-ID: USER-ROLE-20250727-001

["MODERATOR"]
```

#### **Get User Roles**
```bash
GET /api/roles/users/{userId}
X-Transaction-ID: USER-ROLE-20250727-001
```

#### **Get User Role Names**
```bash
GET /api/roles/users/{userId}/names
X-Transaction-ID: USER-ROLE-20250727-001
```

#### **Check if User Has Role**
```bash
GET /api/roles/users/{userId}/has-role/{roleName}
X-Transaction-ID: USER-ROLE-20250727-001
```

### **Role-Based User Queries**

#### **Get Users by Role Name**
```bash
GET /api/roles/name/{roleName}/users
X-Transaction-ID: USER-ROLE-20250727-001
```

#### **Count Users by Role Name**
```bash
GET /api/roles/name/{roleName}/count
X-Transaction-ID: USER-ROLE-20250727-001
```

## 🚀 Usage Examples

### **1. Setting Up Basic Roles**

```bash
# Create admin role
curl -X POST http://localhost:8080/api/roles \
  -H "Content-Type: application/json" \
  -H "X-Transaction-ID: SETUP-20250727-001" \
  -d '{
    "name": "ADMIN",
    "description": "Administrator with full access",
    "active": true
  }'

# Create user role
curl -X POST http://localhost:8080/api/roles \
  -H "Content-Type: application/json" \
  -H "X-Transaction-ID: SETUP-20250727-001" \
  -d '{
    "name": "USER",
    "description": "Regular user",
    "active": true
  }'

# Create moderator role
curl -X POST http://localhost:8080/api/roles \
  -H "Content-Type: application/json" \
  -H "X-Transaction-ID: SETUP-20250727-001" \
  -d '{
    "name": "MODERATOR",
    "description": "Content moderator",
    "active": true
  }'
```

### **2. Creating Users with Roles**

```bash
# Create a user
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -H "X-Transaction-ID: USER-20250727-001" \
  -d '{
    "username": "john_doe",
    "active": true
  }'

# Assign roles to the user
curl -X POST http://localhost:8080/api/roles/users/1/assign \
  -H "Content-Type: application/json" \
  -H "X-Transaction-ID: USER-ROLE-20250727-001" \
  -d '["ADMIN", "USER"]'
```

### **3. Role-Based Queries**

```bash
# Get all admin users
curl -X GET http://localhost:8080/api/roles/name/ADMIN/users \
  -H "X-Transaction-ID: QUERY-20250727-001"

# Check if user has admin role
curl -X GET http://localhost:8080/api/roles/users/1/has-role/ADMIN \
  -H "X-Transaction-ID: QUERY-20250727-001"

# Get user's role names
curl -X GET http://localhost:8080/api/roles/users/1/names \
  -H "X-Transaction-ID: QUERY-20250727-001"
```

## 🔍 Database Queries

### **Find Users by Role**
```sql
-- Get all users with ADMIN role
SELECT u.* FROM user u
JOIN user_roles ur ON u.id = ur.user_id
JOIN roles r ON ur.role_id = r.id
WHERE r.name = 'ADMIN' AND r.active = true;

-- Get users with multiple roles
SELECT u.username, GROUP_CONCAT(r.name) as roles
FROM user u
JOIN user_roles ur ON u.id = ur.user_id
JOIN roles r ON ur.role_id = r.id
WHERE r.active = true
GROUP BY u.id, u.username;
```

### **Role Statistics**
```sql
-- Count users per role
SELECT r.name, COUNT(u.id) as user_count
FROM roles r
LEFT JOIN user_roles ur ON r.id = ur.role_id
LEFT JOIN user u ON ur.user_id = u.id
WHERE r.active = true
GROUP BY r.id, r.name;

-- Find roles with no users
SELECT r.name FROM roles r
LEFT JOIN user_roles ur ON r.id = ur.role_id
WHERE ur.role_id IS NULL AND r.active = true;
```

### **Transaction Audit**
```sql
-- Find all operations for a transaction
SELECT 'USER' as entity_type, username, transaction_id, created_at
FROM user 
WHERE transaction_id = 'USER-ROLE-20250727-001'
UNION ALL
SELECT 'ROLE' as entity_type, name, transaction_id, created_at
FROM roles 
WHERE transaction_id = 'USER-ROLE-20250727-001';
```

## 🛠️ Implementation Details

### **Entity Relationships**

```java
// User entity
@ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
@JoinTable(
    name = "user_roles",
    joinColumns = @JoinColumn(name = "user_id"),
    inverseJoinColumns = @JoinColumn(name = "role_id")
)
private Set<Role> roles = new HashSet<>();

// Role entity
@ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
private Set<User> users = new HashSet<>();
```

### **Helper Methods**

```java
// User helper methods
public void addRole(Role role) {
    this.roles.add(role);
    role.getUsers().add(this);
}

public void removeRole(Role role) {
    this.roles.remove(role);
    role.getUsers().remove(this);
}

public boolean hasRole(String roleName) {
    return this.roles.stream()
            .anyMatch(role -> role.getName().equals(roleName) && role.isActive());
}

public Set<String> getRoleNames() {
    return this.roles.stream()
            .filter(Role::isActive)
            .map(Role::getName)
            .collect(Collectors.toSet());
}
```

## 🔧 Best Practices

### **1. Role Naming Convention**
- Use UPPERCASE for role names (e.g., `ADMIN`, `USER`, `MODERATOR`)
- Keep role names short and descriptive
- Use consistent naming across the application

### **2. Role Assignment**
- Always validate role existence before assignment
- Use transaction IDs for audit trails
- Implement role hierarchy if needed

### **3. Performance Considerations**
- Use lazy loading for role collections
- Index the junction table for better query performance
- Consider caching frequently accessed role data

### **4. Security**
- Validate role permissions before operations
- Log all role assignment/removal operations
- Implement role-based access control (RBAC)

## 🎯 Common Use Cases

### **1. E-commerce Roles**
```bash
# Create e-commerce specific roles
curl -X POST http://localhost:8080/api/roles \
  -H "Content-Type: application/json" \
  -d '{"name": "CUSTOMER", "description": "Regular customer"}'

curl -X POST http://localhost:8080/api/roles \
  -H "Content-Type: application/json" \
  -d '{"name": "SELLER", "description": "Product seller"}'

curl -X POST http://localhost:8080/api/roles \
  -H "Content-Type: application/json" \
  -d '{"name": "SUPPORT", "description": "Customer support"}'
```

### **2. Multi-Tenant Applications**
```bash
# Assign multiple roles for different contexts
curl -X POST http://localhost:8080/api/roles/users/1/assign \
  -H "Content-Type: application/json" \
  -d '["ADMIN", "CUSTOMER", "SUPPORT"]'
```

### **3. Role-Based Access Control**
```java
// Check user permissions
if (user.hasRole("ADMIN")) {
    // Allow admin operations
} else if (user.hasRole("MODERATOR")) {
    // Allow moderator operations
} else {
    // Allow basic user operations
}
```

## 🔍 Monitoring and Analytics

### **Role Usage Statistics**
```bash
# Get role statistics
curl -X GET http://localhost:8080/api/roles/name/ADMIN/count
curl -X GET http://localhost:8080/api/roles/name/USER/count
```

### **User Role Distribution**
```sql
-- Analyze role distribution
SELECT 
    r.name as role_name,
    COUNT(u.id) as user_count,
    ROUND(COUNT(u.id) * 100.0 / (SELECT COUNT(*) FROM user), 2) as percentage
FROM roles r
LEFT JOIN user_roles ur ON r.id = ur.role_id
LEFT JOIN user u ON ur.user_id = u.id
WHERE r.active = true
GROUP BY r.id, r.name
ORDER BY user_count DESC;
```

The user-role mapping system is now fully integrated and ready for use! 🚀 