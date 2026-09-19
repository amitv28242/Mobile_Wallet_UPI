// FILE: src/main/java/com/mobilewallet/controller/AuthController.java
package com.mobilewallet.controller;

import com.mobilewallet.dto.auth.*;
import com.mobilewallet.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "Authentication and authorization APIs")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Register a new user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "User registered successfully"),
        @ApiResponse(responseCode = "409", description = "Username/email/phone already exists"),
        @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(
            @Valid @RequestBody RegisterRequest request) {

        log.info("Registration request received for username={}, role={}",
                request.getUsername(), request.getRole());

        RegisterResponse response = authService.register(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "Login user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login successful"),
        @ApiResponse(responseCode = "401", description = "Invalid credentials"),
        @ApiResponse(responseCode = "403", description = "Account locked or disabled")
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        log.info("Login request for: {}", request.getUsernameOrEmail());
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Refresh access token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Token refreshed successfully"),
        @ApiResponse(responseCode = "401", description = "Invalid or expired refresh token")
    })
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        log.info("Refresh token request");
        AuthResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Logout user", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Logout successful"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(@RequestHeader(value = "X-Refresh-Token", required = false) String refreshToken) {
        log.info("Logout request");
        authService.logout(refreshToken);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Logout successful");
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Generate OTP")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OTP sent successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid identifier or purpose")
    })
    @PostMapping("/otp/generate")
    public ResponseEntity<OTPResponse> generateOTP(@Valid @RequestBody OTPGenerateRequest request) {
        log.info("OTP generation request for: {}", request.getIdentifier());
        OTPResponse response = authService.generateOTP(request.getIdentifier(), request.getPurpose());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Verify OTP")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OTP verified successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid or expired OTP")
    })
    @PostMapping("/otp/verify")
    public ResponseEntity<Map<String, Boolean>> verifyOTP(@Valid @RequestBody OTPRequest request) {
        log.info("OTP verification request for: {}", request.getIdentifier());
        boolean verified = authService.verifyOTP(request);
        Map<String, Boolean> response = new HashMap<>();
        response.put("verified", verified);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Resend OTP")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OTP resent successfully"),
        @ApiResponse(responseCode = "429", description = "Too many requests")
    })
    @PostMapping("/otp/resend")
    public ResponseEntity<OTPResponse> resendOTP(@Valid @RequestBody OTPResendRequest request) {
        log.info("OTP resend request for: {}", request.getIdentifier());
        OTPResponse response = authService.resendOTP(request.getIdentifier(), request.getPurpose());
        return ResponseEntity.ok(ApiResponse.<OTPResponse>builder()
                .success(true)
                .message("OTP resent successfully")
                .data(response)
                .timestamp(LocalDateTime.now())
                .build());
    }

    @Operation(summary = "Guest login")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Guest login successful"),
        @ApiResponse(responseCode = "403", description = "Guest login disabled")
    })
    @PostMapping("/guest")
    public ResponseEntity<AuthResponse> guestLogin(@Valid @RequestBody GuestLoginRequest request) {
        log.info("Guest login request from device: {}", request.getDeviceId());
        AuthResponse response = authService.guestLogin(request);
        return ResponseEntity.ok(ApiResponse.<AuthResponse>builder()
                .success(true)
                .message("Guest login successful")
                .data(response)
                .timestamp(LocalDateTime.now())
                .build());
    }

    @Operation(summary = "Validate token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Token is valid"),
        @ApiResponse(responseCode = "401", description = "Token is invalid")
    })
    @PostMapping("/validate")
    public ResponseEntity<Map<String, Boolean>> validateToken(@RequestHeader("Authorization") String token) {
        String jwt = token.replace("Bearer ", "");
        boolean valid = authService.validateToken(jwt);
        Map<String, Boolean> response = new HashMap<>();
        response.put("valid", valid);
        return ResponseEntity.ok(response);
    }
}