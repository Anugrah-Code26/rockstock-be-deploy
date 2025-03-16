package com.rockstock.backend.service.order.impl;

import com.midtrans.httpclient.error.MidtransError;
import com.rockstock.backend.common.exceptions.DataNotFoundException;
import com.rockstock.backend.common.utils.OrderCodeGenerator;
import com.rockstock.backend.common.utils.DistanceCalculator;
import com.rockstock.backend.entity.cart.Cart;
import com.rockstock.backend.entity.cart.CartItem;
import com.rockstock.backend.entity.geolocation.Address;
import com.rockstock.backend.entity.order.Order;
import com.rockstock.backend.entity.order.OrderStatusList;
import com.rockstock.backend.entity.payment.PaymentMethod;
import com.rockstock.backend.entity.user.User;
import com.rockstock.backend.entity.warehouse.Warehouse;
import com.rockstock.backend.infrastructure.address.repository.AddressRepository;
import com.rockstock.backend.infrastructure.cart.repository.CartItemRepository;
import com.rockstock.backend.infrastructure.cart.repository.CartRepository;
import com.rockstock.backend.infrastructure.order.dto.CreateOrderRequestDTO;
import com.rockstock.backend.infrastructure.order.repository.OrderRepository;
import com.rockstock.backend.infrastructure.payment.repository.PaymentMethodRepository;
import com.rockstock.backend.infrastructure.user.auth.security.Claims;
import com.rockstock.backend.infrastructure.user.repository.UserRepository;
import com.rockstock.backend.infrastructure.warehouse.repository.WarehouseRepository;
import com.rockstock.backend.service.cart.DeleteCartItemService;
import com.rockstock.backend.service.mutation.LockStockService;
import com.rockstock.backend.service.order.CreateOrderItemService;
import com.rockstock.backend.service.order.CreateOrderService;
import com.rockstock.backend.service.payment.MidtransPaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CreateOrderServiceImpl implements CreateOrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final WarehouseRepository warehouseRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final CreateOrderItemService createOrderItemService;
    private final DeleteCartItemService deleteCartItemService;
    private final MidtransPaymentService midtransPaymentService;
    private final LockStockService lockStockService;

    @Override
    @Transactional
    public Order createOrder(CreateOrderRequestDTO req) throws MidtransError {
        Long userId = Claims.getUserIdFromJwt();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User not found"));

        Cart cart = cartRepository.findActiveCartByUserId(userId);
        if (cart == null) {
            throw new DataNotFoundException("Cart not found ");
        }

        Address address = addressRepository.findById(req.getAddressId())
                .orElseThrow(() -> new DataNotFoundException("Address not found"));

        PaymentMethod paymentMethod = paymentMethodRepository.findById(req.getPaymentMethodId())
                .orElseThrow(() -> new DataNotFoundException("Payment Method not found"));

        Warehouse nearestWarehouse = findNearestWarehouse(address.getLatitude(), address.getLongitude());
        BigDecimal totalPrice = calculateTotalPrice(userId);

        Order order = req.toEntity(address);

        order.setDeliveryCost(req.getDeliveryCost());
        order.setTotalPrice(totalPrice);
        order.setTotalPayment(totalPrice.add(req.getDeliveryCost()));
        order.setUser(user);
        order.setWarehouse(nearestWarehouse);
        order.setStatus(OrderStatusList.WAITING_FOR_PAYMENT);

        // Save order first to get orderId
        Order savedOrder = orderRepository.save(order);

        // Generate and set order code using the utility
        String orderCode = OrderCodeGenerator.generateOrderCode(userId, savedOrder.getId(), LocalDate.now());
        savedOrder.setOrderCode(orderCode);

        if (!paymentMethod.getName().equals("Manual Bank Transfer")) {
            Map<String, String> midtransResponse = midtransPaymentService.createTransactionToken(savedOrder.getOrderCode(), savedOrder.getTotalPayment().doubleValue());
            savedOrder.setTransactionToken(midtransResponse.get("token"));
            savedOrder.setPaymentRedirectUrl(midtransResponse.get("redirect_url"));
        }

        savedOrder.setPaymentMethod(paymentMethod);

        // Save order again with order code & payment method
        orderRepository.save(savedOrder);

        lockStockService.lockStockForOrder(savedOrder.getId(), cart);

        // Save order items for history
        createOrderItemService.createOrderItem(savedOrder.getId());

        // Delete cart items
        deleteCartItemService.deleteByCartId(cart.getId());

        return savedOrder;
    }

    private Warehouse findNearestWarehouse(String addressLat, String addressLng) {
        List<Warehouse> warehouses = warehouseRepository.findAll();
        Warehouse nearestWarehouse = null;
        double minDistance = Double.MAX_VALUE;

        double lat1 = Double.parseDouble(addressLat);
        double lng1 = Double.parseDouble(addressLng);

        for (Warehouse warehouse : warehouses) {
            double lat2 = Double.parseDouble(warehouse.getLatitude());
            double lng2 = Double.parseDouble(warehouse.getLongitude());

            double distance = DistanceCalculator.haversine(lat1, lng1, lat2, lng2);
            if (distance < minDistance) {
                minDistance = distance;
                nearestWarehouse = warehouse;
            }
        }

        if (nearestWarehouse == null) {
            throw new RuntimeException("No warehouse found");
        }
        return nearestWarehouse;
    }

    private BigDecimal calculateTotalPrice(Long userId) {
        Cart cart = cartRepository.findActiveCartByUserId(userId);
        if (cart == null) {
            throw new DataNotFoundException("Cart not found ");
        }

        List<CartItem> cartItems = cartItemRepository.findAllByActiveCartId(cart.getId());
        if (cartItems.isEmpty()) {
            throw new DataNotFoundException("User still not have any items in the cart !");
        }

        return cartItems.stream()
                .map(CartItem::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
