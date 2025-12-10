package com.example.gestioncommandes.config;

import com.example.gestioncommandes.model.Client;
import com.example.gestioncommandes.model.Product;
import com.example.gestioncommandes.repository.ClientRepository;
import com.example.gestioncommandes.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    public void run(String... args) throws Exception {
        // Initialize Clients
        Client client1 = new Client();
        client1.setName("Jean Dupont");
        client1.setEmail("jean.dupont@email.com");
        client1.setPhone("+33 6 12 34 56 78");
        client1.setAddress("123 Rue de Paris, 75001 Paris");
        clientRepository.save(client1);

        Client client2 = new Client();
        client2.setName("Marie Martin");
        client2.setEmail("marie.martin@email.com");
        client2.setPhone("+33 6 98 76 54 32");
        client2.setAddress("456 Avenue des Champs, 69001 Lyon");
        clientRepository.save(client2);

        Client client3 = new Client();
        client3.setName("Pierre Dubois");
        client3.setEmail("pierre.dubois@email.com");
        client3.setPhone("+33 6 11 22 33 44");
        client3.setAddress("789 Boulevard Victor, 13001 Marseille");
        clientRepository.save(client3);

        // Initialize Products
        Product product1 = new Product();
        product1.setName("Laptop Dell XPS 15");
        product1.setDescription("High-performance laptop with Intel i7 processor");
        product1.setPrice(new BigDecimal("1299.99"));
        product1.setStockQuantity(50);
        product1.setCategory("Electronics");
        productRepository.save(product1);

        Product product2 = new Product();
        product2.setName("iPhone 15 Pro");
        product2.setDescription("Latest Apple smartphone with A17 chip");
        product2.setPrice(new BigDecimal("1199.99"));
        product2.setStockQuantity(100);
        product2.setCategory("Electronics");
        productRepository.save(product2);

        Product product3 = new Product();
        product3.setName("Samsung Galaxy S24");
        product3.setDescription("Android flagship smartphone");
        product3.setPrice(new BigDecimal("999.99"));
        product3.setStockQuantity(75);
        product3.setCategory("Electronics");
        productRepository.save(product3);

        Product product4 = new Product();
        product4.setName("Sony WH-1000XM5");
        product4.setDescription("Noise-cancelling wireless headphones");
        product4.setPrice(new BigDecimal("399.99"));
        product4.setStockQuantity(200);
        product4.setCategory("Audio");
        productRepository.save(product4);

        Product product5 = new Product();
        product5.setName("iPad Pro 12.9\"");
        product5.setDescription("Professional tablet with M2 chip");
        product5.setPrice(new BigDecimal("1099.99"));
        product5.setStockQuantity(60);
        product5.setCategory("Electronics");
        productRepository.save(product5);

        Product product6 = new Product();
        product6.setName("Logitech MX Master 3");
        product6.setDescription("Advanced wireless mouse for professionals");
        product6.setPrice(new BigDecimal("99.99"));
        product6.setStockQuantity(150);
        product6.setCategory("Accessories");
        productRepository.save(product6);

        System.out.println("===================================");
        System.out.println("Database initialized with sample data:");
        System.out.println("- 3 Clients");
        System.out.println("- 6 Products");
        System.out.println("===================================");
    }
}

