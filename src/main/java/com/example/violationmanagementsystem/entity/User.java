package com.example.violationmanagementsystem.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "national_id", nullable = false, unique = true, length = 10)
    private String nationalId;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, length = 255)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Column(nullable = false, unique = true, length = 100)
    private String email;
    @Column(name = "enabled", nullable = false)
    private boolean enabled;
    @Column(name = "account_non_locked", nullable = false)
    private boolean accountNonLocked;

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
