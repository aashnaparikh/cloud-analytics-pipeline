package com.cloudanalytics.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthRequest {
    @Email(message = "Valid email required")
    @NotBlank
    private String email;

    @NotBlank(message = "Password is required")
    private String password;
}
