package dev.kayange.sdms.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Setter @Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserRequest {
    @NotEmpty(message = "First name can not be empty")
    private String firstName;
    @NotEmpty(message = "Last name can not be empty")
    private String lastName;
    @NotEmpty(message = "Email can not be empty or null")
    @Email(message = "Invalid email address")
    private String email;
    @NotEmpty(message = "Password can not be empty")
    private String password;
    private String phone;
    private String bio;
}
