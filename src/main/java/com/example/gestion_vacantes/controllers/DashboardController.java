package com.example.gestion_vacantes.controllers;

import com.example.gestion_vacantes.models.Aspirante;
import com.example.gestion_vacantes.models.Empleador;
import com.example.gestion_vacantes.repositories.AspiranteRepository;
import com.example.gestion_vacantes.repositories.EmpleadorRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.Optional;

@Controller
public class DashboardController {

    private final AspiranteRepository aspiranteRepository;
    private final EmpleadorRepository empleadorRepository;

    public DashboardController(AspiranteRepository aspiranteRepository, EmpleadorRepository empleadorRepository) {
        this.aspiranteRepository = aspiranteRepository;
        this.empleadorRepository = empleadorRepository;
    }

    @GetMapping("/dashboard")
    public String mostrarDashboard(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String correo = authentication.getName();

        // Buscamos si el usuario es un Aspirante
        Optional<Aspirante> aspiranteOpt = aspiranteRepository.findByCorreo(correo);
        if (aspiranteOpt.isPresent()) {
            model.addAttribute("perfil", aspiranteOpt.get());
            model.addAttribute("tipoUsuario", "Aspirante");
            model.addAttribute("mensaje", "¡Bienvenido Aspirante! Estamos contentos de tenerte aquí.");
            return "dashboard";
        }

        // Si no, buscamos si es un Empleador
        Optional<Empleador> empleadorOpt = empleadorRepository.findByCorreo(correo);
        if (empleadorOpt.isPresent()) {
            model.addAttribute("perfil", empleadorOpt.get());
            model.addAttribute("tipoUsuario", "Empleador");
            model.addAttribute("mensaje", "¡Bienvenido Empleador! Encuentra el talento que necesitas.");
            return "dashboard";
        }

        // Si no se encuentra, redirige al login (esto no debería pasar si la seguridad funciona)
        return "redirect:/login?error=true";
    }
}