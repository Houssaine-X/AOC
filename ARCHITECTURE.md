# Documentation d'Architecture - Gestion Commandes Multi-Canaux

## 1. Diagramme de Composants

```
┌─────────────────────────────────────────────────────────────────────┐
│                        CLIENTS / CONSUMERS                           │
├─────────────┬──────────────┬──────────────┬────────────────────────┤
│  Web App    │  Mobile App  │  B2B Partner │  Internal Services     │
│  (Browser)  │              │              │  (Microservices)       │
└──────┬──────┴──────┬───────┴──────┬───────┴──────┬─────────────────┘
       │             │              │              │
       │ REST        │ GraphQL      │ SOAP         │ gRPC
       │             │              │              │
┌──────▼─────────────▼──────────────▼──────────────▼─────────────────┐
│                    PRESENTATION LAYER                               │
├─────────────┬──────────────┬──────────────┬────────────────────────┤
│   REST      │   GraphQL    │    SOAP      │       gRPC             │
│ Controllers │  Controllers │  Endpoints   │  Services              │
│             │              │              │                        │
│ @RestController           @Endpoint       @GrpcService             │
│ @RequestMapping           @PayloadRoot    NotificationService      │
└──────┬──────┴──────┬───────┴──────┬───────┴──────┬─────────────────┘
       │             │              │              │
       └─────────────┴──────────────┴──────────────┘
                            │
┌───────────────────────────▼─────────────────────────────────────────┐
│                      SERVICE LAYER                                  │
│                                                                     │
│  ┌────────────────────────────────────────────────────┐            │
│  │           OrderService (@Service)                   │            │
│  │                                                     │            │
│  │  - createOrder(CreateOrderRequest)                 │            │
│  │  - getOrdersByClient(Long clientId)                │            │
│  │  - getOrderById(Long orderId)                      │            │
│  │  - calculateOrderTotal(Long orderId)               │            │
│  │  - updateOrderStatus(Long id, OrderStatus)         │            │
│  │  - getAllOrders()                                  │            │
│  └────────────────────────────────────────────────────┘            │
│                                                                     │
│  Business Logic:                                                    │
│  - Validation des données                                           │
│  - Calcul des montants                                              │
│  - Gestion des stocks                                               │
│  - Gestion des transactions                                         │
└───────────────────────────▲─────────────────────────────────────────┘
                            │
                            │ Spring IoC / Dependency Injection
                            │
┌───────────────────────────▼─────────────────────────────────────────┐
│                    DATA ACCESS LAYER                                │
│                                                                     │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐             │
│  │   Client     │  │   Product    │  │    Order     │             │
│  │  Repository  │  │  Repository  │  │  Repository  │             │
│  │              │  │              │  │              │             │
│  │ @Repository  │  │ @Repository  │  │ @Repository  │             │
│  │ extends JPA  │  │ extends JPA  │  │ extends JPA  │             │
│  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘             │
│         │                 │                 │                      │
│         └─────────────────┴─────────────────┘                      │
└───────────────────────────▼─────────────────────────────────────────┘
                            │
┌───────────────────────────▼─────────────────────────────────────────┐
│                    PERSISTENCE LAYER                                │
│                                                                     │
│  ┌────────────────────────────────────────────────────┐            │
│  │              JPA / Hibernate                        │            │
│  └────────────────────────────────────────────────────┘            │
│                                                                     │
│  ┌────────────────────────────────────────────────────┐            │
│  │           H2 In-Memory Database                     │            │
│  │                                                     │            │
│  │  Tables: clients, products, orders, order_items    │            │
│  └────────────────────────────────────────────────────┘            │
└─────────────────────────────────────────────────────────────────────┘
```

## 2. Diagramme de Séquence - Création de Commande

### Scénario: Client crée une commande via REST API

