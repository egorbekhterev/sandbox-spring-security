package com.egorbekherev.springsecurity.configurer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

@Slf4j
public class MyConfigurer extends AbstractHttpConfigurer<MyConfigurer, HttpSecurity> {

    private String realmName = "My realm";

    public MyConfigurer realmName(String realmName) {
        this.realmName = realmName;
        return this;
    }

    @Override
    public void init(HttpSecurity builder) throws Exception {
        log.info("AM in init: {}", builder.getSharedObject(AuthenticationManager.class));
        builder.httpBasic(httpBasic -> httpBasic.realmName(this.realmName))
                .authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests.anyRequest().authenticated());
    }

    @Override
    public void configure(HttpSecurity builder) throws Exception {
        log.info("AM in configure: {}", builder.getSharedObject(AuthenticationManager.class));
    }
}
