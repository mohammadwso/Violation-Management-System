package com.example.violationmanagementsystem.service;

import com.example.violationmanagementsystem.dto.request.AddUserRequest;
import com.example.violationmanagementsystem.dto.request.LoginRequest;
import com.example.violationmanagementsystem.dto.response.AddUserResponse;
import com.example.violationmanagementsystem.dto.response.LoginResponse;
import com.example.violationmanagementsystem.dto.response.UserResponse;
import com.example.violationmanagementsystem.entity.Role;
import com.example.violationmanagementsystem.entity.User;
import com.example.violationmanagementsystem.exception.BadRequestException;
import com.example.violationmanagementsystem.exception.ConflictException;
import com.example.violationmanagementsystem.exception.ForbiddenException;
import com.example.violationmanagementsystem.repo.RoleRepo;
import com.example.violationmanagementsystem.repo.UserRepo;
import com.example.violationmanagementsystem.security.AppUserDetails;
import com.example.violationmanagementsystem.security.JWTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepo userRepo;
    @Autowired
    private RoleRepo roleRepo;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    JWTService jwtService;
    @Autowired
    PasswordEncoder passwordEncoder;

    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(
                        request.username(), request.password()));

        AppUserDetails details = (AppUserDetails) authentication.getPrincipal();
        String token = jwtService.generateToken(details.getId());

        return new LoginResponse(token, "Bearer", jwtService.getExpirationSeconds());
    }

    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return userRepo.findAll(pageable).map(UserResponse::from);
    }

    @Transactional
    public AddUserResponse addUser(AddUserRequest userRequest, User currentUser) {
        Role newRole = roleRepo.findById(userRequest.roleId())
                .orElseThrow(() -> new BadRequestException("Role does not exists"));

        String username = userRequest.username().trim();
        String email = userRequest.email().trim().toLowerCase();
        String nationalId = userRequest.nationalId().trim();

        if (userRepo.existsByUsername(username))
            throw new ConflictException("username already taken");
        if (userRepo.existsByEmail(email))
            throw new ConflictException("email already registered");
        if (userRepo.existsByNationalId(nationalId))
            throw new ConflictException("National ID already registered");
        if (newRole.getLevel() >= currentUser.getRole().getLevel()) {
            throw new ForbiddenException("Not allowed to assign this role");
        }
        User user = new User(
                userRequest.username(),
                userRequest.nationalId(),
                passwordEncoder.encode(userRequest.password()),
                userRequest.email()
        );
        user.setRole(newRole);
        user.setEnabled(true);
        user.setAccountNonLocked(true);

        User savedUser;
        try {
            savedUser = userRepo.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("User already exists");
        }

        return new AddUserResponse(true, savedUser.getId());
    }

    public Optional<User> findByUsername(String name) {
        return userRepo.findUserByUsername(name);
    }
}
