// FILE: src/main/java/com/mobilewallet/exception/ApiError.java
package com.mobilewallet.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiError {
    private boolean success;
    private String message;
    private String code;
    private int status;
    private LocalDateTime timestamp;
    private String path;
    private Map<String, String> errors;
}