```
┌────────┐      ┌──────────────┐      ┌──────────────┐      ┌─────────────┐      ┌──────────┐      ┌──────────┐
│ Client │      │ REST         │      │ Order        │      │ Repository  │      │ Database │      │ gRPC     │
│        │      │ Controller   │      │ Service      │      │             │      │          │      │ Client   │
└───┬────┘      └──────┬───────┘      └──────┬───────┘      └──────┬──────┘      └────┬─────┘      └────┬─────┘
    │                  │                     │                     │                   │                  │
    │ POST /orders     │                     │                     │                   │                  │
    ├─────────────────►│                     │                     │                   │                  │
    │ CreateOrderReq   │                     │                     │                   │                  │
    │                  │                     │                     │                   │                  │
    │                  │ createOrder(req)    │                     │                   │                  │
    │                  ├────────────────────►│                     │                   │                  │
    │                  │                     │                     │                   │                  │
    │                  │                     │ findById(clientId)  │                   │                  │
    │                  │                     ├────────────────────►│                   │                  │
    │                  │                     │                     │ SELECT * FROM... │                  │
    │                  │                     │                     ├──────────────────►│                  │
    │                  │                     │                     │                   │                  │
    │                  │                     │                     │ Client Entity     │                  │
    │                  │                     │                     ◄──────────────────┤                  │
    │                  │                     │ Client              │                   │                  │
    │                  │                     ◄─────────────────────┤                   │                  │
    │                  │                     │                     │                   │                  │
    │                  │                     │ findById(productId) │                   │                  │
    │                  │                     ├────────────────────►│                   │                  │
    │                  │                     │                     │ SELECT * FROM... │                  │
    │                  │                     │                     ├──────────────────►│                  │
    │                  │                     │                     │ Product Entity    │                  │
    │                  │                     │                     ◄──────────────────┤                  │
    │                  │                     │ Product             │                   │                  │
    │                  │                     ◄─────────────────────┤                   │                  │
    │                  │                     │                     │                   │                  │
    │                  │                     │ Validate Stock      │                   │                  │
    │                  │                     │ Calculate Total     │                   │                  │
    │                  │                     │ Update Stock        │                   │                  │
    │                  │                     │                     │                   │                  │
    │                  │                     │ save(order)         │                   │                  │
    │                  │                     ├────────────────────►│                   │                  │
    │                  │                     │                     │ INSERT INTO...    │                  │
    │                  │                     │                     ├──────────────────►│                  │
    │                  │                     │                     │ Order Saved       │                  │
    │                  │                     │                     ◄──────────────────┤                  │
    │                  │                     │ Order Entity        │                   │                  │
    │                  │                     ◄─────────────────────┤                   │                  │
    │                  │                     │                     │                   │                  │
    │                  │                     │ notifyOrderCreated()                   │                  │
    │                  │                     ├────────────────────────────────────────────────────────►│
    │                  │                     │                     │                   │                  │
    │                  │                     │                     │                   │  gRPC Call       │
    │                  │                     │                     │                   │  Notification    │
    │                  │                     │                     │                   │                  │
    │                  │                     │                  Response               │                  │
    │                  │                     ◄────────────────────────────────────────────────────────────┤
    │                  │ OrderResponse       │                     │                   │                  │
    │                  ◄─────────────────────┤                     │                   │                  │
    │  201 Created     │                     │                     │                   │                  │
    │  OrderResponse   │                     │                     │                   │                  │
    ◄──────────────────┤                     │                     │                   │                  │
    │                  │                     │                     │                   │                  │
```

## 3. Diagramme de Classes - Modèle de Domaine

```
┌────────────────────────┐
│       Client           │
├────────────────────────┤
│ - id: Long             │
│ - name: String         │
│ - email: String        │
│ - phone: String        │
│ - address: String      │
│ - orders: List<Order>  │
└───────────┬────────────┘
            │ 1
            │
            │ has
            │
            │ *
┌───────────▼────────────┐
│       Order            │
├────────────────────────┤
│ - id: Long             │
│ - client: Client       │
│ - orderDate: DateTime  │
│ - status: OrderStatus  │
│ - totalAmount: Decimal │
│ - source: String       │
│ - items: List<Item>    │
├────────────────────────┤
│ + addItem()            │
│ + removeItem()         │
│ + calculateTotal()     │
└───────────┬────────────┘
            │ 1
            │
            │ contains
            │
            │ *
┌───────────▼────────────┐         ┌────────────────────────┐
│     OrderItem          │         │      Product           │
├────────────────────────┤         ├────────────────────────┤
│ - id: Long             │         │ - id: Long             │
│ - order: Order         │ *    1  │ - name: String         │
│ - product: Product     ├─────────┤ - description: String  │
│ - quantity: Integer    │ refers  │ - price: Decimal       │
│ - unitPrice: Decimal   │   to    │ - stockQuantity: Int   │
│ - subtotal: Decimal    │         │ - category: String     │
├────────────────────────┤         └────────────────────────┘
│ + calculateSubtotal()  │
└────────────────────────┘

┌────────────────────────┐
│    <<enumeration>>     │
│     OrderStatus        │
├────────────────────────┤
│ PENDING                │
│ CONFIRMED              │
│ PROCESSING             │
│ SHIPPED                │
│ DELIVERED              │
│ CANCELLED              │
└────────────────────────┘
```

## 4. Architecture Multicouche

### 4.1 Couche Présentation (Presentation Layer)

**Responsabilités:**
- Exposer les services via différents protocoles
- Validation des entrées
- Transformation des données (DTO ↔ Entity)
- Gestion des erreurs HTTP/SOAP/GraphQL/gRPC

