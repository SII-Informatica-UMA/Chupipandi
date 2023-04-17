package sii.ms_evalexamenes.controller;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.springframework.boot.SpringApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import sii.ms_evalexamenes.Main;
import sii.ms_evalexamenes.entities.Examen;
import sii.ms_evalexamenes.service.ExamenDBService;


@RestController
@RequestMapping(path = "/examenes")
public class Examen_Controller {
	public static final String EXAMEN_PATH="/examenes";
	
	private ExamenDBService service;
	
	public Examen_Controller(ExamenDBService service) {
		this.service = service;
	}
	
	
	@GetMapping("{id}")
	public ResponseEntity<?> get_Exmamen_By_Id(@PathVariable Long id) {
		Optional<Examen> ExamenById = service.get_Examen_By_Id(id);
		return ResponseEntity.of(ExamenById);
		
	}
	
	@PutMapping("{id}")
	public ResponseEntity<?> modify_Examen_By_id(@PathVariable Long id, @RequestBody Examen examen) {
		//examen.setId(id);
		//service.modificarListaCompra(examen);
		return ResponseEntity.ok().build();
	}
		
	
	
}
