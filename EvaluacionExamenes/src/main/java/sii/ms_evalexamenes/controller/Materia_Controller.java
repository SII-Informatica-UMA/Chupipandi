package sii.ms_evalexamenes.controller;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

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
	
	@GetMapping("{id}")
    public ResponseEntity<Materia> getExamen(@PathVariable Long id) {
        Optional<Materia> materia = service.getMateriaById(id);
        return ResponseEntity.of(materia);
    }

	
	@DeleteMapping("{id}")
	public ResponseEntity<?> eliminarLista(@PathVariable(name = "id") Long id) {
		if (service.getMateriaById(id).isPresent()) {
			service.deleteMateria(id);
			return ResponseEntity.ok().build();
		}
		return ResponseEntity.notFound().build();
	}
	
	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> añadirCorrector(@RequestBody Materia_DTO newMateria, UriComponentsBuilder builder) {
		Long id = service.addMateria(newMateria.materia());
		URI uri = builder
				.path("/materias")
				.path(String.format("/%d",id))
				.build()
				.toUri();
		return ResponseEntity.created(uri).build();
	}
}
