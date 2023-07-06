package com.egorbekherev.springsecurity.service;

import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.MappingSqlQuery;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Map;
import java.util.Optional;

public class JdbcUserDetailsService extends MappingSqlQuery<UserDetails>
        implements UserDetailsService {

    public JdbcUserDetailsService(DataSource dataSource) {
        super(
                dataSource, """
                                SELECT u.c_username, up.c_password, array_agg(ua.c_authority) as c_authorities 
                                FROM t_user u                           
                                LEFT JOIN t_user_password up on up.id_user = u.id
                                LEFT JOIN t_user_authority ua on ua.id_user = u.id
                                WHERE u.c_username = :username
                                GROUP BY u.id, up.id
                                """
        );
        this.declareParameter(new SqlParameter("username", Types.VARCHAR));
        this.compile();
    }

    @Override
    protected UserDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
        return User.builder()
                .username(rs.getString("c_username"))
                .password(rs.getString("c_password"))
                .authorities((String[]) rs.getArray("c_authorities").getArray())
                .build();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return Optional.ofNullable(this.findObjectByNamedParam(Map.of("username", username)))
                .orElseThrow(() -> new UsernameNotFoundException("Username %s not found".formatted(username)));
    }
}
