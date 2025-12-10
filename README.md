# Gestion Unifiée des Commandes Multi-Canaux

## Description du Projet

Système de gestion centralisée des commandes provenant de plusieurs sources (site web e-commerce, application mobile, partenaires B2B). Le projet expose ses fonctionnalités via plusieurs types d'API pour s'adapter aux besoins variés des clients.

## Technologies Utilisées

- **Spring Boot 3.2.0** - Framework principal
- **Spring Data JPA** - Persistance des données
- **Spring Data REST** - Exposition CRUD automatique
- **Spring Web Services** - Service SOAP
- **Spring GraphQL** - API GraphQL
- **gRPC** - Communication microservices performante
- **H2 Database** - Base de données en mémoire
- **Lombok** - Réduction du code boilerplate
- **Maven** - Gestion des dépendances

## Architecture

### Structure du Projet

```
src/main/java/com/example/gestioncommandes/
├── model/                  # Entités JPA
│   ├── Client.java
│   ├── Product.java
│   ├── Order.java
│   ├── OrderItem.java
│   └── OrderStatus.java
├── repository/             # Repositories Spring Data
│   ├── ClientRepository.java
│   ├── ProductRepository.java
│   ├── OrderRepository.java
│   └── OrderItemRepository.java
├── service/                # Logique métier
│   └── OrderService.java
├── dto/                    # Data Transfer Objects
│   ├── CreateOrderRequest.java
│   ├── OrderResponse.java
│   ├── OrderItemRequest.java
│   └── OrderItemResponse.java
├── web/
│   ├── rest/              # API REST
│   │   └── OrderRestController.java
│   ├── soap/              # Service SOAP
│   │   ├── WebServiceConfig.java
│   │   └── OrderSoapEndpoint.java
│   └── graphql/           # API GraphQL
│       ├── OrderGraphQLController.java
│       └── ClientProductGraphQLController.java
├── grpc/                  # Service gRPC
│   ├── NotificationServiceImpl.java
│   └── NotificationGrpcClient.java
└── config/
    └── DataInitializer.java
```

## Fonctionnalités

### Gestion des Entités
- **Clients** : Informations client (nom, email, téléphone, adresse)
- **Produits** : Catalogue de produits avec prix et stock
- **Commandes** : Commandes avec statut, source, et montant total
- **Lignes de commande** : Produits avec quantités et sous-totaux

### Services Métier
- Création de commandes avec validation de stock
- Liste des commandes par client
- Calcul du montant total d'une commande
- Mise à jour du statut d'une commande
- Gestion automatique des stocks

## Installation et Lancement

### Prérequis
- Java 17 ou supérieur
- Maven 3.6+
- Port 8080 disponible pour l'application
- Port 9090 disponible pour gRPC

### Compilation

```bash
mvn clean install
```

### Génération des classes SOAP et gRPC

```bash
# Génération des classes JAXB pour SOAP
mvn jaxb2:xjc

# Génération des classes gRPC
mvn protobuf:compile
mvn protobuf:compile-custom
```

### Lancement de l'application

```bash
mvn spring-boot:run
```

L'application démarre sur `http://localhost:8080`

## Utilisation des APIs

### 1. API REST

**Base URL**: `http://localhost:8080/api/rest/orders`

#### Créer une commande
```bash
POST http://localhost:8080/api/rest/orders
Content-Type: application/json

{
  "clientId": 1,
  "source": "e-commerce",
  "items": [
    {
      "productId": 1,
      "quantity": 2
    },
    {
      "productId": 4,
      "quantity": 1
    }
  ]
}
```

#### Récupérer toutes les commandes
```bash
GET http://localhost:8080/api/rest/orders
```

#### Récupérer une commande par ID
```bash
GET http://localhost:8080/api/rest/orders/1
```

#### Récupérer les commandes d'un client
```bash
GET http://localhost:8080/api/rest/orders/client/1
```

#### Calculer le total d'une commande
```bash
GET http://localhost:8080/api/rest/orders/1/total
```

#### Mettre à jour le statut d'une commande
```bash
PATCH http://localhost:8080/api/rest/orders/1/status?status=CONFIRMED
```

### 2. Spring Data REST

**Base URL**: `http://localhost:8080/api/data`

Accès CRUD automatique aux entités :
- `GET /api/data/clients` - Liste des clients
- `GET /api/data/clients/1` - Client par ID
- `POST /api/data/clients` - Créer un client
- `GET /api/data/products` - Liste des produits
- `GET /api/data/orders` - Liste des commandes

### 3. API GraphQL

**Endpoint**: `http://localhost:8080/graphql`  
**GraphiQL Interface**: `http://localhost:8080/graphiql`

#### Exemples de requêtes

**Récupérer une commande**
```graphql
query {
  getOrder(id: 1) {
    id
    clientName
    orderDate
    status
    totalAmount
    source
    items {
      productName
      quantity
      unitPrice
      subtotal
    }
  }
}
```

**Récupérer les commandes d'un client**
```graphql
query {
  getClientOrders(clientId: 1) {
    id
    orderDate
    status
    totalAmount
    items {
      productName
      quantity
    }
  }
}
```

**Créer une commande**
```graphql
mutation {
  createOrder(input: {
    clientId: 1
    source: "mobile"
    items: [
      { productId: 1, quantity: 1 },
      { productId: 2, quantity: 2 }
    ]
  }) {
    id
    clientName
    totalAmount
    status
  }
}
```

