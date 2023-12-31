package com.egorbekherev.springsecurity.configuration;

import com.egorbekherev.springsecurity.configurer.HexConfigurer;
import com.egorbekherev.springsecurity.service.JdbcUserDetailsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerResponse;

import javax.sql.DataSource;
import java.util.Map;

@Configuration
@Slf4j
public class SpringSecurityConfiguration {

    @Bean
    public UserDetailsService userDetailsService(DataSource dataSource) {
        return new JdbcUserDetailsService(dataSource);
    }

//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        log.info("AM in securityFilterChain: {}", http.getSharedObject(AuthenticationManager.class));
//        http.apply(new MyConfigurer()).realmName("My custom realm name");
//        return http.build();
//    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
//                .addFilterBefore(new DeniedClientFilter(), DisableEncodeUrlFilter.class)
                .authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
                        .requestMatchers("/error").permitAll()
                        .anyRequest().authenticated())
                        .apply(new HexConfigurer());
        return http.build();
//                for debugging
//                .exceptionHandling(exceptionHandling -> exceptionHandling
//                        .accessDeniedHandler((request, response, accessDeniedException) -> {
//                            accessDeniedException.printStackTrace();
//                        }))
    }

//    basic authentication
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        BasicAuthenticationEntryPoint basicAuthenticationEntryPoint = new BasicAuthenticationEntryPoint();
//        basicAuthenticationEntryPoint.setRealmName("Realm");
//        return http
//                .httpBasic(httpBasic -> httpBasic.authenticationEntryPoint((request, response, authException) -> {
//                    authException.printStackTrace();
//                    basicAuthenticationEntryPoint.commence(request, response, authException);
//                }))
//                .authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests.anyRequest().authenticated())
//                .exceptionHandling(exceptionHandling -> exceptionHandling.authenticationEntryPoint(basicAuthenticationEntryPoint))
//                .build();
//    }

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
//                .authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
//                        .requestMatchers("/public/**").permitAll()
//                        .anyRequest().authenticated())
//                .exceptionHandling(exceptionHandling -> exceptionHandling
//                        .authenticationEntryPoint((request, response, authException) -> {
//                            response.sendRedirect("http://localhost:8080/public/403.html");
//                        }))
//                .build();
//    }
}
