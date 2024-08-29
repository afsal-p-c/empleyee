package com.example.employeemanagementsystem.controller;

import com.example.employeemanagementsystem.dto.LoginDTO;
import com.example.employeemanagementsystem.dto.TokenResponse;
import com.example.employeemanagementsystem.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Login", description = "Authenticate a user and return a JWT token")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginDTO loginDTO) {
        String token = authService.login(loginDTO);
        TokenResponse tokenResponse = new TokenResponse(token);
        return ResponseEntity.ok(tokenResponse);
    }
}