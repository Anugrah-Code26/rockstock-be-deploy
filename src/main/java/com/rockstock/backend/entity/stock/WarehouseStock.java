package com.rockstock.backend.entity.stock;

import com.rockstock.backend.entity.product.Product;
import com.rockstock.backend.entity.warehouse.Warehouse;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
//@EntityListeners(WarehouseStockListener.class)
@Table(name = "warehouse_stocks", schema = "rockstock")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseStock {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "warehouse_stock_id_gen")
    @SequenceGenerator(name = "warehouse_stock_id_gen", sequenceName = "warehouse_stock_id_seq", schema = "rockstock", allocationSize = 1)
    @Column(name = "warehouse_stock_id", nullable = false)
    private Long id;

    @Column(name = "stock_quantity", nullable = false)
    private Long stockQuantity;

    @Column(name = "locked_quantity")
    private Long lockedQuantity;

    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt;

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

    @PreRemove
    protected void onRemove() {
        deletedAt = OffsetDateTime.now();
    }

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @OneToMany(mappedBy = "warehouseStock", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<MutationJournal> mutationJournals = new HashSet<>();
}
