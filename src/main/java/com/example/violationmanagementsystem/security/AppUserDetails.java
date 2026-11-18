package com.example.violationmanagementsystem.security;

import com.example.violationmanagementsystem.entity.Role;
import com.example.violationmanagementsystem.entity.User;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class AppUserDetails implements UserDetails {

    private final User user;

    public AppUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Role role = user.getRole();
        if (role == null) {
            return List.of();
        }
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().getCode()));
    }

    @Override
    public @Nullable String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    public Long getId() {
        return user.getId();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // TODO:
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // TODO:
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // TODO:
    }

    @Override
    public boolean isEnabled() {
        return user.getEnabled();
    }
}

/**
 * @implNote UserDetailsService = Spring interface: "given a username, return a UserDetails".
 * CustomUserDetailsService = my implementation; loads User from DB, wraps it in UserPrincipal.
 * User = JPA entity (DB/business). UserPrincipal = adapter that makes User understandable to Spring Security.
 * Login: AuthenticationManager -> DaoAuthenticationProvider -> UserDetailsService + PasswordEncoder -> JWT issued.
 * Other requests: JwtFilter validates token -> sets SecurityContext -> authorizeHttpRequests checks access.
 */