package com.rockstock.backend.infrastructure.mutationJournal.repository;

import com.rockstock.backend.entity.order.Order;
import com.rockstock.backend.entity.stock.MutationJournal;
import com.rockstock.backend.infrastructure.mutationJournal.dto.StockSummaryResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockReportRepository extends JpaRepository<Order, Long> {
    @Query("""
    SELECT NEW com.rockstock.backend.infrastructure.mutationJournal.dto.StockSummaryResponse(
        ws.product.id, ws.product.productName,
        EXTRACT(YEAR FROM mj.updatedAt),
        EXTRACT(MONTH FROM mj.updatedAt),
        SUM(CASE WHEN mj.stockAdjustmentType = 'POSITIVE' THEN mj.mutationQuantity ELSE 0 END),
        SUM(CASE WHEN mj.stockAdjustmentType = 'NEGATIVE' THEN mj.mutationQuantity ELSE 0 END),
        MAX(mj.newStockQuantity)
    )
    FROM MutationJournal mj
    JOIN mj.warehouseStock ws
    WHERE mj.mutationStatus = 'COMPLETED'
    AND ws.warehouse.id = :warehouseId
    GROUP BY ws.product.id, ws.product.productName, EXTRACT(YEAR FROM mj.updatedAt), EXTRACT(MONTH FROM mj.updatedAt)
    ORDER BY ws.product.productName
    """)
    List<StockSummaryResponse> getProductMonthlySummary(@Param("warehouseId") Long warehouseId);



    @Query("""
    SELECT NEW com.rockstock.backend.infrastructure.mutationJournal.dto.StockSummaryResponse(
        ws.product.id, ws.product.productName,
        EXTRACT(YEAR FROM mj.updatedAt),
        EXTRACT(MONTH FROM mj.updatedAt),
        SUM(CASE WHEN mj.stockAdjustmentType = 'POSITIVE' THEN mj.mutationQuantity ELSE 0 END),
        SUM(CASE WHEN mj.stockAdjustmentType = 'NEGATIVE' THEN mj.mutationQuantity ELSE 0 END),
        MAX(mj.newStockQuantity)
    )
    FROM MutationJournal mj
    JOIN mj.warehouseStock ws
    WHERE mj.mutationStatus = 'COMPLETED'
    AND ws.warehouse.id = :warehouseId
    AND ws.product.id = :productId
    GROUP BY EXTRACT(YEAR FROM mj.updatedAt), EXTRACT(MONTH FROM mj.updatedAt)
    ORDER BY EXTRACT(YEAR FROM mj.updatedAt), EXTRACT(MONTH FROM mj.updatedAt)
    """)
    List<StockSummaryResponse> getMonthlySummaryForProduct(@Param("warehouseId") Long warehouseId, @Param("productId") Long productId);


    @Query("""
    SELECT mj
    FROM MutationJournal mj
    JOIN mj.warehouseStock ws
    WHERE mj.mutationStatus = 'COMPLETED'
    AND ws.warehouse.id = :warehouseId
    AND ws.product.id = :productId
    AND EXTRACT(YEAR FROM mj.updatedAt) = :year
    AND EXTRACT(MONTH FROM mj.updatedAt) = :month
    ORDER BY mj.updatedAt DESC
    """)
    List<MutationJournal> getProductMutationsByMonth(
            @Param("warehouseId") Long warehouseId,
            @Param("productId") Long productId,
            @Param("year") int year,
            @Param("month") int month
    );
}
