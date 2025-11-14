package com.example.gestion_vacantes.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        // Permite el acceso público a estas rutas, incluyendo la de los CVs
                        .requestMatchers("/", "/login", "/registro/**", "/css/**", "/js/**", "/images/**", "/uploads/**").permitAll()

                        // Rutas específicas para el Empleador (STAKEHOLDER)
                        .requestMatchers("/vacantes/nueva", "/vacantes/mis-vacantes", "/vacantes/{id}/aspirantes").hasAuthority("ROLE_STAKEHOLDER")

                        // Rutas específicas para el Aspirante (PROGRAMADOR)
                        .requestMatchers("/vacantes/{id}/postular").hasAuthority("ROLE_PROGRAMADOR")

                        // Cualquier usuario autenticado puede ver la lista general de vacantes
                        .requestMatchers("/vacantes").authenticated()

                        // Cualquier otra petición requiere autenticación
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/dashboard", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout=true")
                        .permitAll()
                );

        return http.build();
    }
}