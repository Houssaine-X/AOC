# 🧪 Complete Testing Guide - Gestion Commandes Multi-Canaux

## 📋 Table of Contents

1. [Quick Start](#quick-start)
2. [GraphQL Testing](#graphql-testing)
3. [SOAP Testing](#soap-testing)
4. [gRPC Testing](#grpc-testing)
5. [REST API Testing](#rest-api-testing)
6. [Troubleshooting](#troubleshooting)

---

## 🚀 Quick Start

### Prerequisites

- Application running on **http://localhost:8099**
- gRPC service running on port **9091**
- Postman installed (for REST, GraphQL, SOAP)
- Optional: grpcurl or BloomRPC (for gRPC)

### Initial Setup

1. **Start the Application**:
   ```powershell
   cd C:\Users\houss\Gestion-commandes
   mvn spring-boot:run
   ```

2. **Wait for Startup** (look for this log):
   ```
   Started GestionCommandesApplication in X.XXX seconds
   ```

3. **Verify Application is Running**:
   ```powershell
   Invoke-WebRequest -Uri "http://localhost:8099/actuator/health" -UseBasicParsing
   ```

---

## 🎨 GraphQL Testing

### Issue You Encountered

**Error**: `Field 'allClients' in type 'Query' is undefined`

**Cause**: The GraphQL schema uses different naming conventions than expected.

### ✅ Correct GraphQL Query Names

The application uses these query names (defined in `schema.graphqls`):

| What You Might Try | ❌ Wrong | ✅ Correct |
|-------------------|---------|-----------|
| Get all clients | `allClients` | `getAllClients` |
| Get client by ID | `clientById(id: 1)` | `getClient(id: 1)` |
| Get all products | `allProducts` | `getAllProducts` |
| Get product by ID | `productById(id: 1)` | `getProduct(id: 1)` |
| Get all orders | `allOrders` | `getAllOrders` |
| Get order by ID | `orderById(id: 1)` | `getOrder(id: 1)` |
| Get orders by client | `ordersByClientId(clientId: 1)` | `getClientOrders(clientId: 1)` |
| Get order total | `clientTotalSpent(clientId: 1)` | `getOrderTotal(id: 1)` |

### ✅ Working GraphQL Queries

#### 1. Get All Clients

```graphql
query {
  getAllClients {
    id
    name
    email
    phone
    address
  }
}
```

**Postman/HTTP Request**:
```json
POST http://localhost:8099/graphql
Content-Type: application/json

{
  "query": "query { getAllClients { id name email phone address } }"
}
```

#### 2. Get Client by ID

```graphql
query {
  getClient(id: 1) {
    id
    name
    email
    phone
    address
  }
}
```

#### 3. Get All Products

```graphql
query {
  getAllProducts {
    id
    name
    description
    price
    stockQuantity
    category
  }
}
```

#### 4. Get All Orders (with nested data)

```graphql
query {
  getAllOrders {
    id
    clientId
    clientName
    orderDate
    status
    totalAmount
    source
    items {
      id
      productId
      productName
      quantity
      unitPrice
      subtotal
    }
  }
}
```

#### 5. Get Order by ID

```graphql
query {
  getOrder(id: 1) {
    id
    clientId
    clientName
    orderDate
    status
    totalAmount
    source
    items {
      productId
      productName
      quantity
      unitPrice
      subtotal
    }
  }
}
```

#### 6. Get Client's Orders

```graphql
query {
  getClientOrders(clientId: 1) {
    id
    orderDate
    status
    totalAmount
    source
  }
}
```

#### 7. Get Order Total

```graphql
query {
  getOrderTotal(id: 1)
}
```

#### 8. Create Order (Mutation)

```graphql
mutation {
  createOrder(input: {
    clientId: 1
    source: "e-commerce"
    items: [
      { productId: 1, quantity: 2 },
      { productId: 2, quantity: 1 }
    ]
  }) {
    id
    clientName
    orderDate
    status
    totalAmount
  }
}
```

#### 9. Update Order Status (Mutation)

```graphql
mutation {
  updateOrderStatus(orderId: 1, status: SHIPPED) {
    id
    status
    totalAmount
  }
}
```

**Available Status Values**: `PENDING`, `CONFIRMED`, `PROCESSING`, `SHIPPED`, `DELIVERED`, `CANCELLED`

### Using GraphiQL Interface

**Issue**: GraphiQL stuck on loading

**Solution**:

1. **Open in Browser**: http://localhost:8099/graphiql
2. **If it loads slowly**: Clear browser cache and refresh
3. **If still not loading**: Check browser console for errors (F12)
4. **Alternative**: Use Postman or Insomnia for GraphQL testing

**Browser Console Check**:
```javascript
// Open browser console (F12) and check for errors
// Common issue: CORS or connection errors
```

### PowerShell Testing

```powershell
# Test GraphQL query
$query = @{
    query = "query { getAllClients { id name email } }"
} | ConvertTo-Json

$response = Invoke-WebRequest `
    -Uri "http://localhost:8099/graphql" `
    -Method POST `
    -ContentType "application/json" `
    -Body $query

$response.Content | ConvertFrom-Json | ConvertTo-Json -Depth 10
```

---

## 🧼 SOAP Testing

### Issue You Encountered

**Error**: SOAP requests not working

**Causes**:
1. Wrong namespace: Used `http://www.example.com/gestioncommandes/orders` instead of `http://example.com/gestioncommandes/soap`
2. Wrong element names: Used capitalized names instead of camelCase
3. Missing operations: Tried to use `calculateTotalRequest` which doesn't exist

### ✅ Correct SOAP Configuration

**WSDL URL**: http://localhost:8099/ws/orders.wsdl  
**Endpoint**: http://localhost:8099/ws  
**Namespace**: `http://example.com/gestioncommandes/soap`  
**Prefix**: `tns`

### Available SOAP Operations

1. ✅ `getOrderRequest` - Get order by ID
2. ✅ `getClientOrdersRequest` - Get all orders for a client
3. ✅ `createOrderRequest` - Create a new order
4. ✅ `updateOrderStatusRequest` - Update order status
5. ❌ `calculateTotalRequest` - **NOT AVAILABLE** (use REST API instead)

### ✅ Working SOAP Requests

#### 1. Get Order by ID

```xml
<?xml version="1.0" encoding="UTF-8"?>
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:tns="http://example.com/gestioncommandes/soap">
    <soapenv:Header/>
    <soapenv:Body>
        <tns:getOrderRequest>
            <tns:orderId>1</tns:orderId>
        </tns:getOrderRequest>
    </soapenv:Body>
</soapenv:Envelope>
```

**Response**:
```xml
<SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/">
    <SOAP-ENV:Body>
        <ns2:getOrderResponse xmlns:ns2="http://example.com/gestioncommandes/soap">
            <ns2:order>
                <ns2:id>1</ns2:id>
                <ns2:clientId>1</ns2:clientId>
                <ns2:clientName>Jean Dupont</ns2:clientName>
                <ns2:orderDate>2025-12-10T...</ns2:orderDate>
                <ns2:status>PENDING</ns2:status>
                <ns2:totalAmount>2599.98</ns2:totalAmount>
                <ns2:source>e-commerce</ns2:source>
            </ns2:order>
        </ns2:getOrderResponse>
    </SOAP-ENV:Body>
</SOAP-ENV:Envelope>
```

#### 2. Get Client Orders

```xml
<?xml version="1.0" encoding="UTF-8"?>
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:tns="http://example.com/gestioncommandes/soap">
    <soapenv:Header/>
    <soapenv:Body>
        <tns:getClientOrdersRequest>
            <tns:clientId>1</tns:clientId>
        </tns:getClientOrdersRequest>
    </soapenv:Body>
</soapenv:Envelope>
```

#### 3. Create Order

```xml
<?xml version="1.0" encoding="UTF-8"?>
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:tns="http://example.com/gestioncommandes/soap">
    <soapenv:Header/>
    <soapenv:Body>
        <tns:createOrderRequest>
            <tns:clientId>1</tns:clientId>
            <tns:source>mobile-app</tns:source>
            <tns:items>
                <tns:productId>1</tns:productId>
                <tns:quantity>2</tns:quantity>
            </tns:items>
            <tns:items>
                <tns:productId>2</tns:productId>
                <tns:quantity>1</tns:quantity>
            </tns:items>
        </tns:createOrderRequest>
    </soapenv:Body>
</soapenv:Envelope>
```

#### 4. Update Order Status

```xml
<?xml version="1.0" encoding="UTF-8"?>
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:tns="http://example.com/gestioncommandes/soap">
    <soapenv:Header/>
    <soapenv:Body>
        <tns:updateOrderStatusRequest>
            <tns:orderId>1</tns:orderId>
            <tns:status>SHIPPED</tns:status>
        </tns:updateOrderStatusRequest>
    </soapenv:Body>
</soapenv:Envelope>
```

**Valid Status Values**: `PENDING`, `CONFIRMED`, `PROCESSING`, `SHIPPED`, `DELIVERED`, `CANCELLED`

### Testing with PowerShell

```powershell
$soapRequest = @"
<?xml version="1.0" encoding="UTF-8"?>
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:tns="http://example.com/gestioncommandes/soap">
    <soapenv:Header/>
    <soapenv:Body>
        <tns:getOrderRequest>
            <tns:orderId>1</tns:orderId>
        </tns:getOrderRequest>
    </soapenv:Body>
</soapenv:Envelope>
"@

$response = Invoke-WebRequest `
    -Uri "http://localhost:8099/ws" `
    -Method POST `
    -ContentType "text/xml" `
    -Body $soapRequest

$response.Content
```

### Testing with curl

```bash
curl -X POST http://localhost:8099/ws \
  -H "Content-Type: text/xml" \
  -d '<?xml version="1.0" encoding="UTF-8"?>
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:tns="http://example.com/gestioncommandes/soap">
    <soapenv:Header/>
    <soapenv:Body>
        <tns:getOrderRequest>
            <tns:orderId>1</tns:orderId>
        </tns:getOrderRequest>
    </soapenv:Body>
</soapenv:Envelope>'
```

### Viewing the WSDL

```powershell
# Download and view WSDL
Invoke-WebRequest -Uri "http://localhost:8099/ws/orders.wsdl" -OutFile "orders.wsdl"
notepad orders.wsdl
```

---

## 🔌 gRPC Testing

### Overview

The gRPC service is a **notification service** that receives messages when orders are created or updated. It runs on port **9091**.

### Service Definition

**File**: `src/main/proto/notification.proto`

```protobuf
syntax = "proto3";

package notification;

service NotificationService {
  rpc SendNotification (NotificationRequest) returns (NotificationResponse);
}

message NotificationRequest {
  string order_id = 1;
  string message = 2;
  string notification_type = 3;
}

message NotificationResponse {
  bool success = 1;
  string message = 2;
}
```

### Testing Methods

#### Method 1: Using grpcurl (Recommended)

**Installation**:
```powershell
# Using Chocolatey
choco install grpcurl

# Or download from: https://github.com/fullstorydev/grpcurl/releases
```

**List Services**:
```bash
grpcurl -plaintext localhost:9091 list
```

**Expected Output**:
```
grpc.reflection.v1alpha.ServerReflection
notification.NotificationService
```

**Describe Service**:
```bash
grpcurl -plaintext localhost:9091 describe notification.NotificationService
```

**Send Test Notification**:
```bash
grpcurl -plaintext -d '{
  "order_id": "1",
  "message": "Order created successfully",
  "notification_type": "ORDER_CREATED"
}' localhost:9091 notification.NotificationService/SendNotification
```

**Expected Response**:
```json
{
  "success": true,
  "message": "Notification received: Order created successfully for order 1"
}
```

#### Method 2: Using BloomRPC (GUI Tool)

**Installation**:
1. Download from: https://github.com/bloomrpc/bloomrpc/releases
2. Install and launch BloomRPC

**Setup**:
1. Click "Import Paths" → Add: `C:\Users\houss\Gestion-commandes\src\main\proto`
2. Click "Import Protos" → Select `notification.proto`
3. Select `NotificationService` → `SendNotification`
4. Enter server URL: `localhost:9091`
5. Uncheck "TLS" (use plaintext)

**Test Request**:
```json
{
  "order_id": "1",
  "message": "Test notification from BloomRPC",
  "notification_type": "ORDER_UPDATED"
}
```

#### Method 3: Using Postman (gRPC Support)

**Note**: Postman now supports gRPC in recent versions.

1. Create new gRPC Request
2. Enter server URL: `localhost:9091`
3. Import proto file: `notification.proto`
4. Select method: `SendNotification`
5. Enter request body (JSON format)
6. Send request

#### Method 4: Programmatic Testing (Java)

**File**: `src/main/java/com/example/gestioncommandes/grpc/NotificationGrpcClient.java`

The application already includes a gRPC client that is automatically called when orders are created.

**Manual Test** (Create a test class):

```java
package com.example.gestioncommandes.grpc;

import com.example.notification.NotificationRequest;
import com.example.notification.NotificationResponse;
import com.example.notification.NotificationServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class GrpcManualTest {
    public static void main(String[] args) {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", 9091)
                .usePlaintext()
                .build();

        NotificationServiceGrpc.NotificationServiceBlockingStub stub = 
                NotificationServiceGrpc.newBlockingStub(channel);

        NotificationRequest request = NotificationRequest.newBuilder()
                .setOrderId("123")
                .setMessage("Test order created")
                .setNotificationType("ORDER_CREATED")
                .build();

        NotificationResponse response = stub.sendNotification(request);
        
        System.out.println("Success: " + response.getSuccess());
        System.out.println("Message: " + response.getMessage());

        channel.shutdown();
    }
}
```

### ✅ Method 5: Testing via Console (Easiest Way!)

**YES!** You can see gRPC notifications directly in your application console. This is the **simplest method** - no additional tools needed!

#### How It Works

When you create or update an order through **any channel** (REST, SOAP, GraphQL), the application automatically:
1. Creates the order
2. Sends a gRPC notification
3. **Logs the notification details to the console**

#### Step-by-Step Console Testing

**Step 1: Start the Application**
```powershell
mvn spring-boot:run
```

**Step 2: Watch for gRPC Startup Messages**
```
gRPC Server started, listening on address: 0.0.0.0, port: 9091
Registered gRPC service: notification.NotificationService
```

**Step 3: Create an Order (Choose Any Method)**

**Option A - Using REST API:**
```powershell
$orderData = @{
    clientId = 1
    items = @(
        @{ productId = 1; quantity = 2 }
    )
} | ConvertTo-Json

Invoke-WebRequest `
    -Uri "http://localhost:8099/api/rest/orders" `
    -Method POST `
    -ContentType "application/json" `
    -Body $orderData
```

**Option B - Using GraphQL:**
```powershell
$query = @{
    query = "mutation { createOrder(input: { clientId: 1, items: [{ productId: 1, quantity: 2 }] }) { id clientName status totalAmount } }"
} | ConvertTo-Json

Invoke-WebRequest `
    -Uri "http://localhost:8099/graphql" `
    -Method POST `
    -ContentType "application/json" `
    -Body $query
```

**Option C - Using SOAP:**
```powershell
$soapRequest = @"
<?xml version="1.0" encoding="UTF-8"?>
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:tns="http://example.com/gestioncommandes/soap">
    <soapenv:Body>
        <tns:createOrderRequest>
            <tns:clientId>1</tns:clientId>
            <tns:items>
                <tns:productId>1</tns:productId>
                <tns:quantity>2</tns:quantity>
            </tns:items>
        </tns:createOrderRequest>
    </soapenv:Body>
</soapenv:Envelope>
"@

Invoke-WebRequest `
    -Uri "http://localhost:8099/ws" `
    -Method POST `
    -ContentType "text/xml" `
    -Body $soapRequest
```

**Step 4: Check Console Output**

You will see detailed notification logs like this:

```
=== Order Created Notification ===
Order ID: 1
Client: Jean Dupont (ID: 1)
Total Amount: $1299.99
Status: PENDING
Message: New order created successfully
===================================
gRPC Notification sent: Order creation notification sent successfully
```

#### What You'll See in the Console

**When Creating an Order:**
```
INFO  - Order created with ID: 1
INFO  - === Order Created Notification ===
INFO  - Order ID: 1
INFO  - Client: Jean Dupont (ID: 1)
INFO  - Total Amount: $1299.99
INFO  - Status: PENDING
INFO  - Message: New order created successfully
INFO  - ===================================
INFO  - gRPC Notification sent: Order creation notification sent successfully
```

**When Updating Order Status:**
```
INFO  - Order status updated to: SHIPPED
INFO  - === Order Status Changed Notification ===
INFO  - Order ID: 1
INFO  - Client: Jean Dupont (ID: 1)
INFO  - New Status: SHIPPED
INFO  - Message: Order status updated to: SHIPPED
INFO  - =========================================
INFO  - gRPC Notification sent: Status change notification sent successfully
```

#### Testing Status Updates (Also Triggers Notifications)

```powershell
# Update order status via REST
Invoke-WebRequest `
    -Uri "http://localhost:8099/api/rest/orders/1/status?status=SHIPPED" `
    -Method PATCH
```

**Console Output:**
```
=== Order Status Changed Notification ===
Order ID: 1
Client: Jean Dupont (ID: 1)
New Status: SHIPPED
Message: Order status updated to: SHIPPED
=========================================
```

#### Console Notification Examples for Each Status

**PENDING → CONFIRMED:**
```
INFO  - === Order Status Changed Notification ===
INFO  - Order ID: 5
INFO  - Client: Marie Martin (ID: 2)
INFO  - New Status: CONFIRMED
INFO  - Message: Order status updated to: CONFIRMED
```

**CONFIRMED → PROCESSING:**
```
INFO  - === Order Status Changed Notification ===
INFO  - Order ID: 5
INFO  - New Status: PROCESSING
INFO  - Message: Order is being processed
```

**PROCESSING → SHIPPED:**
```
INFO  - === Order Status Changed Notification ===
INFO  - Order ID: 5
INFO  - New Status: SHIPPED
INFO  - Message: Order has been shipped
```

**SHIPPED → DELIVERED:**
```
INFO  - === Order Status Changed Notification ===
INFO  - Order ID: 5
INFO  - New Status: DELIVERED
INFO  - Message: Order has been delivered
```

#### Why Console Testing is Great

✅ **No additional tools needed** - just your terminal  
✅ **Real-time feedback** - see notifications instantly  
✅ **Complete information** - all notification details displayed  
✅ **Works with all channels** - REST, SOAP, GraphQL, gRPC  
✅ **Perfect for demos** - easy to show during interviews  

#### Quick Test Script (Create & Update Order)

```powershell
# 1. Create an order
Write-Host "Creating order..." -ForegroundColor Green
$order = @{ clientId = 1; items = @(@{ productId = 1; quantity = 2 }) } | ConvertTo-Json
$response = Invoke-WebRequest -Uri "http://localhost:8099/api/rest/orders" -Method POST -ContentType "application/json" -Body $order
$orderId = ($response.Content | ConvertFrom-Json).id
Write-Host "Order $orderId created - Check console for notification!" -ForegroundColor Yellow

Start-Sleep -Seconds 2

# 2. Update status to CONFIRMED
Write-Host "Updating to CONFIRMED..." -ForegroundColor Green
Invoke-WebRequest -Uri "http://localhost:8099/api/rest/orders/$orderId/status?status=CONFIRMED" -Method PATCH
Write-Host "Status updated - Check console for notification!" -ForegroundColor Yellow

Start-Sleep -Seconds 2

# 3. Update status to SHIPPED
Write-Host "Updating to SHIPPED..." -ForegroundColor Green
Invoke-WebRequest -Uri "http://localhost:8099/api/rest/orders/$orderId/status?status=SHIPPED" -Method PATCH
Write-Host "Status updated - Check console for notification!" -ForegroundColor Yellow

Write-Host "`nCheck your application console to see all 3 notifications!" -ForegroundColor Cyan
```

**Expected Console Output:**
```
=== Order Created Notification ===
Order ID: 6
Client: Jean Dupont (ID: 1)
Total Amount: $2599.98
Status: PENDING
...

=== Order Status Changed Notification ===
Order ID: 6
New Status: CONFIRMED
...

=== Order Status Changed Notification ===
Order ID: 6
New Status: SHIPPED
...
```

### Automatic gRPC Notifications

When you create an order via REST, SOAP, or GraphQL, the application **automatically sends a gRPC notification**.

**Check Logs**:
```
Sending gRPC notification for order 1
Notification sent successfully: Notification received: Order created...
```

### gRPC Port Configuration

**File**: `src/main/resources/application.properties`
```properties
grpc.server.port=9091
```

**File**: `src/main/java/com/example/gestioncommandes/grpc/NotificationGrpcClient.java`
```java
this.channel = ManagedChannelBuilder
        .forAddress("localhost", 9091)  // ✅ Correct port
        .usePlaintext()
        .build();
```

### Common gRPC Issues

#### Issue 1: Connection Refused

**Error**: `UNAVAILABLE: io exception`

**Solution**:
```powershell
# Check if gRPC port is listening
netstat -ano | findstr :9091

# If not, check application logs for startup errors
```

#### Issue 2: Method Not Found

**Error**: `UNIMPLEMENTED: Method not found`

**Solution**:
- Verify the service name and method name match exactly
- Correct: `notification.NotificationService/SendNotification`
- Check proto file for exact definitions

#### Issue 3: TLS/SSL Errors

**Error**: `SSL handshake failed`

**Solution**:
- The application uses **plaintext** (no TLS)
- In grpcurl, use `-plaintext` flag
- In clients, use `.usePlaintext()`

---

## 🔧 REST API Testing

The REST API is the easiest to test and works correctly. Use the Postman collection for comprehensive tests.

**Base URL**: http://localhost:8099/api/rest/orders

### Quick REST Tests

```powershell
# Get all orders
Invoke-WebRequest -Uri "http://localhost:8099/api/rest/orders" -UseBasicParsing

# Get order by ID
Invoke-WebRequest -Uri "http://localhost:8099/api/rest/orders/1" -UseBasicParsing

# Create order
$orderData = @{
    clientId = 1
    source = "e-commerce"
    items = @(
        @{ productId = 1; quantity = 2 }
    )
} | ConvertTo-Json

Invoke-WebRequest `
    -Uri "http://localhost:8099/api/rest/orders" `
    -Method POST `
    -ContentType "application/json" `
    -Body $orderData
```

---

## 🔍 Troubleshooting

### SOAP Error: "could not initialize proxy - no Session"

**Error**:
```xml
<faultstring>could not initialize proxy [com.example.gestioncommandes.model.Client#1] - no Session</faultstring>
```

**Cause**: This error occurs when trying to access lazy-loaded JPA entities (like `Client`) outside of a transaction context.

**Solution**: ✅ **FIXED!** Added `@Transactional` annotations to SOAP endpoint methods. Rebuild and restart the application:

```powershell
mvn clean install
mvn spring-boot:run
```

After restart, SOAP `getOrderRequest` and `getClientOrdersRequest` will work correctly.

### REST Error: "405 Method Not Allowed"

**Error**:
```json
{
  "timestamp": "2025-12-10T23:00:53.955+00:00",
  "status": 405,
  "error": "Method Not Allowed",
  "path": "/api/rest/orders/9"
}
```

**Cause**: You're using the wrong HTTP method (probably PUT or POST instead of GET).

**Solution**:
- ✅ Use **GET** for `/api/rest/orders/{id}`
- ✅ Use **GET** for `/api/rest/orders` (all orders)
- ✅ Use **POST** for creating: `/api/rest/orders`
- ✅ Use **PATCH** for updating status: `/api/rest/orders/{id}/status?status=SHIPPED`

**Correct PowerShell Command**:
```powershell
# ✅ Correct - GET method
Invoke-WebRequest -Uri "http://localhost:8099/api/rest/orders/9" -Method GET

# ❌ Wrong - will give 405 error
Invoke-WebRequest -Uri "http://localhost:8099/api/rest/orders/9" -Method PUT
```

### GraphQL Error: "Field 'client' in type 'Order' is undefined"

**Error**:
```json
{
  "message": "Validation error (FieldUndefined@[getAllOrders/client]) : Field 'client' in type 'Order' is undefined"
}
```

**Cause**: The GraphQL schema does NOT have nested `client` or `product` objects. It only has IDs and names.

**❌ Wrong Query**:
```graphql
query {
  getAllOrders {
    client { id name }  # ❌ Field 'client' doesn't exist
    items {
      product { id name }  # ❌ Field 'product' doesn't exist
    }
  }
}
```

**✅ Correct Query**:
```graphql
query {
  getAllOrders {
    id
    clientId       # ✅ Use clientId, not client object
    clientName     # ✅ Use clientName for display
    orderDate
    status
    totalAmount
    source
    items {
      id
      productId    # ✅ Use productId, not product object
      productName  # ✅ Use productName for display
      quantity
      unitPrice
      subtotal
    }
  }
}
```

**If You Need Full Client or Product Details**:
```graphql
query {
  # First get the order
  getOrder(id: 1) {
    clientId
    clientName
  }
  
  # Then get full client details separately
  getClient(id: 1) {
    id
    name
    email
    phone
    address
  }
}
```

### Application Not Starting

**Check**:
```powershell
# Check if ports are in use
netstat -ano | findstr ":8099 :9091"

# Kill processes if needed
taskkill /F /PID <PID>
```

### Database Issues

**Access H2 Console**:
- URL: http://localhost:8099/h2-console
- JDBC URL: `jdbc:h2:mem:ordersdb`
- Username: `sa`
- Password: (empty)

### GraphiQL Not Loading

**Solutions**:
1. Clear browser cache
2. Try different browser
3. Check browser console (F12) for errors
4. Use Postman or Insomnia instead

### SOAP Returns 500 Error

**Check**:
1. Namespace is correct: `http://example.com/gestioncommandes/soap`
2. Element names are camelCase: `getOrderRequest` not `GetOrderRequest`
3. Request structure matches XSD
4. View WSDL for exact requirements: http://localhost:8099/ws/orders.wsdl

### gRPC Connection Refused

**Check**:
```powershell
# Verify gRPC server is running
netstat -ano | findstr :9091

# Check application logs for gRPC startup
# Should see: "gRPC Server started, listening on port 9091"
```

### Testing Order

1. ✅ Start application
2. ✅ Run Setup requests in Postman (Section 0)
3. ✅ Test REST API (easiest)
4. ✅ Test GraphQL with correct query names
5. ✅ Test SOAP with correct namespace
6. ✅ Test gRPC with grpcurl or BloomRPC

---

## 📦 Summary

### ✅ Fixed Issues

| Issue | Root Cause | Solution |
|-------|-----------|----------|
| GraphQL fields undefined | Wrong query names | Use `getAllClients` not `allClients` |
| SOAP not working | Wrong namespace | Use `http://example.com/gestioncommandes/soap` |
| SOAP element errors | Wrong casing | Use `getOrderRequest` not `GetOrderRequest` |
| GraphiQL loading | Browser cache | Clear cache or use Postman |
| gRPC testing | No tools | Install grpcurl or BloomRPC |

### 📊 Ports Summary

- **8099**: REST, GraphQL, SOAP, H2 Console
- **9091**: gRPC

### 🎯 Next Steps

1. Import updated Postman collection: `Postman_Collection_Updated.json`
2. Install grpcurl for gRPC testing
3. Run all tests in order (Setup → REST → GraphQL → SOAP → gRPC)

---

**Documentation created**: December 10, 2025  
**Application**: Gestion Commandes Multi-Canaux v1.0.0

