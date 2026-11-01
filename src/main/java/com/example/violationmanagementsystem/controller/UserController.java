package com.example.violationmanagementsystem.controller;

import com.example.violationmanagementsystem.dto.request.AddUserRequest;
import com.example.violationmanagementsystem.dto.request.LoginRequest;
import com.example.violationmanagementsystem.dto.response.AddUserResponse;
import com.example.violationmanagementsystem.dto.response.LoginResponse;
import com.example.violationmanagementsystem.dto.response.UserResponse;
import com.example.violationmanagementsystem.entity.User;
import com.example.violationmanagementsystem.security.AppUserDetails;
import com.example.violationmanagementsystem.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest loginRequest) {
        return userService.login(loginRequest);
    }

    @GetMapping("/users")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ADMIN')")
    public Page<UserResponse> getUsers(@PageableDefault(size = 20) Pageable pageable) {
        return userService.getAllUsers(pageable);
    }

    @PostMapping("/users")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ADMIN')")
    public AddUserResponse addUser(@Valid @RequestBody AddUserRequest userRequest,
                                   @AuthenticationPrincipal AppUserDetails principal) {
        User currentUser = userService.findByUsername(principal.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated"));

        return userService.addUser(userRequest, currentUser);
    }


}
