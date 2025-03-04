package com.rockstock.backend.entity.stock;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.rockstock.backend.entity.warehouse.Warehouse;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.OffsetDateTime;

@Entity
@Table(name = "mutation_journals", schema = "rockstock")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MutationJournal {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mutation_journal_id_gen")
    @SequenceGenerator(name = "mutation_journal_id_gen", sequenceName = "mutation_journal_id_seq", schema = "rockstock", allocationSize = 1)
    @Column(name = "mutation_journal_id", nullable = false)
    private Long id;

    @Column(nullable = false)
    private Long mutationQuantity;

    @Column(name = "previous_stock_quantity", nullable = false)
    private Long previousStockQuantity;

    @Column(name = "new_stock_quantity", nullable = false)
    private Long newStockQuantity;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "mutation_status", nullable = false)
    private MutationStatus mutationStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "stock_change_type", nullable = false)
    private StockChangeType stockChangeType;

    @Column(name = "created_at", nullable = false)
    @ColumnDefault("CURRENT_TIMESTAMP")
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    @ColumnDefault("CURRENT_TIMESTAMP")
    private OffsetDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now();
        updatedAt = OffsetDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }

    // Relationships
    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "warehouse_stock_id", nullable = false)
    private WarehouseStock warehouseStock;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "origin_warehouse_id")
    private Warehouse originWarehouse;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_warehouse_id")
    private Warehouse destinationWarehouse;}
