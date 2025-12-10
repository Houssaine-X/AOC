# 🛒 Gestion des Commandes Multi-API

Système de gestion des commandes exposant **4 types d'API** : REST, SOAP, GraphQL et gRPC.

## 🚀 Démarrage Rapide

### Prérequis
- Java 17+
- Maven 3.8+

### Lancer l'Application

**Option 1 - Script automatisé** (Windows) :
```powershell
.\rebuild-and-run.ps1
```

**Option 2 - Commandes Maven** :
```bash
mvn clean install
mvn spring-boot:run
```

**L'application démarre sur** :
- Port `8099` (REST, SOAP, GraphQL)
- Port `9091` (gRPC)

---

## 📡 APIs Disponibles

| API | Endpoint | Documentation |
|-----|----------|---------------|
| **REST** | `http://localhost:8099/api/rest/orders` | [Voir tests REST](#-api-rest) |
| **SOAP** | `http://localhost:8099/ws` | [Voir tests SOAP](#-api-soap) |
| **GraphQL** | `http://localhost:8099/graphql` | [Voir tests GraphQL](#-api-graphql) |
| **gRPC** | `localhost:9091` | [Voir tests gRPC](#-api-grpc) |

### Outils d'exploration
- **GraphiQL** : http://localhost:8099/graphiql
- **Console H2** : http://localhost:8099/h2-console
  - JDBC URL: `jdbc:h2:mem:ordersdb`
  - Username: `sa`
  - Password: _(vide)_

---

## 🧪 Tests des APIs

### 🔷 API REST

**Récupérer toutes les commandes** :
```powershell
Invoke-WebRequest -Uri "http://localhost:8099/api/rest/orders" -Method GET
```

**Récupérer une commande** :
```powershell
Invoke-WebRequest -Uri "http://localhost:8099/api/rest/orders/1" -Method GET
```

**Créer une commande** :
```powershell
$order = @{
    clientId = 1
    source = "e-commerce"
    items = @(@{ productId = 1; quantity = 2 })
} | ConvertTo-Json

Invoke-WebRequest -Uri "http://localhost:8099/api/rest/orders" `
    -Method POST -ContentType "application/json" -Body $order
```

**Mettre à jour le statut** :
```powershell
Invoke-WebRequest -Uri "http://localhost:8099/api/rest/orders/1/status?status=SHIPPED" `
    -Method PATCH
```

---

### 🔶 API SOAP

**Récupérer une commande** :
```powershell
$soapRequest = @"
<?xml version="1.0" encoding="UTF-8"?>
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:tns="http://example.com/gestioncommandes/soap">
    <soapenv:Body>
        <tns:getOrderRequest>
            <tns:orderId>1</tns:orderId>
        </tns:getOrderRequest>
    </soapenv:Body>
</soapenv:Envelope>
"@

Invoke-WebRequest -Uri "http://localhost:8099/ws" `
    -Method POST -ContentType "text/xml" -Body $soapRequest
```

**WSDL disponible** : http://localhost:8099/ws/orders.wsdl

---

### 🔸 API GraphQL

**Récupérer toutes les commandes** :
```powershell
$query = @{
    query = "query { getAllOrders { id clientName totalAmount status } }"
} | ConvertTo-Json

Invoke-WebRequest -Uri "http://localhost:8099/graphql" `
    -Method POST -ContentType "application/json" -Body $query
```

**Créer une commande** :
```powershell
$mutation = @{
    query = @"
mutation {
  createOrder(input: {
    clientId: 1
    source: "mobile"
    items: [{productId: 1, quantity: 1}]
  }) {
    id
    totalAmount
    status
  }
}
"@
} | ConvertTo-Json

Invoke-WebRequest -Uri "http://localhost:8099/graphql" `
    -Method POST -ContentType "application/json" -Body $mutation
```

**⚠️ Note** : Utiliser `clientId` et `clientName` (pas d'objet `client` imbriqué).

**Interface interactive** : http://localhost:8099/graphiql

---

### 🔹 API gRPC

Utiliser un client gRPC (ex: BloomRPC, grpcurl) avec :
- **Host** : `localhost:9091`
- **Proto file** : `src/main/proto/notification.proto`

**Exemple avec grpcurl** :
```bash
grpcurl -plaintext -d '{
  "orderId": 1,
  "message": "Commande expédiée",
  "type": "ORDER_SHIPPED"
}' localhost:9091 notification.NotificationService/SendNotification
```

---

## 📋 Données de Test

Au démarrage, l'application charge automatiquement :
- **3 clients** (ID: 1, 2, 3)
- **5 produits** (ID: 1, 2, 3, 4, 5)
- **Commandes de démonstration**

Vérifier dans la console H2 : http://localhost:8099/h2-console

---

## 🧰 Scripts Utiles

| Script | Description |
|--------|-------------|
| `rebuild-and-run.ps1` | Recompile et lance l'application |
| `test-all-apis.ps1` | Teste automatiquement les 4 APIs |

---

## 📚 Documentation Complète

- **DIAGRAMS.md** - Diagrammes UML (composants, séquences)
- **ISSUE_FIXES.md** - Solutions aux problèmes courants
- **TESTING_GUIDE.md** - Guide de test détaillé
- **Postman_Collection_Updated.json** - Collection Postman

---

## 🛠️ Technologies

- Spring Boot 3.2.0
- Spring Data JPA
- Spring Web Services (SOAP)
- Spring GraphQL
- gRPC + Protocol Buffers
- H2 Database (in-memory)
- Lombok
- Maven

---

## 🐛 Dépannage

**Port déjà utilisé** :
```powershell
netstat -ano | findstr ":8099 :9091"
taskkill /F /PID <PID>
```

**Erreur SOAP "no Session"** :  
✅ Corrigé - Voir `ISSUE_FIXES.md`

**Erreur GraphQL "Field undefined"** :  
✅ Utiliser `clientId`/`clientName` au lieu de `client { }`

**Erreur REST 405** :  
✅ Utiliser `GET` pour récupérer, `POST` pour créer, `PATCH` pour modifier

---

## 📝 Licence

Ce projet est à usage éducatif.

