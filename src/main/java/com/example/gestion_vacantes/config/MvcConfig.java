package com.example.gestion_vacantes.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // La URL pública debe ser "/uploads/**"
        registry.addResourceHandler("/uploads/**")
                // La carpeta física debe ser "file:uploads/" (¡OJO con la diagonal al final!)
                .addResourceLocations("file:uploads/");
    }
}