package com.rockstock.backend.infrastructure.util.security;

import com.rockstock.backend.entity.stock.MutationJournal;
import com.rockstock.backend.infrastructure.user.auth.security.Claims;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;

public class AuthorizationUtil {
    public static void validateOriginAuthorization(MutationJournal journal) {
        String userRole = Claims.getRoleFromJwt();

        if ("Super Admin".equals(userRole)) {
            return;
        } else if ("Warehouse Admin".equals(userRole)) {
            List<Long> userWarehouseIds = Claims.getWarehouseIdsFromJwt();
            Long originWarehouseId = journal.getOriginWarehouse().getId();

            if (!userWarehouseIds.contains(originWarehouseId)) {
                throw new AccessDeniedException("Access Denied: You do not have permission for this request.");
            }
        } else {
            throw new AccessDeniedException("Access Denied: Insufficient permissions.");
        }
    }

    public static void validateDestinationAuthorization(Long warehouseId) {
        String userRole = Claims.getRoleFromJwt();

        if ("Super Admin".equals(userRole)) {
            return;
        } else if ("Warehouse Admin".equals(userRole)) {
            List<Long> userWarehouseIds = Claims.getWarehouseIdsFromJwt();

            if (!userWarehouseIds.contains(warehouseId)) {
                throw new AccessDeniedException("Access Denied: You do not have permission for this destination warehouse.");
            }
        } else {
            throw new AccessDeniedException("Access Denied: Insufficient permissions.");
        }
    }
}
