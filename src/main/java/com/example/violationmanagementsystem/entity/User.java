package com.example.violationmanagementsystem.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "national_id", nullable = false, unique = true, length = 10)
    private String nationalId;

    @NotNull
    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Size(max = 255)
    @NotNull
    @Column(name = "password", nullable = false)
    private String password;

    @Size(max = 100)
    @NotNull
    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @NotNull
    @Column(name = "enabled", nullable = false)
    private Boolean enabled;

    @ColumnDefault("1")
    @Column(name = "account_non_locked", nullable = false)
    private Boolean accountNonLocked;

    @CreationTimestamp
    @ColumnDefault("sysutcdatetime()")
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @ColumnDefault("sysutcdatetime()")
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @ManyToOne(
            cascade = {CascadeType.DETACH,
                    CascadeType.MERGE,
                    CascadeType.PERSIST,
                    CascadeType.REFRESH,}
            , fetch = FetchType.EAGER
    )
    @JoinColumn(name = "role_id")
    private Role role;

    public User() {
    }

    public User(String username, String nationalId, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.nationalId = nationalId;
    }

}
