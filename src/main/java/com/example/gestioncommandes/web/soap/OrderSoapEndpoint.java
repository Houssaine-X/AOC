package com.example.gestioncommandes.web.soap;

import com.example.gestioncommandes.model.OrderStatus;
import com.example.gestioncommandes.service.OrderService;
import com.example.gestioncommandes.soap.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import java.util.List;
import java.util.stream.Collectors;

@Endpoint
public class OrderSoapEndpoint {

    private static final String NAMESPACE_URI = "http://example.com/gestioncommandes/soap";

    @Autowired
    private OrderService orderService;

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getOrderRequest")
    @ResponsePayload
    public GetOrderResponse getOrder(@RequestPayload GetOrderRequest request) {
        com.example.gestioncommandes.dto.OrderResponse order = orderService.getOrderById(request.getOrderId());

        GetOrderResponse response = new GetOrderResponse();
        response.setOrder(convertToOrderInfo(order));
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getClientOrdersRequest")
    @ResponsePayload
    public GetClientOrdersResponse getClientOrders(@RequestPayload GetClientOrdersRequest request) {
        List<com.example.gestioncommandes.dto.OrderResponse> orders = orderService.getOrdersByClient(request.getClientId());

        GetClientOrdersResponse response = new GetClientOrdersResponse();
        orders.forEach(order -> response.getOrders().add(convertToOrderInfo(order)));
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "createOrderRequest")
    @ResponsePayload
    public CreateOrderResponse createOrder(@RequestPayload CreateOrderRequest request) {
        com.example.gestioncommandes.dto.CreateOrderRequest orderRequest = new com.example.gestioncommandes.dto.CreateOrderRequest();
        orderRequest.setClientId(request.getClientId());
        orderRequest.setSource(request.getSource());

        List<com.example.gestioncommandes.dto.OrderItemRequest> items = request.getItems().stream()
                .map(item -> {
                    com.example.gestioncommandes.dto.OrderItemRequest itemRequest = new com.example.gestioncommandes.dto.OrderItemRequest();
                    itemRequest.setProductId(item.getProductId());
                    itemRequest.setQuantity(item.getQuantity());
                    return itemRequest;
                })
                .collect(Collectors.toList());
        orderRequest.setItems(items);

        com.example.gestioncommandes.dto.OrderResponse order = orderService.createOrder(orderRequest);

        CreateOrderResponse response = new CreateOrderResponse();
        response.setOrder(convertToOrderInfo(order));
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "updateOrderStatusRequest")
    @ResponsePayload
    public UpdateOrderStatusResponse updateOrderStatus(@RequestPayload UpdateOrderStatusRequest request) {
        OrderStatus status = OrderStatus.valueOf(request.getStatus());
        com.example.gestioncommandes.dto.OrderResponse order = orderService.updateOrderStatus(request.getOrderId(), status);

        UpdateOrderStatusResponse response = new UpdateOrderStatusResponse();
        response.setOrder(convertToOrderInfo(order));
        return response;
    }

    private OrderInfo convertToOrderInfo(com.example.gestioncommandes.dto.OrderResponse order) {
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setId(order.getId());
        orderInfo.setClientId(order.getClientId());
        orderInfo.setClientName(order.getClientName());
        orderInfo.setOrderDate(order.getOrderDate().toString());
        orderInfo.setStatus(order.getStatus().toString());
        orderInfo.setTotalAmount(order.getTotalAmount());
        orderInfo.setSource(order.getSource());
        return orderInfo;
    }
}

