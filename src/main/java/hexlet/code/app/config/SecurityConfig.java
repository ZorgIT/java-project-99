package hexlet.code.app.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Autowired
    private JwtDecoder jwtDecoder;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/favicon.ico",
                                "/error",
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/webjars/**",
                                "/api/error"
                        ).permitAll()
                        .requestMatchers(
                                "/",
                                "/index.html",
                                "/assets/**",
                                "/swagger-ui/**",
                                "/v3/api-docs",
                                "/v3/api-docs/**")
                        .permitAll()
                        .requestMatchers(
                                "/api/login")
                        .permitAll()
                        .requestMatchers(HttpMethod.POST,
                                "/api/users")
                        .permitAll()
                        .requestMatchers(HttpMethod.GET,
                                "/api/task_statuses/**")
                        .authenticated()
                        .requestMatchers(HttpMethod.POST,
                                "/api/task_statuses").authenticated()
                        .requestMatchers(
                                "/api/users/**",
                                "/api/task_statuses")
                        .authenticated()
                        .anyRequest().authenticated())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2ResourceServer(rs -> rs.jwt(jwt -> jwt.decoder(jwtDecoder)))
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

}
