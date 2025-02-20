package com.rockstock.backend.entity.geolocation;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
@Table(name = "sub_districts", schema = "rockstock")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubDistrict {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sub_district_id_gen")
    @SequenceGenerator(name = "sub_district_id_gen", sequenceName = "sub_district_id_seq", schema = "rockstock", allocationSize = 1)
    @Column(name = "sub_district_id", nullable = false)
    private Long id;

    @NotNull
    @Column(nullable = false, length = 100)
    private String name;

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

    // Relationships
    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "district_id", nullable = false)
    private District district;

//    @JsonManagedReference
//    @OneToMany(mappedBy = "subDistrict", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//    private Set<Address> addresses = new HashSet<>();
}
