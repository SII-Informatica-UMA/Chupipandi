package sii.ms_evalexamenes.controller;

import java.util.List;
import java.util.function.Function;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import sii.ms_evalexamenes.dto.Materia_DTO;
import sii.ms_evalexamenes.entities.Materia;
import sii.ms_evalexamenes.service.DB_Service;

@RestController
@RequestMapping(path = "/materias")
public class Materia_Controller {
	public static final String MATERIA_PATH="/materias";
	
	private DB_Service service;
	
	public Materia_Controller(DB_Service service) {
		this.service = service;
	}
	
	@GetMapping
	public ResponseEntity<?> get_Materias() {
		return ResponseEntity.ok().build();
	}
	
}
