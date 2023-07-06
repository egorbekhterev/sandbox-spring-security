package com.egorbekherev.springsecurity.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerResponse;

import java.util.Map;

@Configuration
public class SpringSecurityConfiguration {

//    basic authentication
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        BasicAuthenticationEntryPoint basicAuthenticationEntryPoint = new BasicAuthenticationEntryPoint();
        basicAuthenticationEntryPoint.setRealmName("Realm");
        return http
                .httpBasic().authenticationEntryPoint((request, response, authException) -> {
                    authException.printStackTrace();
                    basicAuthenticationEntryPoint.commence(request, response, authException);
                }).and()
                .authorizeHttpRequests()
                    .anyRequest().authenticated().and()
                .exceptionHandling()
                .authenticationEntryPoint(basicAuthenticationEntryPoint).and()
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> routerFunction() {
        return RouterFunctions.route()
                .GET("/api/v4/greetings", request -> {
                    UserDetails userDetails = request
                            .principal()
                            .map(Authentication.class::cast)
                            .map(Authentication::getPrincipal)
                            .map(UserDetails.class::cast)
                            .orElseThrow();
                    return ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(Map.of("greeting", "Hello, %s! ".formatted(userDetails.getUsername())));
                }).build();
    }

//    @Bean
//    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        return http
//                .authorizeHttpRequests()
//                .requestMatchers("/public/**").permitAll()
//                .anyRequest().authenticated().and()
//                .exceptionHandling()
//                .authenticationEntryPoint(((request, response, authException) -> {
//                    response.sendRedirect("http://localhost:8080/public/403.html");
//                })).and()
//                .build();
//    }
}
