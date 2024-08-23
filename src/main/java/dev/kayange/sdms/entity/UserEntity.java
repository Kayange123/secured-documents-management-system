package dev.kayange.sdms.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity @Table(name = "users")
@Setter @Getter @Builder @NoArgsConstructor @AllArgsConstructor
@ToString @JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class UserEntity extends Auditable{
    @Column(nullable = false, unique = true, updatable = false)
    private String userId;
    private String firstName;
    private String lastName;
    @Column(nullable = false, unique = true, updatable = false)
    private String email;
    private Integer loginAttempts;
    private LocalDateTime lastLogin;
    private String phone;
    private String bio;
    private String imageUrl;
    private boolean accountNonExpired;
    protected boolean enabled;
    private boolean mfa;
    private boolean accountNonLocked;
    @JsonIgnore
    private String qrCodeSecret;
    @Column(columnDefinition = "text")
    private String qrCodeImageUri;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id")
    )
    private RoleEntity role;

    public String getFullName() {
        return this.firstName + " " + this.lastName;
    }
}
