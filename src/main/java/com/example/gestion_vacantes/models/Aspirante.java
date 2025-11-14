package com.example.gestion_vacantes.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "aspirantes")
@Getter
@Setter
public class Aspirante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String correo;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String nombreCompleto;

    @Column(unique = true)
    private String nombreUsuario;

    // --- NUEVO CAMPO AÑADIDO ---
    @Column(columnDefinition = "TEXT") // Coincide con el tipo de la BD
    private String habilidades; // Guardaremos las habilidades como texto separado por comas, ej: "Java, Spring, SQL"

    private boolean activo = true;

    @Column(updatable = false) // Para que no se actualice en cada guardado
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @OneToMany(mappedBy = "aspirante", cascade = CascadeType.ALL)
    private List<Postulacion> postulaciones;
}