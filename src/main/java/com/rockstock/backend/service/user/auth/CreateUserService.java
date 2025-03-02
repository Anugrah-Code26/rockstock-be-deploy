package com.rockstock.backend.infrastructure.user.auth;

import com.rockstock.backend.entity.user.User;
import com.rockstock.backend.infrastructure.user.auth.dto.CreateUserRequestDTO;
import com.rockstock.backend.infrastructure.user.dto.*;


public interface CreateUserService {
    User createUser(CreateUserRequestDTO req);
}
