package com.cloudanalytics.dto;

import com.cloudanalytics.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String tokenType;
    private long expiresIn;
    private UUID userId;
    private String email;
    private String firstName;
    private String lastName;
    private User.Role role;
    private String tenantId;
}
