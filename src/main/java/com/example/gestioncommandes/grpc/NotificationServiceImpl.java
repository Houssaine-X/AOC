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
        // Format and log the notification
        String notification = String.format("""
                
                ===================================
                === Order Created Notification ===
                ===================================
                Order ID: %d
                Client: %s (ID: %d)
                Total Amount: $%.2f
                Status: %s
                Message: %s
                ===================================
                """,
                request.getOrderId(),
                request.getClientName(),
                request.getClientId(),
                request.getTotalAmount(),
                request.getStatus(),
                request.getMessage());

        logger.info(notification);

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
        // Format and log the notification
        String notification = String.format("""
                
                =========================================
                === Order Status Changed Notification ===
                =========================================
                Order ID: %d
                Client: %s (ID: %d)
                New Status: %s
                Message: %s
                =========================================
                """,
                request.getOrderId(),
                request.getClientName(),
                request.getClientId(),
                request.getStatus(),
                request.getMessage());

        logger.info(notification);

        // Simulate notification processing
        NotificationResponse response = NotificationResponse.newBuilder()
                .setSuccess(true)
                .setMessage("Status change notification sent successfully")
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}

