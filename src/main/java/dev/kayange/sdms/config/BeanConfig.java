package dev.kayange.sdms.config;

import dev.kayange.sdms.constants.AuthorityConstants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class BeanConfig {

    @Bean
    public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(AuthorityConstants.PASSWORD_ENCODING_STRENGTH);}
}
