package be.vdab.firma.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;

@Configuration
class SecurityConfig {
    private final DataSource dataSource;

    SecurityConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    @Bean
    public JdbcUserDetailsManager maakPrincipals() {
        var manager = new JdbcUserDetailsManager(dataSource);
        manager.setUsersByUsernameQuery(
                """
                        select emailAdres as username, paswoord as password, true as enabled
                        from werknemers where emailAdres = ?
                        """
        );
        manager.setAuthoritiesByUsernameQuery("select ?, 'gebruiker'");
        return manager;
    }

    @Bean
    public SecurityFilterChain geefRechten(HttpSecurity http) throws Exception {
        http.formLogin();
        http.authorizeRequests(requests -> requests
                .mvcMatchers("/geluk").hasAuthority("gebruiker"));
        return http.build();
    }
}
