package dev.kayange.sdms.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data @Builder
public class User implements Serializable {
    private Long id;
    private Long createdBy;
    private Long updatedBy;
    private String userId;
    private String firstName;
    private String lastName;
    private String email;
    private Integer loginAttempts;
    private String lastLogin;
    private String createdAt;
    private String updatedAt;
    private String role;
    private String authorities;
    private String phone;
    private String bio;
    private String imageUrl;
    private boolean accountNonExpired;
    private boolean credentialsNonExpired;
    protected boolean enabled;
    private boolean mfa;
    private boolean accountNonLocked;
    private String qrCodeImageUri;
}
