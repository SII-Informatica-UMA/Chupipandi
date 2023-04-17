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
import sii.ms_evalexamenes.service.ExamenDBService;

@RestController
@RequestMapping(path = "/materias")
public class Materia_Controller {
	public static final String MATERIA_PATH="/materias";
	
	private ExamenDBService service;
	
	public Materia_Controller(ExamenDBService service) {
		this.service = service;
	}
	
	
	@GetMapping
	 public ResponseEntity<List<Materia_DTO>> obtieneCorrectores(@RequestParam(required = false) Long idConvocatoria) {
		List<Materia> materias = null;
		if (idConvocatoria == null) {
			materias = service.get_All_Materias().get();
		} else {
			//materias = service.getTodosMateriasByConvocatoria(idConvocatoria).get();
		}
        Function<Materia, Materia_DTO> mapper = (materia -> Materia_DTO.fromMateria(materia));
		return ResponseEntity.ok(materias.stream().map(mapper).toList());
    }
	
}
