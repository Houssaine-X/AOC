package com.example.gestioncommandes.repository;

import com.example.gestioncommandes.model.Order;
import com.example.gestioncommandes.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RepositoryRestResource(path = "orders")
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByClientId(Long clientId);

    List<Order> findByStatus(OrderStatus status);

    List<Order> findBySource(String source);

    @Query("SELECT o FROM Order o WHERE o.client.id = :clientId AND o.status = :status")
    List<Order> findByClientIdAndStatus(Long clientId, OrderStatus status);
}

