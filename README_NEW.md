# 🛒 Gestion Commandes Multi-Canaux

> Système centralisé de gestion des commandes provenant de multiples sources (e-commerce, mobile, B2B) avec exposition via **REST**, **SOAP**, **GraphQL** et **gRPC**.

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

---

## 📋 Table des Matières

- [🎯 Objectifs](#-objectifs)
- [🏗️ Architecture](#️-architecture)
- [🚀 Démarrage Rapide](#-démarrage-rapide)
- [🔌 APIs Disponibles](#-apis-disponibles)
- [🧪 Tests](#-tests)
- [📊 Base de Données](#-base-de-données)
- [📚 Documentation](#-documentation)

---

## 🎯 Objectifs

Ce projet illustre une architecture moderne multi-canaux permettant à différents types de clients d'accéder aux mêmes services métier via leur protocole préféré:

- **REST** → Applications web modernes, SPAs
- **GraphQL** → Applications mobiles, clients flexibles
- **SOAP** → Systèmes legacy, partenaires B2B
- **gRPC** → Communication inter-microservices, haute performance

### Technologies Utilisées

| Catégorie | Technologies |
|-----------|-------------|
| **Backend** | Spring Boot 3.2.0, Java 17 |
| **Persistance** | Spring Data JPA, H2 Database |
| **APIs** | Spring Web, Spring Data REST, Spring GraphQL, Spring WS |
| **Messaging** | gRPC, Protocol Buffers |
| **Outils** | Lombok, Maven, JAXB |

---

## 🏗️ Architecture

### Architecture en Couches

```
┌─────────────────────────────────────────────────────┐
│               COUCHE PRÉSENTATION                   │
├─────────────┬──────────┬──────────┬─────────────────┤
│  REST API   │ GraphQL  │   SOAP   │      gRPC       │
│ Controllers │ Resolvers│ Endpoints│    Services     │
└─────────────┴──────────┴──────────┴─────────────────┘
                         │
┌────────────────────────▼────────────────────────────┐
│              COUCHE SERVICE MÉTIER                  │
│  • OrderService    • Validation                     │
│  • Calculs         • Logique métier                 │
└──────────────────────┬──────────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────────┐
│           COUCHE ACCÈS AUX DONNÉES                  │
│  • Spring Data Repositories                         │
│  • OrderRepository  • ClientRepository              │
│  • ProductRepository                                │
└──────────────────────┬──────────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────────┐
│                BASE DE DONNÉES                      │
│              H2 In-Memory Database                  │
│  Tables: clients, products, orders, order_items     │
└─────────────────────────────────────────────────────┘
```

### Structure du Projet

```
src/main/
├── java/com/example/gestioncommandes/
│   ├── GestionCommandesApplication.java    # Point d'entrée
│   ├── config/                             # Configuration Spring
│   ├── model/                              # Entités JPA
│   │   ├── Client.java
│   │   ├── Product.java
│   │   ├── Order.java
│   │   ├── OrderItem.java
│   │   └── OrderStatus.java
│   ├── repository/                         # Spring Data Repositories
│   │   ├── ClientRepository.java
│   │   ├── ProductRepository.java
│   │   └── OrderRepository.java
│   ├── service/                            # Logique métier
│   │   └── OrderService.java
│   ├── dto/                                # Data Transfer Objects
│   │   ├── CreateOrderRequest.java
│   │   └── OrderResponse.java
│   ├── web/
│   │   ├── rest/                           # REST Controllers
│   │   │   └── OrderRestController.java
│   │   ├── graphql/                        # GraphQL Resolvers
│   │   │   └── OrderGraphQLController.java
│   │   └── soap/                           # SOAP Endpoints
│   │       ├── OrderSoapEndpoint.java
│   │       └── WebServiceConfig.java
│   └── grpc/                               # gRPC Services
│       ├── NotificationServiceImpl.java
│       └── GrpcServerConfig.java
├── resources/
│   ├── application.properties              # Configuration
│   ├── graphql/schema.graphqls             # Schéma GraphQL
│   └── xsd/orders.xsd                      # Schéma SOAP
└── proto/
    └── notification.proto                  # Définition gRPC
```

**Voir [ARCHITECTURE.md](ARCHITECTURE.md) pour les diagrammes détaillés.**

---

## 🚀 Démarrage Rapide

### Prérequis

- ☑️ **Java 17** ou supérieur ([Télécharger](https://adoptium.net/))
- ☑️ **Maven 3.6+** ([Télécharger](https://maven.apache.org/download.cgi))
- ☑️ Ports disponibles: **8080** (HTTP), **9090** (gRPC)

### Installation et Lancement

#### Option 1: Maven (Recommandé)

```powershell
# Cloner et accéder au projet
cd C:\Users\houss\Gestion-commandes

# Compiler le projet
mvn clean install

# Lancer l'application
mvn spring-boot:run
```

#### Option 2: Script PowerShell

```powershell
.\run.ps1
```

#### Option 3: JAR Exécutable

```powershell
mvn clean package
java -jar target/gestion-commandes-1.0.0.jar
```

### Vérification

✅ Application démarrée: `http://localhost:8080`

```bash
# Test rapide
curl http://localhost:8080/api/data
```

**Réponse attendue**: JSON avec les liens HAL vers les ressources disponibles.

---

## 🔌 APIs Disponibles

### 1️⃣ REST API - Endpoints Personnalisés

**Base URL**: `http://localhost:8080/api/rest/orders`

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `POST` | `/api/rest/orders` | Créer une commande |
| `GET` | `/api/rest/orders` | Lister toutes les commandes |
| `GET` | `/api/rest/orders/{id}` | Récupérer une commande |
| `GET` | `/api/rest/orders/client/{id}` | Commandes d'un client |
| `GET` | `/api/rest/orders/{id}/total` | Calculer le total |
| `PATCH` | `/api/rest/orders/{id}/status` | Mettre à jour le statut |

#### Exemple: Créer une Commande

```bash
curl -X POST http://localhost:8080/api/rest/orders \
  -H "Content-Type: application/json" \
  -d '{
    "clientId": 1,
    "source": "e-commerce",
    "items": [
      {"productId": 1, "quantity": 2},
      {"productId": 2, "quantity": 1}
    ]
  }'
```

---

### 2️⃣ Spring Data REST - CRUD Automatique

**Base URL**: `http://localhost:8080/api/data`

Accès direct aux entités avec CRUD automatique et recherches personnalisées.

| Ressource | Endpoint | Opérations |
|-----------|----------|------------|
| **Clients** | `/api/data/clients` | GET, POST, PUT, PATCH, DELETE |
| **Produits** | `/api/data/products` | GET, POST, PUT, PATCH, DELETE |
| **Commandes** | `/api/data/orders` | GET, POST, PUT, PATCH, DELETE |

#### Exemple: Recherche Personnalisée

```bash
# Rechercher un client par email
curl http://localhost:8080/api/data/clients/search/findByEmail?email=jean@example.com

# Produits par catégorie
curl http://localhost:8080/api/data/products/search/findByCategory?category=Informatique
```

---

### 3️⃣ GraphQL - Requêtes Flexibles

**Endpoint**: `http://localhost:8080/graphql`  
**Interface**: `http://localhost:8080/graphiql` (navigateur)

#### Exemple: Query - Récupérer des Commandes

```graphql
query {
  getAllOrders {
    id
    clientName
    orderDate
    status
    totalAmount
    items {
      productName
      quantity
      unitPrice
    }
  }
}
```

#### Exemple: Mutation - Créer une Commande

```graphql
mutation {
  createOrder(input: {
    clientId: "1"
    source: "mobile-app"
    items: [
      {productId: "1", quantity: 2}
    ]
  }) {
    id
    clientName
    totalAmount
    status
  }
}
```

**Interface GraphiQL**: Ouvrir `http://localhost:8080/graphiql` dans votre navigateur pour tester interactivement.

---

### 4️⃣ SOAP - Web Services Legacy

**Endpoint**: `http://localhost:8080/ws`  
**WSDL**: `http://localhost:8080/ws/orders.wsdl`

#### Exemple: Récupérer une Commande

```xml
POST http://localhost:8080/ws
Content-Type: text/xml

<?xml version="1.0"?>
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" 
                  xmlns:soap="http://example.com/gestioncommandes/soap">
  <soapenv:Header/>
  <soapenv:Body>
    <soap:getOrderRequest>
      <soap:orderId>1</soap:orderId>
    </soap:getOrderRequest>
  </soapenv:Body>
</soapenv:Envelope>
```

#### Test avec Script

```powershell
.\test-soap.ps1
```

---

### 5️⃣ gRPC - Communication Haute Performance

**Port**: `9090`  
**Service**: `NotificationService`

Le service gRPC est utilisé pour les notifications internes. Il est automatiquement appelé lors de la création d'une commande.

#### Définition (Proto)

```protobuf
service NotificationService {
  rpc SendNotification (NotificationRequest) returns (NotificationResponse);
}
```

**Note**: Vérifier les logs pour voir les notifications envoyées:
```
[gRPC] Notification envoyée pour la commande #1
```

---

## 🧪 Tests

### Collection Postman

**Importer**: `Postman_Collection_Complete.json`

La collection contient **40+ requêtes** organisées en 5 sections:

```
📂 Gestion Commandes - Tests Complets
├── 0. Setup - Données de Test (6 requêtes)
├── 1. REST API (11 requêtes)
├── 2. Spring Data REST (13 requêtes)
├── 3. GraphQL (10 requêtes)
├── 4. SOAP (5 requêtes)
└── 5. Monitoring (3 requêtes)
```

### Ordre d'Exécution

1. **Setup** → Créer clients et produits
2. **REST API** → Tester la création de commandes
3. **GraphQL** → Tester queries et mutations
4. **SOAP** → Tester opérations SOAP
5. **Spring Data REST** → Tester CRUD automatique

### Tests avec cURL

```bash
# 1. Créer un client
curl -X POST http://localhost:8080/api/data/clients \
  -H "Content-Type: application/json" \
  -d '{"name":"Jean Dupont","email":"jean@example.com"}'

# 2. Créer un produit
curl -X POST http://localhost:8080/api/data/products \
  -H "Content-Type: application/json" \
  -d '{"name":"Laptop","price":1299.99,"stockQuantity":50}'

# 3. Créer une commande
curl -X POST http://localhost:8080/api/rest/orders \
  -H "Content-Type: application/json" \
  -d '{"clientId":1,"source":"e-commerce","items":[{"productId":1,"quantity":2}]}'

# 4. Récupérer la commande
curl http://localhost:8080/api/rest/orders/1
```

**Guide complet**: [TESTING_GUIDE.md](TESTING_GUIDE.md)

---

## 📊 Base de Données

### H2 Console

**URL**: `http://localhost:8080/h2-console`

**Paramètres de connexion**:
```
JDBC URL: jdbc:h2:mem:ordersdb
Username: sa
Password: (laisser vide)
```

### Schéma de Base de Données

```sql
┌─────────────┐       ┌─────────────┐       ┌─────────────┐
│   CLIENTS   │       │   ORDERS    │       │  PRODUCTS   │
├─────────────┤       ├─────────────┤       ├─────────────┤
│ id (PK)     │◄──────│ client_id   │       │ id (PK)     │
│ name        │       │ order_date  │       │ name        │
│ email       │       │ status      │       │ price       │
│ phone       │       │ source      │       │ stock_qty   │
│ address     │       │ total_amt   │       │ category    │
└─────────────┘       └─────────────┘       └─────────────┘
                            │                       ▲
                            │                       │
                            ▼                       │
                      ┌─────────────┐              │
                      │ ORDER_ITEMS │              │
                      ├─────────────┤              │
                      │ id (PK)     │              │
                      │ order_id    │──────────────┘
                      │ product_id  │
                      │ quantity    │
                      │ unit_price  │
                      └─────────────┘
```

### Requêtes SQL Utiles

```sql
-- Voir toutes les commandes avec clients
SELECT o.id, c.name, o.order_date, o.status, o.total_amount 
FROM orders o 
JOIN clients c ON o.client_id = c.id;

-- Voir les détails d'une commande
SELECT oi.*, p.name, p.price 
FROM order_items oi 
JOIN products p ON oi.product_id = p.id 
WHERE oi.order_id = 1;

-- Commandes par statut
SELECT status, COUNT(*) 
FROM orders 
GROUP BY status;
```

---

## 📚 Documentation

### Fichiers de Documentation

| Fichier | Description |
|---------|-------------|
| **README.md** | Ce fichier - Guide principal |
| **[ARCHITECTURE.md](ARCHITECTURE.md)** | Diagrammes d'architecture détaillés |
| **[TESTING_GUIDE.md](TESTING_GUIDE.md)** | Guide de test complet (1000+ lignes) |
| **[PROJECT_VERIFICATION.md](PROJECT_VERIFICATION.md)** | Vérification et statut du projet |
| **[ERREUR_RESOLVED.md](ERREUR_RESOLVED.md)** | Résolution des erreurs IntelliJ |

### Diagrammes

Voir **[ARCHITECTURE.md](ARCHITECTURE.md)** pour:
- ✅ Diagramme de composants
- ✅ Diagramme de séquence (création de commande)
- ✅ Diagramme de classes
- ✅ Architecture multi-canaux

---

## 🎯 Scénarios d'Utilisation

### Scénario 1: Site E-Commerce
```
Client Web → REST API → OrderService → BDD
                    ↓
                  gRPC → NotificationService
```

### Scénario 2: Application Mobile
```
App Mobile → GraphQL → OrderService → BDD
```

### Scénario 3: Partenaire B2B
```
Système Legacy → SOAP → OrderService → BDD
```

### Scénario 4: Microservice Interne
```
Microservice → gRPC → NotificationService
```

---

## 🛠️ Troubleshooting

### Port déjà utilisé

```powershell
# Trouver le processus
netstat -ano | findstr :8080

# Arrêter le processus (remplacer PID)
taskkill /PID <PID> /F
```

### Erreur `os.detected.classifier`

C'est normal! Voir [ERREUR_RESOLVED.md](ERREUR_RESOLVED.md). Maven compile correctement.

### Application ne démarre pas

```powershell
# Vérifier Java version
java -version  # Doit être >= 17

# Nettoyer et rebuilder
mvn clean install -U
```

---

## 📄 License

Ce projet est sous licence MIT. Voir le fichier [LICENSE](LICENSE) pour plus de détails.

---

## 👨‍💻 Auteur

**Projet Académique** - Gestion Unifiée des Commandes Multi-Canaux

Pour questions ou support, consulter la documentation dans `TESTING_GUIDE.md`.

---

**🚀 Prêt à démarrer? Lancer `mvn spring-boot:run` et ouvrir http://localhost:8080** 🎉

