package com.rockstock.backend.entity.stock;

import com.rockstock.backend.entity.warehouse.Warehouse;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @Column(name = "mutation_quantity", nullable = false)
    private Long mutationQuantity;

    @Column(name = "previous_stock_quantity")
    private Long previousStockQuantity;

    @Column(name = "new_stock_quantity")
    private Long newStockQuantity;

    @Size(min = 3, max = 100)
    @Column(length = 100)
    private String description;

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
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "warehouse_stock_id", nullable = false)
    private WarehouseStock warehouseStock;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "origin_warehouse_id")
    private Warehouse originWarehouse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_warehouse_id")
    private Warehouse destinationWarehouse;

    @Enumerated(EnumType.STRING)
    @Column(name = "stock_change_type", nullable = false)
    private StockChangeType stockChangeType;

    @Enumerated(EnumType.STRING)
    @Column(name = "adjustment_type", nullable = false)
    private StockAdjustmentType stockAdjustmentType;

    @Enumerated(EnumType.STRING)
    @Column(name = "mutation_status", nullable = false)
    private MutationStatus mutationStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "related_journal_id")
    private MutationJournal relatedJournal;

    @OneToMany(mappedBy = "relatedJournal", cascade = CascadeType.ALL)
    private List<MutationJournal> childJournals = new ArrayList<>();
}
