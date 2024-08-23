package dev.kayange.sdms.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;

@Getter
@Setter
public class JwtConfig {

    @Value("${application.security.jwt.secret-key}")
    private String jwtSecret;
    @Value("${application.security.jwt.expiration}")
    private String jwtExpiration;
}
