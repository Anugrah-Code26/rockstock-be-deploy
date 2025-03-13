package com.rockstock.backend.entity.user;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.rockstock.backend.entity.cart.Cart;
import com.rockstock.backend.entity.geolocation.Address;
import com.rockstock.backend.entity.order.Order;
import com.rockstock.backend.entity.warehouse.Warehouse;
import com.rockstock.backend.entity.warehouse.WarehouseAdmin;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users", schema = "rockstock")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_id_gen")
    @SequenceGenerator(name = "user_id_gen", sequenceName = "user_id_seq", schema = "rockstock", allocationSize = 1)
    @Column(name = "user_id", nullable = false)
    private Long id;

    @NotNull(message = "Fullname is mandatory")
    @Size(max = 100, message = "Fullname can have at most 100 characters")
    @Column(nullable = false, length = 100)
    private String fullname;

    @NotNull(message = "Email is mandatory")
    @Email(message = "Email should be valid")
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Size(min = 6, message = "Password must be at least 6 characters")
    @Column
    private String password;

    @Column(name = "profile_picture_url")
    private String photoProfileUrl;

    @Column(name = "google_image_url")
    private String googleImageUrl;


    @Column(name = "birth_date")
    private OffsetDateTime birthDate;

    @Size(max = 10, message = "Gender can have at most 10 characters")
    @Column(length = 10)
    private String gender;

    @NotNull
    @Column(nullable = false)
    private Boolean isAdmin = false;

    @NotNull
    @ColumnDefault("false")
    @Column(name = "is_verified", nullable = false)
    private Boolean isVerified = false;

    @NotNull
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @NotNull
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt;

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

    @NotNull
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;


    // Relationships
    @JsonBackReference
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    @JsonBackReference
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "warehouse_admins", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "warehouse_id"))
    private Set<Warehouse> warehouses = new HashSet<>();

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_provider_id", referencedColumnName = "user_provider_id", nullable = true)
    private UserProvider userProvider;

    @JsonManagedReference
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<UserRole> userRoles = new HashSet<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Address> addresses = new HashSet<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<WarehouseAdmin> warehouseAdmins = new HashSet<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Cart> carts = new HashSet<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Order> orders = new HashSet<>();

}