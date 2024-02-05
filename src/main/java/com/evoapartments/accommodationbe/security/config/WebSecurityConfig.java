package com.evoapartments.accommodationbe.security.config;

import com.evoapartments.accommodationbe.security.jwt.AuthenticationTokenFilter;
import com.evoapartments.accommodationbe.security.jwt.JwtAuthenticationEntryPoint;
import com.evoapartments.accommodationbe.security.user.ApplicationUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@Configuration
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true, prePostEnabled = true)
public class WebSecurityConfig {
    private final ApplicationUserDetailsService userDetailsService;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Bean
    public DaoAuthenticationProvider authenticationProvider(){
        var authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public AuthenticationTokenFilter authenticationTokenFilter(){
        return new AuthenticationTokenFilter();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf(AbstractHttpConfigurer :: disable)
                .exceptionHandling(exceptionConfig -> exceptionConfig.authenticationEntryPoint(jwtAuthenticationEntryPoint))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorization -> authorization
                        .requestMatchers("/auth/**",
                                "/users/**",
                                "/accommodations/**",
                                "/reservations/**",
                                "/accommodation-types/**")
                        .permitAll()
                        .requestMatchers("/roles/**")
                        .hasRole("ADMIN")
                        .anyRequest()
                        .authenticated());
        httpSecurity.authenticationProvider(authenticationProvider());
        httpSecurity.addFilterBefore(authenticationTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }
}
