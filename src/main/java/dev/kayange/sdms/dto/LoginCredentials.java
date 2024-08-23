package dev.kayange.sdms.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoginCredentials {
    @NotEmpty(message = "Email can not be empty or null")
    @Email(message = "Invalid email address")
    private String email;
    @NotEmpty(message = "Password can not be empty")
    private String password;
}
