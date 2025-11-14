package com.example.gestion_vacantes.repositories;

import com.example.gestion_vacantes.models.Postulacion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PostulacionRepository extends JpaRepository<Postulacion, Long> {
    // Añade este método para buscar todas las postulaciones de un aspirante
    List<Postulacion> findByAspiranteId(Long aspiranteId);
}