package com.example.gestion_vacantes.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "postulaciones")
@Getter
@Setter
public class Postulacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cv_path")
    private String cvPath; // Ruta al archivo PDF del CV

    @Column(name = "fecha_postulacion")
    private LocalDateTime fechaPostulacion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoPostulacion estado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aspirante_id", nullable = false)
    private Aspirante aspirante; // <-- CAMBIO CLAVE: Ahora se relaciona con Aspirante

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vacante_id", nullable = false)
    private Vacante vacante;

    @PrePersist
    public void prePersist() {
        fechaPostulacion = LocalDateTime.now();
        if (estado == null) {
            estado = EstadoPostulacion.PENDIENTE;
        }
    }
}