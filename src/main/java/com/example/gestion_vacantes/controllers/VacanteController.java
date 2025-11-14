package com.example.gestion_vacantes.controllers;

import com.example.gestion_vacantes.models.*;
import com.example.gestion_vacantes.repositories.AspiranteRepository;
import com.example.gestion_vacantes.repositories.EmpleadorRepository;
import com.example.gestion_vacantes.repositories.PostulacionRepository;
import com.example.gestion_vacantes.repositories.VacanteRepository;
import com.example.gestion_vacantes.services.EmailService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Controller
@RequestMapping("/vacantes")
public class VacanteController {

    private final VacanteRepository vacanteRepository;
    private final AspiranteRepository aspiranteRepository;
    private final EmpleadorRepository empleadorRepository;
    private final PostulacionRepository postulacionRepository;
    private final EmailService emailService;

    private static final String UPLOAD_DIR = "uploads/cvs/";

    // Constructor actualizado (ya incluye EmailService)
    public VacanteController(VacanteRepository vacanteRepository, AspiranteRepository aspiranteRepository, EmpleadorRepository empleadorRepository, PostulacionRepository postulacionRepository, EmailService emailService) {
        this.vacanteRepository = vacanteRepository;
        this.aspiranteRepository = aspiranteRepository;
        this.empleadorRepository = empleadorRepository;
        this.postulacionRepository = postulacionRepository;
        this.emailService = emailService;
    }

    // LISTA DE TODAS LAS VACANTES (PARA AMBOS ROLES)
    @GetMapping
    public String listarVacantes(Model model) {
        model.addAttribute("vacantes", vacanteRepository.findAll());
        return "vacantes/lista-publica";
    }

    // FORMULARIO PARA CREAR NUEVA VACANTE (SÓLO EMPLEADOR)
    @GetMapping("/nueva")
    public String mostrarFormularioNuevaVacante(Model model) {
        model.addAttribute("vacante", new Vacante());
        return "vacantes/formulario";
    }

    // PROCESAR CREACIÓN DE NUEVA VACANTE
    @PostMapping("/guardar")
    public String guardarVacante(@ModelAttribute Vacante vacante, RedirectAttributes attributes) {
        String correo = SecurityContextHolder.getContext().getAuthentication().getName();
        Empleador empleador = empleadorRepository.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Empleador no encontrado"));

        vacante.setEmpleador(empleador);
        vacanteRepository.save(vacante);

        attributes.addFlashAttribute("msg_exito", "¡Vacante publicada correctamente!");
        return "redirect:/vacantes/mis-vacantes";
    }

    // LISTA DE VACANTES DEL EMPLEADOR LOGUEADO
    @GetMapping("/mis-vacantes")
    public String listarMisVacantes(Model model) {
        String correo = SecurityContextHolder.getContext().getAuthentication().getName();
        Empleador empleador = empleadorRepository.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Empleador no encontrado"));

        List<Vacante> misVacantes = vacanteRepository.findByEmpleadorId(empleador.getId());
        model.addAttribute("vacantes", misVacantes);
        return "vacantes/mis-vacantes";
    }

    // VER ASPIRANTES DE UNA VACANTE ESPECÍFICA
    @GetMapping("/{id}/aspirantes")
    public String verAspirantes(@PathVariable Long id, Model model) {
        Vacante vacante = vacanteRepository.findById(id).orElseThrow(() -> new RuntimeException("Vacante no encontrada"));
        model.addAttribute("vacante", vacante);
        model.addAttribute("postulaciones", vacante.getPostulaciones());
        return "vacantes/aspirantes";
    }

    // PROCESAR POSTULACIÓN DE UN ASPIRANTE
    @PostMapping("/{id}/postular")
    public String postularse(@PathVariable Long id, @RequestParam("cv") MultipartFile cv, RedirectAttributes attributes) {
        String correo = SecurityContextHolder.getContext().getAuthentication().getName();
        Aspirante aspirante = aspiranteRepository.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Aspirante no encontrado"));
        Vacante vacante = vacanteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vacante no encontrada"));

        if (cv.isEmpty()) {
            attributes.addFlashAttribute("msg_error", "Por favor, selecciona un archivo CV.");
            return "redirect:/vacantes";
        }
        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);