**Mettre à jour le statut**
```graphql
mutation {
  updateOrderStatus(orderId: 1, status: SHIPPED) {
    id
    status
    clientName
  }
}
```

**Récupérer tous les clients**
```graphql
query {
  getAllClients {
    id
    name
    email
    phone
  }
}
```

**Récupérer tous les produits**
```graphql
query {
  getAllProducts {
    id
    name
    price
    stockQuantity
    category
  }
}
```

### 4. Service SOAP

**WSDL**: `http://localhost:8080/ws/orders.wsdl`  
**Endpoint**: `http://localhost:8080/ws`

#### Exemple avec SoapUI ou outil similaire

**GetOrder Request**
```xml
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

**GetClientOrders Request**
```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:soap="http://example.com/gestioncommandes/soap">
   <soapenv:Header/>
   <soapenv:Body>
      <soap:getClientOrdersRequest>
         <soap:clientId>1</soap:clientId>
      </soap:getClientOrdersRequest>
   </soapenv:Body>
</soapenv:Envelope>
```

**CreateOrder Request**
```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:soap="http://example.com/gestioncommandes/soap">
   <soapenv:Header/>
   <soapenv:Body>
      <soap:createOrderRequest>
         <soap:clientId>1</soap:clientId>
         <soap:source>B2B</soap:source>
         <soap:items>
            <soap:productId>1</soap:productId>
            <soap:quantity>3</soap:quantity>
         </soap:items>
         <soap:items>
            <soap:productId>4</soap:productId>
            <soap:quantity>1</soap:quantity>
         </soap:items>
      </soap:createOrderRequest>
   </soapenv:Body>
</soapenv:Envelope>
```

### 5. Service gRPC

**Port**: `9090`

Le service gRPC est utilisé pour les notifications inter-microservices. Il est automatiquement appelé lors de :
- Création d'une commande
- Changement de statut d'une commande

Les logs des notifications apparaissent dans la console de l'application.

## Console H2

Accédez à la console H2 pour explorer la base de données :

**URL**: `http://localhost:8080/h2-console`

**Connexion**:
- JDBC URL: `jdbc:h2:mem:ordersdb`
- Username: `sa`
- Password: _(laisser vide)_

## Données de Test

L'application initialise automatiquement au démarrage :

### 3 Clients
1. Jean Dupont (jean.dupont@email.com)
2. Marie Martin (marie.martin@email.com)
3. Pierre Dubois (pierre.dubois@email.com)

### 6 Produits
1. Laptop Dell XPS 15 - 1299.99€
2. iPhone 15 Pro - 1199.99€
3. Samsung Galaxy S24 - 999.99€
4. Sony WH-1000XM5 - 399.99€
5. iPad Pro 12.9" - 1099.99€
6. Logitech MX Master 3 - 99.99€

## Tests avec Postman

Une collection Postman est recommandée pour tester facilement tous les endpoints. Voici les principales requêtes à inclure :

1. **Create Order** (POST)
2. **Get All Orders** (GET)
3. **Get Order by ID** (GET)
4. **Get Client Orders** (GET)
5. **Update Order Status** (PATCH)
6. **Get Clients** (GET - Spring Data REST)
7. **Get Products** (GET - Spring Data REST)

## Architecture en Couches

### Couche Présentation
- **REST Controllers** : Exposition HTTP classique
- **SOAP Endpoints** : Services web XML
- **GraphQL Controllers** : API flexible
- **gRPC Services** : Communication inter-services

### Couche Service Métier
- **OrderService** : Logique métier centralisée
- Validation des données
- Gestion des transactions
- Calculs métier

### Couche Accès aux Données
- **Repositories Spring Data** : Abstraction de la persistance
- Requêtes JPA automatiques et personnalisées
- Gestion des relations entre entités

## Principes d'Architecture

### Inversion de Contrôle (IoC)
- Utilisation intensive de l'injection de dépendances Spring
- `@Autowired` pour l'injection des composants
- Couplage faible entre les couches

### Séparation des Préoccupations
- Entités JPA distinctes des DTOs
- Services métier indépendants des contrôleurs
- Configuration externalisée

### Architecture Modulaire
- Chaque type d'API dans son propre package
- Réutilisation du service métier par toutes les API
- Extensibilité facilitée

## Points d'Extension

### Ajouter un nouveau canal
1. Créer un nouveau contrôleur dans le package approprié
2. Injecter `OrderService`
3. Mapper les requêtes/réponses selon le protocole

### Ajouter une nouvelle fonctionnalité métier
1. Ajouter la méthode dans `OrderService`
2. Exposer via les contrôleurs existants
3. Mettre à jour les schémas (XSD, GraphQL, Proto)

## Dépannage

### Port déjà utilisé
```bash
# Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# Ou modifier dans application.properties
server.port=8081
```

### Erreurs de compilation SOAP/gRPC
```bash
# Nettoyer et régénérer
mvn clean
mvn compile
```

### Base de données verrouillée
La base H2 se réinitialise à chaque démarrage. Modifier `application.properties` pour persister :
```properties
spring.jpa.hibernate.ddl-auto=update
spring.datasource.url=jdbc:h2:file:./data/ordersdb
```

## Auteur

Projet réalisé dans le cadre du cours d'architecture logicielle et intégration de services.

## Licence

Ce projet est à usage pédagogique.

