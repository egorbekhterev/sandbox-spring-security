package com.egorbekherev.springsecurity.configurer;

import com.egorbekherev.springsecurity.converter.HexAuthenticationConverter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AuthenticationEntryPointFailureHandler;
import org.springframework.security.web.authentication.AuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

public class HexConfigurer extends AbstractHttpConfigurer<HexConfigurer, HttpSecurity> {

    private AuthenticationEntryPoint authenticationEntryPoint =
            (request, response, authException) -> {
        response.addHeader(HttpHeaders.WWW_AUTHENTICATE, "Hex");
        response.sendError(HttpStatus.UNAUTHORIZED.value());
    };

    @Override
    public void init(HttpSecurity builder) throws Exception {
        builder.exceptionHandling(exceptionHandling -> exceptionHandling
                .authenticationEntryPoint(this.authenticationEntryPoint));
    }

    @Override
    public void configure(HttpSecurity builder) throws Exception {
        final AuthenticationManager authenticationManager = builder.getSharedObject(AuthenticationManager.class);
        final AuthenticationFilter authenticationFilter = new AuthenticationFilter(authenticationManager,
                new HexAuthenticationConverter());
        authenticationFilter.setSuccessHandler((request, response, authentication) -> {});
        authenticationFilter.setFailureHandler(new AuthenticationEntryPointFailureHandler(
                this.authenticationEntryPoint));
        builder.addFilterBefore(authenticationFilter, BasicAuthenticationFilter.class);
    }

    public HexConfigurer authenticationEntryPoint(AuthenticationEntryPoint authenticationEntryPoint) {
        this.authenticationEntryPoint = authenticationEntryPoint;
        return this;
    }
}
