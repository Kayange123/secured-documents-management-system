package dev.kayange.sdms.service;

import dev.kayange.sdms.domain.Token;
import dev.kayange.sdms.domain.TokenData;
import dev.kayange.sdms.dto.User;
import dev.kayange.sdms.enumeration.TokenType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Optional;
import java.util.function.Function;

public interface JwtService {
    String createToken(User user, Function<Token, String> tokenStringFunction);
    Optional<String> extractToken(HttpServletRequest request, String tokenType);
    void addCookie(HttpServletResponse response, User user, TokenType tokenType);
    <T> T getValueData(String token, Function<TokenData, T> tokenDataFunction);
    void removeCookie(HttpServletRequest request, HttpServletResponse response, String cookieName);
}
