package com.example.gestion_vacantes.services;

import com.example.gestion_vacantes.models.Aspirante;
import com.example.gestion_vacantes.models.Empleador;
import com.example.gestion_vacantes.repositories.AspiranteRepository;
import com.example.gestion_vacantes.repositories.EmpleadorRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final AspiranteRepository aspiranteRepository;
    private final EmpleadorRepository empleadorRepository;

    public CustomUserDetailsService(AspiranteRepository aspiranteRepository, EmpleadorRepository empleadorRepository) {
        this.aspiranteRepository = aspiranteRepository;
        this.empleadorRepository = empleadorRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        // 1. Buscar primero en la tabla de aspirantes
        Optional<Aspirante> aspiranteOpt = aspiranteRepository.findByCorreo(correo);
        if (aspiranteOpt.isPresent()) {
            Aspirante aspirante = aspiranteOpt.get();
            List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_PROGRAMADOR"));
            return new User(aspirante.getCorreo(), aspirante.getPassword(), aspirante.isActivo(), true, true, true, authorities);
        }

        // 2. Si no es aspirante, buscar en la tabla de empleadores
        Optional<Empleador> empleadorOpt = empleadorRepository.findByCorreo(correo);
        if (empleadorOpt.isPresent()) {
            Empleador empleador = empleadorOpt.get();
            List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_STAKEHOLDER"));
            return new User(empleador.getCorreo(), empleador.getPassword(), empleador.isActivo(), true, true, true, authorities);
        }

        // 3. Si no se encuentra en ninguna tabla, lanzar excepción
        throw new UsernameNotFoundException("No se encontró un usuario con el correo: " + correo);
    }
}