package com.example.gestioncommandes.grpc;

import com.example.gestioncommandes.grpc.NotificationResponse;
import com.example.gestioncommandes.grpc.NotificationServiceGrpc;
import com.example.gestioncommandes.grpc.OrderNotification;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.stereotype.Component;

@Component
public class NotificationGrpcClient {

    private final NotificationServiceGrpc.NotificationServiceBlockingStub stub;
    private final ManagedChannel channel;

    public NotificationGrpcClient() {
        this.channel = ManagedChannelBuilder
                .forAddress("localhost", 9090)
                .usePlaintext()
                .build();
        this.stub = NotificationServiceGrpc.newBlockingStub(channel);
    }

    public void notifyOrderCreated(Long orderId, Long clientId, String clientName,
                                   String status, Double totalAmount) {
        OrderNotification notification = OrderNotification.newBuilder()
                .setOrderId(orderId)
                .setClientId(clientId)
                .setClientName(clientName)
                .setStatus(status)
                .setTotalAmount(totalAmount)
                .setMessage("New order created successfully")
                .build();

        try {
            NotificationResponse response = stub.notifyOrderCreated(notification);
            System.out.println("gRPC Notification sent: " + response.getMessage());
        } catch (Exception e) {
            System.err.println("Failed to send gRPC notification: " + e.getMessage());
        }
    }

    public void notifyOrderStatusChanged(Long orderId, Long clientId, String clientName, String newStatus) {
        OrderNotification notification = OrderNotification.newBuilder()
                .setOrderId(orderId)
                .setClientId(clientId)
                .setClientName(clientName)
                .setStatus(newStatus)
                .setMessage("Order status updated to: " + newStatus)
                .build();

        try {
            NotificationResponse response = stub.notifyOrderStatusChanged(notification);
            System.out.println("gRPC Notification sent: " + response.getMessage());
        } catch (Exception e) {
            System.err.println("Failed to send gRPC notification: " + e.getMessage());
        }
    }

    public void shutdown() {
        if (channel != null) {
            channel.shutdown();
        }
    }
}

