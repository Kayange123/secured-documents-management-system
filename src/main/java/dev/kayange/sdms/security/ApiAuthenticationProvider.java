package dev.kayange.sdms.security;

import dev.kayange.sdms.domain.ApiAuthentication;
import dev.kayange.sdms.domain.UserPrincipal;
import dev.kayange.sdms.exception.ApiException;
import dev.kayange.sdms.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.function.Consumer;

import static dev.kayange.sdms.constants.AuthorityConstants.NINETY_DAYS;

@Component
@RequiredArgsConstructor
public class ApiAuthenticationProvider implements AuthenticationProvider {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        var auth = (ApiAuthentication) authentication;
        var user = userService.getUserByEmail(auth.getEmail());
        if(user != null) {
            var credentials = userService.getCredentialsByUserId(user.getId());
                if(credentials.getUpdatedAt().minusDays(NINETY_DAYS).isAfter(LocalDateTime.now())) throw new ApiException("Credentials are expired Please reset your password!");
                var userPrincipal = new UserPrincipal(user, credentials);
                validAccount.accept(userPrincipal);
                if(passwordEncoder.matches(auth.getPassword(), userPrincipal.getPassword())){
                    return ApiAuthentication.authenticated(user, userPrincipal.getAuthorities());
                }else {
                    throw new BadCredentialsException("Email or Password is incorrect");
                }
        }else{
            throw new ApiException("User NOT found");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return ApiAuthentication.class.isAssignableFrom(authentication);
    }

    private final Consumer<UserPrincipal> validAccount = userPrincipal -> {
        if(!userPrincipal.isEnabled()) {throw new DisabledException("Account is Currently disabled");}
        if(userPrincipal.isAccountNonLocked()) {throw new LockedException("Account is Currently Locked. Please contact administrator");}
        if(userPrincipal.isCredentialsNonExpired()) {throw new CredentialsExpiredException("Credentials expired. Please reset your password");}
        if(userPrincipal.isAccountNonExpired()) {throw new DisabledException("Your Account has expired. Please contact administrator");}
    };
}
