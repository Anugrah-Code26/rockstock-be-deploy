package com.rockstock.backend.service.cart.impl;

import com.rockstock.backend.common.exceptions.DataNotFoundException;
import com.rockstock.backend.entity.cart.Cart;
import com.rockstock.backend.entity.user.User;
import com.rockstock.backend.infrastructure.cart.repository.CartRepository;
import com.rockstock.backend.infrastructure.user.auth.security.Claims;
import com.rockstock.backend.infrastructure.user.repository.UserRepository;
import com.rockstock.backend.service.cart.CreateCartService;
import com.rockstock.backend.common.exceptions.DuplicateDataException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateCartServiceImpl implements CreateCartService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public Cart createCart() {

        Long userId = Claims.getUserIdFromJwt();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User not found"));

        Cart checkActiveCart = cartRepository.findActiveCartByUserId(userId);
        if (checkActiveCart != null) {
            throw new DuplicateDataException("Active cart is already exist !");
        }

        Cart newCart = new Cart();

        newCart.setUser(user);
        newCart.setIsActive(true);

        return cartRepository.save(newCart);
    }
}