**Technologies:**
- REST: Spring MVC (@RestController)
- SOAP: Spring WS (@Endpoint)
- GraphQL: Spring GraphQL (@QueryMapping, @MutationMapping)
- gRPC: gRPC-Java (@GrpcService)

### 4.2 Couche Service (Business Layer)

**Responsabilités:**
- Logique métier
- Orchestration des opérations
- Validation métier
- Gestion des transactions
- Calculs et règles métier

**Composants:**
- OrderService: Service principal pour la gestion des commandes
- Utilisation de @Transactional pour garantir l'intégrité

### 4.3 Couche Accès aux Données (Data Access Layer)

**Responsabilités:**
- Abstraction de la persistance
- Requêtes de base de données
- Mapping objet-relationnel

**Technologies:**
- Spring Data JPA
- Repositories: ClientRepository, ProductRepository, OrderRepository

### 4.4 Couche Persistance (Persistence Layer)

**Responsabilités:**
- Stockage physique des données
- Gestion des transactions ACID
- Optimisation des requêtes

**Technologies:**
- JPA/Hibernate (ORM)
- H2 Database (mémoire)

## 5. Inversion de Contrôle (IoC) et Injection de Dépendances

### Principe IoC dans le projet

```java
// Le conteneur Spring gère le cycle de vie des composants

@RestController
public class OrderRestController {
    
    @Autowired  // Injection de dépendance
    private OrderService orderService;
    
    // Spring injecte automatiquement l'instance
}

@Service
public class OrderService {
    
    @Autowired  // Injection de dépendance
    private OrderRepository orderRepository;
    
    @Autowired
    private ClientRepository clientRepository;
    
    @Autowired
    private ProductRepository productRepository;
}

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    // Spring Data génère l'implémentation
}
```

### Avantages:
1. **Couplage faible**: Les composants ne créent pas leurs dépendances
2. **Testabilité**: Facilite les tests unitaires avec des mocks
3. **Maintenabilité**: Changement d'implémentation sans modifier les clients
4. **Réutilisabilité**: Les services sont partagés par toutes les API

## 6. Patterns Utilisés

### 6.1 Repository Pattern
- Abstraction de l'accès aux données
- Spring Data JPA génère les implémentations

### 6.2 Service Layer Pattern
- Encapsulation de la logique métier
- Réutilisation entre différents contrôleurs

### 6.3 DTO Pattern
- Séparation des modèles de domaine et de présentation
- CreateOrderRequest, OrderResponse, etc.

### 6.4 Builder Pattern
- Construction d'objets complexes (gRPC messages)

### 6.5 Facade Pattern
- OrderService agit comme une façade pour les repositories

## 7. Flux de Données

### REST → Service → Repository → Database

```
Client Request (JSON)
    ↓
REST Controller (@RestController)
    ↓
DTO (CreateOrderRequest)
    ↓
Service Layer (@Service)
    ↓  [Business Logic]
    ↓
Repository (@Repository)
    ↓  [JPA/Hibernate]
    ↓
Database (H2)
    ↓  [Persistence]
    ↑
Entity (Order)
    ↓
DTO (OrderResponse)
    ↓
JSON Response
```

## 8. Gestion des Transactions

```java
@Service
public class OrderService {
    
    @Transactional  // Gestion automatique des transactions
    public OrderResponse createOrder(CreateOrderRequest request) {
        // 1. Validation
        // 2. Mise à jour du stock
        // 3. Création de la commande
        // 4. Notification
        
        // Si une exception survient, tout est annulé (rollback)
    }
}
```

## 9. Sécurité et Extensions Possibles

### Extensions recommandées:
1. **Authentification JWT** pour les API REST/GraphQL
2. **WS-Security** pour SOAP
3. **SSL/TLS** pour gRPC
4. **Rate Limiting** pour prévenir les abus
5. **API Gateway** pour centraliser l'accès
6. **Service Discovery** (Eureka) pour le scaling
7. **Circuit Breaker** (Resilience4j) pour la résilience

## 10. Monitoring et Observabilité

### À implémenter:
- **Spring Boot Actuator**: Métriques et health checks
- **Logging**: SLF4J + Logback
- **Distributed Tracing**: Zipkin/Jaeger
- **Metrics**: Prometheus + Grafana

## Conclusion

Cette architecture permet:
- ✅ Séparation claire des responsabilités
- ✅ Testabilité et maintenabilité
- ✅ Extensibilité (ajout de nouveaux canaux facile)
- ✅ Réutilisation du code métier
- ✅ Support de multiples protocoles
- ✅ Couplage faible via IoC

