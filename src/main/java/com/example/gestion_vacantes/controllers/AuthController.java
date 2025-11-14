package com.example.gestion_vacantes.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    @GetMapping("/login")
    public String mostrarFormularioLogin() {
        return "login";
    }

    @GetMapping("/registro/aspirante")
    public String mostrarFormularioAspirante() {
        return "registro-aspirante";
    }

    @GetMapping("/registro/empleador")
    public String mostrarFormularioEmpleador() {
        return "registro-empleador";
    }
}