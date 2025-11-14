package com.example.gestion_vacantes.controllers;

import com.example.gestion_vacantes.models.Aspirante;
import com.example.gestion_vacantes.models.Empleador;
import com.example.gestion_vacantes.repositories.AspiranteRepository;
import com.example.gestion_vacantes.repositories.EmpleadorRepository;
import com.example.gestion_vacantes.services.EmailService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes; // <-- 1. IMPORTAR

@Controller
@RequestMapping("/registro")
public class RegistroController {

    private final AspiranteRepository aspiranteRepository;
    private final EmpleadorRepository empleadorRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public RegistroController(AspiranteRepository aspiranteRepository,
                              EmpleadorRepository empleadorRepository,
                              PasswordEncoder passwordEncoder,
                              EmailService emailService) {
        this.aspiranteRepository = aspiranteRepository;
        this.empleadorRepository = empleadorRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @PostMapping("/aspirante")
    public String procesarRegistroAspirante(
            @RequestParam String nombreCompleto,
            @RequestParam String correo,
            @RequestParam String password,
            @RequestParam String nombreUsuario,
            @RequestParam String habilidades,
            RedirectAttributes attributes) { // <-- 2. AÑADIR RedirectAttributes

        // --- INICIO DE VALIDACIÓN DE CORREO ---
        if (empleadorRepository.findByCorreo(correo).isPresent()) {
            attributes.addFlashAttribute("error_correo", "Este correo ya está registrado como Empleador.");
            return "redirect:/registro/aspirante";
        }
        if (aspiranteRepository.findByCorreo(correo).isPresent()) {
            attributes.addFlashAttribute("error_correo", "Este correo ya está en uso.");
            return "redirect:/registro/aspirante";
        }
        // --- FIN DE VALIDACIÓN DE CORREO ---

        Aspirante nuevoAspirante = new Aspirante();
        nuevoAspirante.setNombreCompleto(nombreCompleto);
        nuevoAspirante.setCorreo(correo);
        nuevoAspirante.setPassword(passwordEncoder.encode(password));
        nuevoAspirante.setNombreUsuario(nombreUsuario);
        nuevoAspirante.setHabilidades(habilidades);
        aspiranteRepository.save(nuevoAspirante);

        try {
            String subject = "¡Bienvenido a Gestión de Vacantes!";
            String text = "Hola " + nombreCompleto + ",\n\nTu cuenta de Aspirante ha sido creada exitosamente. " +
                    "Ya puedes iniciar sesión con tu correo: " + correo;
            emailService.sendSimpleMessage(correo, subject, text);
        } catch (Exception e) {
            System.err.println("Error al enviar correo de bienvenida al aspirante: " + e.getMessage());
        }

        return "redirect:/login?registroExitoso";
    }

    @PostMapping("/empleador")
    public String procesarRegistroEmpleador(
            @RequestParam String nombreCompleto,
            @RequestParam String correo,
            @RequestParam String password,
            @RequestParam String empresa,
            RedirectAttributes attributes) { // <-- 3. AÑADIR RedirectAttributes

        // --- INICIO DE VALIDACIÓN DE CORREO ---
        if (aspiranteRepository.findByCorreo(correo).isPresent()) {
            attributes.addFlashAttribute("error_correo", "Este correo ya está registrado como Aspirante.");
            return "redirect:/registro/empleador";
        }
        if (empleadorRepository.findByCorreo(correo).isPresent()) {
            attributes.addFlashAttribute("error_correo", "Este correo ya está en uso.");
            return "redirect:/registro/empleador";
        }
        // --- FIN DE VALIDACIÓN DE CORREO ---

        Empleador nuevoEmpleador = new Empleador();
        nuevoEmpleador.setNombreCompleto(nombreCompleto);
        nuevoEmpleador.setCorreo(correo);
        nuevoEmpleador.setPassword(passwordEncoder.encode(password));
        nuevoEmpleador.setEmpresa(empresa);
        empleadorRepository.save(nuevoEmpleador);

        try {
            String subject = "¡Bienvenido a Gestión de Vacantes!";
            String text = "Hola " + nombreCompleto + ",\n\nTu cuenta de Empleador para la empresa " + empresa + " ha sido creada exitosamente. " +
                    "Ya puedes iniciar sesión con tu correo: " + correo;
            emailService.sendSimpleMessage(correo, subject, text);
        } catch (Exception e) {
            System.err.println("Error al enviar correo de bienvenida al empleador: " + e.getMessage());
        }

        return "redirect:/login?registroExitoso";
    }
}