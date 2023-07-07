package com.egorbekherev.springsecurity.converter;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.security.web.authentication.AuthenticationConverter;

import java.nio.charset.StandardCharsets;

public class HexAuthenticationConverter implements AuthenticationConverter {

    @Override
    public Authentication convert(HttpServletRequest request) {
        final String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorization != null && authorization.startsWith("Hex ")) {
            final String rawToken = authorization.replaceAll("^Hex ", "");
            final String token = new String(Hex.decode(rawToken), StandardCharsets.UTF_8);
            final String[] tokenParts = token.split(":");
            return UsernamePasswordAuthenticationToken
                    .unauthenticated(tokenParts[0], tokenParts[1]);
        }
        return null;
    }
}
