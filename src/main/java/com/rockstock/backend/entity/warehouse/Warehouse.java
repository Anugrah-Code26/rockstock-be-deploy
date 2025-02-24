package com.rockstock.backend.entity.warehouse;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.rockstock.backend.entity.geolocation.City;
import com.rockstock.backend.entity.geolocation.SubDistrict;
import com.rockstock.backend.entity.order.Order;
import com.rockstock.backend.entity.stock.MutationJournal;
import com.rockstock.backend.entity.stock.WarehouseStock;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "warehouses", schema = "rockstock")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Warehouse {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "warehouse_id_gen")
    @SequenceGenerator(name = "warehouse_id_gen", sequenceName = "warehouse_id_seq", allocationSize = 1)
    @Column(name = "warehouse_id", nullable = false)
    private Long id;

    @NotNull
    @Column(nullable = false)
    private String name;

    @NotNull
    @Column(nullable = false)
    private String address;

    @NotNull
    @Column(nullable = false)
    private String longitude;

    @NotNull
    @Column(nullable = false)
    private String latitude;

    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt;

    @NotNull
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @NotNull
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updated_at", nullable = false)
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
    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_district_id", nullable = false)
    private SubDistrict subDistrict;

    @JsonManagedReference
    @OneToMany(mappedBy = "warehouse", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<WarehouseAdmin> warehouseAdmins = new HashSet<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "warehouse", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<WarehouseStock> warehouseStocks = new HashSet<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "originWarehouse", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<MutationJournal> originMutationJournals = new HashSet<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "destinationWarehouse", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<MutationJournal> destinationMutationJournals = new HashSet<>();

//    @JsonManagedReference
//    @OneToMany(mappedBy = "warehouse", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//    private Set<Order> orders = new HashSet<>();
}
