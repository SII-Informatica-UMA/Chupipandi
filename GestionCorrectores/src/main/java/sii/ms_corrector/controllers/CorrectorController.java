package sii.ms_corrector.controllers;

import java.net.URI;
import java.util.List;
import java.util.function.Function;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import sii.ms_corrector.dtos.CorrectorDTO;
import sii.ms_corrector.dtos.CorrectorNuevoDTO;
import sii.ms_corrector.entities.Corrector;
import sii.ms_corrector.services.CorrectorService;
import sii.ms_corrector.services.exceptions.AccesoNoAutorizado;
import sii.ms_corrector.services.exceptions.CorrectorNoEncontrado;
import sii.ms_corrector.services.exceptions.CorrectorYaExiste;

@RestController
@RequestMapping("/correctores")
public class CorrectorController {

    private CorrectorService service;
    
    public CorrectorController(CorrectorService service) {
        this.service = service;
    }

	@GetMapping("{id}")
	// 200, 403, 404
	public ResponseEntity<CorrectorDTO> obtenerCorrector(@PathVariable Long id) {
		Corrector contactoById = service.getCorrectorById(id);
		return ResponseEntity.ok(CorrectorDTO.fromCorrector(contactoById));
	}

	// FIXME
	@PutMapping("{id}")
	// 200, 403, 404
	public ResponseEntity<?> modificaCorrector(@PathVariable Long id, @RequestBody CorrectorDTO corrector) {
		Corrector entidadCorrector = corrector.corrector();
		entidadCorrector.setId(id);
		service.modificarCorrector(entidadCorrector);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("{id}")
	@ResponseStatus(code = HttpStatus.OK)
	// 200, 403, 404
	public void eliminarCorrector(@PathVariable Long id) {
		service.eliminarCorrector(id);
	}

    @GetMapping
	// 200, 403
    public ResponseEntity<List<CorrectorDTO>> obtieneCorrectores(@RequestParam(required = false) Long idConvocatoria) {
		List<Corrector> correctores;
		if (idConvocatoria == null) {
			correctores = service.getTodosCorrectores();
		} else {
			correctores = service.getTodosCorrectoresByConvocatoria(idConvocatoria);
		}
        Function<Corrector, CorrectorDTO> mapper = (correct -> CorrectorDTO.fromCorrector(correct));
		return ResponseEntity.ok(correctores.stream().map(mapper).toList());
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)	// "aplication/json"
	// 201, 403, 409
	public ResponseEntity<?> añadirCorrector(@RequestBody CorrectorNuevoDTO nuevoCorrector, UriComponentsBuilder builder) {
		Long id = service.añadirCorrector(nuevoCorrector);
		URI uri = builder
				.path("/correctores")
				.path(String.format("/%d",id))
				.build()
				.toUri();
		return ResponseEntity.created(uri).build();
	}
	
    @ExceptionHandler(AccesoNoAutorizado.class)
    @ResponseStatus(code = HttpStatus.FORBIDDEN)	// 403
    public void accesoNoAutorizado() {}

    @ExceptionHandler(CorrectorNoEncontrado.class)
    @ResponseStatus(code = HttpStatus.NOT_FOUND)	// 404
    public void correctorNoEncontrado() {}

    @ExceptionHandler(CorrectorYaExiste.class)
    @ResponseStatus(code = HttpStatus.CONFLICT)		// 409
    public void correctorYaExiste() {}
}
