package com.rockstock.backend.service.order.impl;

import com.cloudinary.Cloudinary;
import com.rockstock.backend.common.exceptions.DataNotFoundException;
import com.rockstock.backend.entity.order.Order;
import com.rockstock.backend.entity.order.OrderStatusList;
import com.rockstock.backend.infrastructure.order.dto.UpdateOrderRequestDTO;
import com.rockstock.backend.infrastructure.order.repository.OrderRepository;
import com.rockstock.backend.infrastructure.order.repository.OrderStatusRepository;
import com.rockstock.backend.infrastructure.user.auth.security.Claims;
import com.rockstock.backend.service.cloudinary.DeleteCloudinaryService;
import com.rockstock.backend.service.order.UpdateOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.cloudinary.utils.ObjectUtils;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UpdateOrderServiceImpl implements UpdateOrderService {

    private final Cloudinary cloudinary;
    private final OrderRepository orderRepository;
    private final OrderStatusRepository orderStatusRepository;
    private final DeleteCloudinaryService deleteCloudinaryService;

    @Transactional
    public Order updateOrderStatus(OrderStatusList newStatus, Long orderId, UpdateOrderRequestDTO req) {
        String userRole = Claims.getRoleFromJwt();
        System.out.println(userRole);


        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new DataNotFoundException("Order not found"));

        OrderStatusList status = order.getOrderStatus().getStatus();

        assert userRole != null;
        switch (status) {
            case WAITING_FOR_PAYMENT -> {
                if (newStatus == OrderStatusList.CANCELED) {
                    if (userRole.equals("Customer")) {
                        order.setOrderStatus(orderStatusRepository.findByStatus(newStatus));
                    } else {
                        throw new IllegalStateException("Unauthorized to cancel order");
                    }
                } else if (newStatus == OrderStatusList.PAYMENT_VERIFICATION) {
                    if (order.getPaymentMethod().getName().equals("Manual Bank Transfer") && req.getPaymentProof() != null) {
                        try {
                            String imageUrl = uploadToCloudinary(req.getPaymentProof());
                            order.setPaymentProof(imageUrl);
                            order.setOrderStatus(orderStatusRepository.findByStatus(newStatus));
                        } catch (IOException e) {
                            throw new RuntimeException("Error uploading file to Cloudinary", e);
                        }
                    } else {
                        throw new IllegalArgumentException("Payment proof required for manual bank transfer");
                    }
                } else if (newStatus == OrderStatusList.PROCESSING) {
                    if (!order.getPaymentMethod().getName().equals("Manual Bank Transfer")) {
                        order.setOrderStatus(orderStatusRepository.findByStatus(newStatus));

                        // ADD AUTOMATIC STOCK MUTATION CODE HERE

                    } else {
                        throw new IllegalArgumentException("Invalid payment method or payment not completed");
                    }
                }
            }
            case PAYMENT_VERIFICATION -> {
                if (userRole.equals("Super Admin")) {
                    if (newStatus == OrderStatusList.WAITING_FOR_PAYMENT) {
                        if (order.getPaymentProof() != null) {
                            deleteCloudinaryService.deleteFromCloudinary(order.getPaymentProof());
                            order.setPaymentProof(null);
                        }
                        order.setOrderStatus(orderStatusRepository.findByStatus(newStatus));
                    } else if (newStatus == OrderStatusList.PROCESSING) {
                        order.setOrderStatus(orderStatusRepository.findByStatus(newStatus));

                        // ADD AUTOMATIC STOCK MUTATION CODE HERE

                    }
                } else {
                    throw new IllegalStateException("Only Super Admin can approve or reject payments");
                }
            }
            case PROCESSING -> {
                if (userRole.equals("Super Admin") && newStatus == OrderStatusList.CANCELED) {
                    order.setOrderStatus(orderStatusRepository.findByStatus(newStatus));
                } else if (userRole.equals("Super Admin") && newStatus == OrderStatusList.ON_DELIVERY) {

                    // CODE to Check Warehouse Stock quantity = Order Quantity

                    order.setOrderStatus(orderStatusRepository.findByStatus(newStatus));
                } else {
                    throw new IllegalStateException("Only Super Admin can move order to ON_DELIVERY or CANCELED");
                }
            }
            case ON_DELIVERY -> {
                if (userRole.equals("Customer") && newStatus == OrderStatusList.COMPLETED) {
                    order.setOrderStatus(orderStatusRepository.findByStatus(newStatus));
                } else {
                    throw new IllegalStateException("Order cannot be marked as completed yet");
                }
            }
            default -> throw new IllegalArgumentException("Invalid status transition");
        }
        return orderRepository.save(order);
    }

    private boolean isPaymentSuccessful(Order order) {
        // Implement actual payment gateway verification logic here
        return true; // Assume success for now
    }

    private boolean isValidFileType(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && (contentType.contains("jpeg") || contentType.contains("jpg") ||
                contentType.contains("png"));
    }

    private String uploadToCloudinary(MultipartFile file) throws IOException {
        if (!isValidFileType(file)) {
            throw new IllegalArgumentException("Invalid file type. Only .jpg, .jpeg, .png allowed.");
        }
        if (file.getSize() > 1 * 1024 * 1024) { // 1MB limit
            throw new IllegalArgumentException("File size must be less than 1MB.");
        }
        Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
        return (String) uploadResult.get("secure_url");
    }
}
