package dev.kayange.sdms.domain;

import dev.kayange.sdms.dto.User;
import dev.kayange.sdms.exception.ApiException;
import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;

import java.util.Collection;

@Getter
public class ApiAuthentication extends AbstractAuthenticationToken {
    private static final String PASSWORD_PROTECTED = "PASSWORD_PROTECTED";
    private static final String EMAIL_PROTECTED = "EMAIL_PROTECTED";

    private User user;
    private String email;
    private String password;
    private boolean authenticated;

    private ApiAuthentication(User user, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.user = user;
        this.password = PASSWORD_PROTECTED;
        this.email = EMAIL_PROTECTED;
        this.authenticated = true;
    }

    private ApiAuthentication(String email, String password) {
        super(AuthorityUtils.NO_AUTHORITIES);
        this.password = password;
        this.email = email;
        this.authenticated = false;
    }

    public static ApiAuthentication unauthenticated(String email, String password) {
        return new ApiAuthentication(email, password);
    }

    public static ApiAuthentication authenticated(User user, Collection<? extends GrantedAuthority> authorities) {
        return new ApiAuthentication(user, authorities);
    }

    @Override
    public void setAuthenticated(boolean authenticated) {
        throw new ApiException("You cannot set authentication");
    }

    @Override
    public boolean isAuthenticated() {
        return this.authenticated;
    }

    @Override
    public String getCredentials() {
        return PASSWORD_PROTECTED;
    }

    @Override
    public User getPrincipal() {
        return this.user;
    }
}
