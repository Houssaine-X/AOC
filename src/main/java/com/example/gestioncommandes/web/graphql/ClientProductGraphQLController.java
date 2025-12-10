package com.example.gestioncommandes.web.graphql;

import com.example.gestioncommandes.model.Client;
import com.example.gestioncommandes.model.Product;
import com.example.gestioncommandes.repository.ClientRepository;
import com.example.gestioncommandes.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;

@Controller
public class ClientProductGraphQLController {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ProductRepository productRepository;

    @QueryMapping
    public Client getClient(@Argument Long id) {
        return clientRepository.findById(id).orElse(null);
    }

    @QueryMapping
    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }

    @QueryMapping
    public Product getProduct(@Argument Long id) {
        return productRepository.findById(id).orElse(null);
    }

    @QueryMapping
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @MutationMapping
    public Client createClient(@Argument Map<String, Object> input) {
        Client client = new Client();
        client.setName((String) input.get("name"));
        client.setEmail((String) input.get("email"));
        client.setPhone((String) input.get("phone"));
        client.setAddress((String) input.get("address"));
        return clientRepository.save(client);
    }

    @MutationMapping
    public Product createProduct(@Argument Map<String, Object> input) {
        Product product = new Product();
        product.setName((String) input.get("name"));
        product.setDescription((String) input.get("description"));
        product.setPrice(new java.math.BigDecimal(input.get("price").toString()));
        product.setStockQuantity((Integer) input.get("stockQuantity"));
        product.setCategory((String) input.get("category"));
        return productRepository.save(product);
    }
}

