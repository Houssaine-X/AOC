package com.example.gestioncommandes.repository;

import com.example.gestioncommandes.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RepositoryRestResource(path = "products")
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategory(String category);
}

