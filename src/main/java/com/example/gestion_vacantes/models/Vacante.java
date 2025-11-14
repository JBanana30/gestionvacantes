package com.example.gestion_vacantes.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "vacantes")
@Getter
@Setter
public class Vacante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(columnDefinition = "TEXT")
    private String requisitos;

    private String ubicacion;
    private String salario;

    @Column(name = "fecha_publicacion")
    private LocalDateTime fechaPublicacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empleador_id", nullable = false)
    private Empleador empleador; // <-- CAMBIO CLAVE: Ahora se relaciona con Empleador

    @OneToMany(mappedBy = "vacante", cascade = CascadeType.ALL)
    private List<Postulacion> postulaciones;

    @PrePersist
    public void prePersist() {
        fechaPublicacion = LocalDateTime.now();
    }
}