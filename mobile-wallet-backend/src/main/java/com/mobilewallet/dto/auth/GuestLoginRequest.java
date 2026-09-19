// FILE: src/main/java/com/mobilewallet/dto/auth/GuestLoginRequest.java
package com.mobilewallet.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GuestLoginRequest {

    @NotBlank(message = "Device ID is required")
    private String deviceId;

    private String deviceName;
    private String deviceModel;
}