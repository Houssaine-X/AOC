package com.example.gestioncommandes.grpc;

import com.example.gestioncommandes.grpc.NotificationResponse;
import com.example.gestioncommandes.grpc.NotificationServiceGrpc;
import com.example.gestioncommandes.grpc.OrderNotification;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@GrpcService
public class NotificationServiceImpl extends NotificationServiceGrpc.NotificationServiceImplBase {

    private static final Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);

    @Override
    public void notifyOrderCreated(OrderNotification request, StreamObserver<NotificationResponse> responseObserver) {
        logger.info("=== Order Created Notification ===");
        logger.info("Order ID: {}", request.getOrderId());
        logger.info("Client: {} (ID: {})", request.getClientName(), request.getClientId());
        logger.info("Total Amount: ${}", request.getTotalAmount());
        logger.info("Status: {}", request.getStatus());
        logger.info("Message: {}", request.getMessage());
        logger.info("===================================");

        // Simulate notification processing (email, SMS, push notification, etc.)
        NotificationResponse response = NotificationResponse.newBuilder()
                .setSuccess(true)
                .setMessage("Order creation notification sent successfully")
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void notifyOrderStatusChanged(OrderNotification request, StreamObserver<NotificationResponse> responseObserver) {
        logger.info("=== Order Status Changed Notification ===");
        logger.info("Order ID: {}", request.getOrderId());
        logger.info("Client: {} (ID: {})", request.getClientName(), request.getClientId());
        logger.info("New Status: {}", request.getStatus());
        logger.info("Message: {}", request.getMessage());
        logger.info("=========================================");

        // Simulate notification processing
        NotificationResponse response = NotificationResponse.newBuilder()
                .setSuccess(true)
                .setMessage("Status change notification sent successfully")
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}

