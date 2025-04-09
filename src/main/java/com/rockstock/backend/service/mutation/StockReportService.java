package com.rockstock.backend.service.mutation;

import com.rockstock.backend.entity.stock.MutationJournal;
import com.rockstock.backend.infrastructure.mutationJournal.dto.StockSummaryResponse;
import com.rockstock.backend.infrastructure.mutationJournal.repository.StockReportRepository;
import com.rockstock.backend.infrastructure.user.auth.security.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StockReportService {

    private final StockReportRepository stockReportRepository;

    private void checkWarehouseAccess(Long warehouseId) {
//        String currentUserRole = Claims.getRoleFromJwt();
//
//        if (!"Super Admin".equalsIgnoreCase(currentUserRole) &&
//                !"Warehouse Admin".equalsIgnoreCase(currentUserRole)) {
//            throw new AccessDeniedException("Access Denied: Only Super Admin or Warehouse Admin can access this data.");
//        }
//
//        if ("Warehouse Admin".equalsIgnoreCase(currentUserRole)) {
//            List<Long> allowedWarehouseIds = Claims.getWarehouseIdsFromJwt();
//            if (warehouseId == null || !allowedWarehouseIds.contains(warehouseId)) {
//                throw new AccessDeniedException("Access Denied: You do not have permission to access this warehouse.");
//            }
//        }
    }

    public List<StockSummaryResponse> getMonthlySummary(Long warehouseId) {
        checkWarehouseAccess(warehouseId);
        return stockReportRepository.getProductMonthlySummary(warehouseId);
    }

    public List<StockSummaryResponse> getMonthlySummaryForProduct(Long warehouseId, Long productId) {
        checkWarehouseAccess(warehouseId);
        return stockReportRepository.getMonthlySummaryForProduct(warehouseId, productId);
    }

    public List<MutationJournal> getProductMutationsByMonth(Long warehouseId, Long productId, int year, int month) {
        checkWarehouseAccess(warehouseId);
        return stockReportRepository.getProductMutationsByMonth(warehouseId, productId, year, month);
    }
}