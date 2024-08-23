package dev.kayange.sdms.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.kayange.sdms.domain.ApiAuthentication;
import dev.kayange.sdms.domain.ApiResponse;
import dev.kayange.sdms.domain.RequestUtil;
import dev.kayange.sdms.dto.LoginCredentials;
import dev.kayange.sdms.dto.User;
import dev.kayange.sdms.enumeration.LoginType;
import dev.kayange.sdms.enumeration.TokenType;
import dev.kayange.sdms.service.JwtService;
import dev.kayange.sdms.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.IOException;
import java.util.Map;

import static com.fasterxml.jackson.core.JsonParser.Feature.AUTO_CLOSE_SOURCE;
import static dev.kayange.sdms.domain.RequestUtil.getResponse;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
public class AuthenticationFilter extends AbstractAuthenticationProcessingFilter {
    private static final String LOGIN_PATH = "/auth/login";
    private final UserService userService;
    private final JwtService jwtService;

    public AuthenticationFilter(AuthenticationManager manager, UserService userService, JwtService jwtService) {
        super(new AntPathRequestMatcher(LOGIN_PATH, POST.name()), manager);
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        super.successfulAuthentication(request, response, chain, authResult);
        var user = (User) authResult.getPrincipal();
        userService.updateLoginAttempt(user.getEmail(), LoginType.LOGIN_SUCCESS);

        var httpResponse = user.isMfa() ? sendQrCode(request, user) : sendResponse(request, response, user);
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setStatus(OK.value());
        var outputStream = response.getOutputStream();
        var mapper = new ObjectMapper();
        mapper.writeValue(outputStream, httpResponse);
        outputStream.flush();
    }

    private ApiResponse sendResponse(HttpServletRequest request, HttpServletResponse response, User user) {
        jwtService.addCookie(response, user, TokenType.ACCESS);
        jwtService.addCookie(response, user, TokenType.REFRESH);
        return getResponse(request, Map.of("user", user), "Login successful", OK);
    }

    private ApiResponse sendQrCode(HttpServletRequest request, User user) {
        return getResponse(request, Map.of("user", user), "Please Enter QR code", OK);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        try{
            var user = new ObjectMapper().configure(AUTO_CLOSE_SOURCE, true)
                    .readValue(request.getInputStream(), LoginCredentials.class);
            userService.updateLoginAttempt(user.getEmail(), LoginType.LOGIN_ATTEMPT);
            var auth = ApiAuthentication.unauthenticated(user.getEmail(), user.getPassword());

            return getAuthenticationManager().authenticate(auth);
        }catch (Exception e){
            log.error("Error authenticating");
            RequestUtil.handleErrorResponse(request, response, e);
            return null;
        }

    }
}
