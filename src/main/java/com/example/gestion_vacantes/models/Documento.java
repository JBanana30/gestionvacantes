package com.example.gestion_vacantes.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "documentos")
@Getter
@Setter
@NoArgsConstructor
public class Documento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String archivo;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    private String tipo; // Ej: "PDF", "Word", etc.

    private LocalDateTime fechaSubida = LocalDateTime.now();

    private boolean activo = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proyecto_id", nullable = false)
    private Proyecto proyecto;

    // El campo "autor" ha sido eliminado para coincidir con la nueva BD
}