            String fileName = aspirante.getId() + "_" + cv.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);
            Files.write(filePath, cv.getBytes());

            Postulacion postulacion = new Postulacion();
            postulacion.setAspirante(aspirante);
            postulacion.setVacante(vacante);

            // ==========================================================
            // AQUÍ ESTÁ LA CORRECCIÓN
            // Guardamos SÓLO el nombre del archivo, no la ruta completa.
            postulacion.setCvPath(fileName);
            // ==========================================================

            postulacionRepository.save(postulacion);

            attributes.addFlashAttribute("msg_exito", "¡Te has postulado exitosamente!");

        } catch (Exception e) {
            e.printStackTrace();
            attributes.addFlashAttribute("msg_error", "Ocurrió un error al subir tu CV.");
        }
        return "redirect:/vacantes";
    }


    // CAMBIAR ESTADO DE POSTULACIÓN (Con lógica de email)
    @PostMapping("/postulaciones/{id}/estado")
    public String cambiarEstadoPostulacion(@PathVariable("id") Long postulacionId,
                                           @RequestParam("estado") String estado,
                                           RedirectAttributes attributes) {
        Postulacion postulacion = postulacionRepository.findById(postulacionId)
                .orElseThrow(() -> new RuntimeException("Postulación no encontrada"));
        try {
            EstadoPostulacion nuevoEstado = EstadoPostulacion.valueOf(estado.toUpperCase());
            postulacion.setEstado(nuevoEstado);
            postulacionRepository.save(postulacion);

            // --- Lógica de envío de correo ---
            try {
                String correoAspirante = postulacion.getAspirante().getCorreo();
                String nombreAspirante = postulacion.getAspirante().getNombreCompleto();
                String tituloVacante = postulacion.getVacante().getTitulo();
                String empresa = postulacion.getVacante().getEmpleador().getEmpresa();
                String subject = "";
                String text = "";

                if (nuevoEstado == EstadoPostulacion.ACEPTADO) {
                    subject = "¡Buenas noticias sobre tu postulación a " + empresa + "!";
                    text = "Hola " + nombreAspirante + ",\n\n¡Felicidades! Tu postulación para la vacante de '" +
                            tituloVacante + "' ha sido ACEPTADA.\n\n" +
                            "La empresa se pondrá en contacto contigo pronto para los siguientes pasos.";
                } else if (nuevoEstado == EstadoPostulacion.RECHAZADO) {
                    subject = "Actualización sobre tu postulación a " + empresa;
                    text = "Hola " + nombreAspirante + ",\n\nGracias por tu interés en la vacante de '" +
                            tituloVacante + "'.\n\n" +
                            "Lamentamos informarte que, en esta ocasión, hemos decidido continuar con otros candidatos. " +
                            "Te deseamos mucho éxito en tu búsqueda.";
                }

                if (!subject.isEmpty()) {
                    emailService.sendSimpleMessage(correoAspirante, subject, text);
                }
            } catch (Exception e) {
                System.err.println("Error al enviar correo de notificación de estado: " + e.getMessage());
            }
            // --- Fin de envío de correo ---

            attributes.addFlashAttribute("msg_exito", "Estado del aspirante actualizado.");
        } catch (IllegalArgumentException e) {
            attributes.addFlashAttribute("msg_error", "Estado no válido.");
        }
        return "redirect:/vacantes/" + postulacion.getVacante().getId() + "/aspirantes";
    }

    // VER MIS POSTULACIONES (ASPIRANTE)
    @GetMapping("/mis-postulaciones")
    public String verMisPostulaciones(Model model) {
        String correo = SecurityContextHolder.getContext().getAuthentication().getName();
        Aspirante aspirante = aspiranteRepository.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Aspirante no encontrado"));

        List<Postulacion> misPostulaciones = postulacionRepository.findByAspiranteId(aspirante.getId());
        model.addAttribute("postulaciones", misPostulaciones);
        return "vacantes/mis-postulaciones";
    }
}