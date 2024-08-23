package dev.kayange.sdms.service.implementation;

import dev.kayange.sdms.domain.Token;
import dev.kayange.sdms.domain.TokenData;
import dev.kayange.sdms.dto.User;
import dev.kayange.sdms.enumeration.TokenType;
import dev.kayange.sdms.function.TriConsumer;
import dev.kayange.sdms.security.JwtConfig;
import dev.kayange.sdms.service.JwtService;
import dev.kayange.sdms.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import static dev.kayange.sdms.constants.AuthorityConstants.*;
import static dev.kayange.sdms.enumeration.TokenType.ACCESS;
import static dev.kayange.sdms.enumeration.TokenType.REFRESH;
import static java.util.Arrays.stream;
import static org.springframework.security.core.authority.AuthorityUtils.commaSeparatedStringToAuthorityList;

@Service
@Slf4j
@RequiredArgsConstructor
public class JwtServiceImpl extends JwtConfig implements JwtService {
    private final UserService userService;

    private final Supplier<SecretKey> key = ()-> Keys.hmacShaKeyFor(Decoders.BASE64.decode(getJwtSecret()));
    private final Function<String, Claims> claimsFunction = token -> Jwts.parser().verifyWith(key.get()).build().parseSignedClaims(token).getPayload();
    private final Function<String, String> subject = token -> claimsFunction.apply(token).getSubject();
    private final BiFunction<HttpServletRequest, String, Optional<String>> extractToken = (request, cookieName)->
            Optional.of(stream(request.getCookies() == null ? new Cookie[]{new Cookie(EMPTY_VALUE, EMPTY_VALUE)} : request.getCookies())
                            .filter(cookie -> Objects.equals(cookieName, cookie.getName()))
                            .map(Cookie::getValue).findAny())
                    .orElse(Optional.empty());

    private final BiFunction<HttpServletRequest, String, Optional<Cookie>> extractCookie = (request, cookieName) ->
            Optional.of(stream(request.getCookies() == null ? new Cookie[]{new Cookie(EMPTY_VALUE, EMPTY_VALUE)} : request.getCookies())
                    .filter(cookie -> Objects.equals(cookieName, cookie.getName()))
                            .findAny()).orElse(Optional.empty());

    private final Supplier<JwtBuilder> builder = ()-> Jwts.builder()
            .header().add(Map.of("typ", "JWT"))
            .and().audience().add(KAYANGE_STORE)
            .and().id(UUID.randomUUID().toString())
            .issuedAt(Date.from(Instant.now()))
            .notBefore(new Date())
            .signWith(key.get(), Jwts.SIG.HS512);

    private final BiFunction<User, TokenType, String> buildToken = (user, type)-> Objects.equals(type, ACCESS) ? builder.get().subject(user.getUserId())
            .claim(AUTHORITIES, user.getAuthorities())
            .claim(ROLE, user.getRole())
            .expiration(Date.from(Instant.now().plusSeconds(Long.parseLong(getJwtExpiration()))))
            .compact() : builder.get().id(user.getUserId()).expiration(Date.from(Instant.now()
            .plusSeconds(Long.parseLong(getJwtExpiration())))).compact();

    private final TriConsumer<HttpServletResponse, User, TokenType> addCookie = (response, user, type) -> {
        switch (type) {
            case ACCESS -> {
                String accessToken = createToken(user, Token::getAccess);
                var cookie = new Cookie(type.name(), accessToken);
                cookie.setHttpOnly(true);
                //cookie.setSecure(true);
                cookie.setMaxAge(2*60);
                cookie.setPath("/");
                cookie.setAttribute("SameSite", org.springframework.boot.web.server.Cookie.SameSite.NONE.name());
                response.addCookie(cookie);
            }
            case REFRESH -> {
                String refreshToken = createToken(user, Token::getRefresh);
                var cookie = new Cookie(type.name(), refreshToken);
                cookie.setHttpOnly(true);
                //cookie.setSecure(true);
                cookie.setMaxAge(2*60*60);
                cookie.setPath("/");
                cookie.setAttribute("SameSite", org.springframework.boot.web.server.Cookie.SameSite.NONE.name());
                response.addCookie(cookie);
            }
            default -> {}
        }
    };

    private <T> T getClaimsValue(String token, Function<Claims, T> claims){
        return claimsFunction.andThen(claims).apply(token);
    }

    public final Function<String, List<GrantedAuthority>> authorities = token ->
            commaSeparatedStringToAuthorityList(new StringJoiner(AUTHORITY_DELIMITER)
                    .add(claimsFunction.apply(token).get(AUTHORITIES, String.class))
                    .add(ROLE_PREFIX + claimsFunction.apply(token).get(ROLE, String.class)).toString());
    @Override
    public String createToken(User user, Function<Token, String> tokenStringFunction) {
        var token = Token.builder().access(buildToken.apply(user, ACCESS)).refresh(buildToken.apply(user, REFRESH)).build();
        return tokenStringFunction.apply(token);
    }

    @Override
    public Optional<String> extractToken(HttpServletRequest request, String tokenType) {
        return extractToken.apply(request, tokenType);
    }

    @Override
    public void addCookie(HttpServletResponse response, User user, TokenType tokenType) {
        addCookie.accept(response, user, tokenType);

    }

    @Override
    public <T> T getValueData(String token, Function<TokenData, T> tokenDataFunction) {
        TokenData tokenData = TokenData.builder()
                .valid(Objects.equals(userService.getUserByUserId(subject.apply(token)).getUserId(), claimsFunction.apply(token).getSubject()))
                .claims(claimsFunction.apply(token)).authorities(authorities.apply(token))
                .user(userService.getUserByUserId(subject.apply(token)))
                .build();
        return tokenDataFunction.apply(tokenData);
    }

    @Override
    public void removeCookie(HttpServletRequest request, HttpServletResponse response, String cookieName) {
        var optionalCookie = extractCookie.apply(request, cookieName);
        if(optionalCookie.isPresent()){
            Cookie cookie = optionalCookie.get();
            cookie.setMaxAge(0);
            response.addCookie(cookie);
        }
    }
}
