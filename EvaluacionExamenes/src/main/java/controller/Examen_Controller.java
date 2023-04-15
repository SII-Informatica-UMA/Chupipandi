package controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import service.ExamenDBService;

@RestController
@RequestMapping(path = "/examen")
public class Examen_Controller {
	public static final String EXAMEN_PATH="/examen";
	
	private ExamenDBService service;
	
	public Examen_Controller(ExamenDBService service) {
		this.service = service;
	}
	@GetMapping
	public ResponseEntity<?> obtieneListas() {
		return ResponseEntity.ok().build();
	}
	
}
