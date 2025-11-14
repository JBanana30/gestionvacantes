package com.example.gestion_vacantes.repositories;

import com.example.gestion_vacantes.models.Empleador;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EmpleadorRepository extends JpaRepository<Empleador, Long> {
    Optional<Empleador> findByCorreo(String correo);
}