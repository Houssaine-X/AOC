package com.example.gestioncommandes.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {
    private Long clientId;
    private List<OrderItemRequest> items;
    private String source; // e-commerce, mobile, B2B
}

