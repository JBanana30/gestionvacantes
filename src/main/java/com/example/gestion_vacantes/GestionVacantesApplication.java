package com.example.gestion_vacantes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GestionVacantesApplication {

	public static void main(String[] args) {
        SpringApplication.run(GestionVacantesApplication.class, args);
        System.out.println("Ejecucion de registro vacantes en el puerto 8080..");
	}
}