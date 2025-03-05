<<<<<<<< HEAD:src/main/java/com/rockstock/backend/entity/payment/PaymentMethod.java
package com.rockstock.backend.entity.payment;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.rockstock.backend.entity.order.Order;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
========
package com.rockstock.backend.entity.user;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
>>>>>>>> origin/backup-dev:src/main/java/com/rockstock/backend/entity/user/UserRole.java
import org.hibernate.annotations.ColumnDefault;

import java.time.OffsetDateTime;

@Entity
<<<<<<<< HEAD:src/main/java/com/rockstock/backend/entity/payment/PaymentMethod.java
@Table(name = "payment_methods", schema = "rockstock")
========
@Table(name = "user_roles", schema = "rockstock")
>>>>>>>> origin/backup-dev:src/main/java/com/rockstock/backend/entity/user/UserRole.java
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
<<<<<<<< HEAD:src/main/java/com/rockstock/backend/entity/payment/PaymentMethod.java
public class PaymentMethod {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "payment_method_id_gen")
    @SequenceGenerator(name = "payment_method_id_gen", sequenceName = "payment_method_id_seq", schema = "rockstock", allocationSize = 1)
    @Column(name = "payment_method_id", nullable = false)
    private Long id;

    @NotNull
    @Column(nullable = false, length = 50)
    private String name;
========

public class UserRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_role_id")
    private Long id;

    @NotNull
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @NotNull
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
>>>>>>>> origin/backup-dev:src/main/java/com/rockstock/backend/entity/user/UserRole.java

    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt;

<<<<<<<< HEAD:src/main/java/com/rockstock/backend/entity/payment/PaymentMethod.java
    @NotNull
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @NotNull
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

========
>>>>>>>> origin/backup-dev:src/main/java/com/rockstock/backend/entity/user/UserRole.java
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
<<<<<<<< HEAD:src/main/java/com/rockstock/backend/entity/payment/PaymentMethod.java
}
========

    // Relationships
    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "role_id", referencedColumnName = "role_id")
    private Role role;

}
>>>>>>>> origin/backup-dev:src/main/java/com/rockstock/backend/entity/user/UserRole.java
