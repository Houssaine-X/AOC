package com.example.gestioncommandes.web.graphql;

import com.example.gestioncommandes.dto.CreateOrderRequest;
import com.example.gestioncommandes.dto.OrderResponse;
import com.example.gestioncommandes.model.OrderStatus;
import com.example.gestioncommandes.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.math.BigDecimal;
import java.util.List;

@Controller
public class OrderGraphQLController {

    @Autowired
    private OrderService orderService;

    @QueryMapping
    public OrderResponse getOrder(@Argument Long id) {
        return orderService.getOrderById(id);
    }

    @QueryMapping
    public List<OrderResponse> getClientOrders(@Argument Long clientId) {
        return orderService.getOrdersByClient(clientId);
    }

    @QueryMapping
    public List<OrderResponse> getAllOrders() {
        return orderService.getAllOrders();
    }

    @QueryMapping
    public BigDecimal getOrderTotal(@Argument Long id) {
        return orderService.calculateOrderTotal(id);
    }

    @MutationMapping
    public OrderResponse createOrder(@Argument CreateOrderRequest input) {
        return orderService.createOrder(input);
    }

    @MutationMapping
    public OrderResponse updateOrderStatus(@Argument Long orderId, @Argument OrderStatus status) {
        return orderService.updateOrderStatus(orderId, status);
    }
}

