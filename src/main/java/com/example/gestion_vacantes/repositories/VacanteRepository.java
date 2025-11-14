package com.example.gestion_vacantes.repositories;

import com.example.gestion_vacantes.models.Vacante;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface VacanteRepository extends JpaRepository<Vacante, Long> {
    List<Vacante> findByEmpleadorId(Long empleadorId); // Este método sigue siendo válido
}