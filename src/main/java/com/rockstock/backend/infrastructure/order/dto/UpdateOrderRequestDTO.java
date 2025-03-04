package com.rockstock.backend.infrastructure.order.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class UpdateOrderRequestDTO {
    private MultipartFile paymentProof;
}
