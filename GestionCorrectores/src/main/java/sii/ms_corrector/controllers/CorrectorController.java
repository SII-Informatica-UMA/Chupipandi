package sii.ms_corrector.controllers;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import sii.ms_corrector.dtos.CorrectorDTO;
import sii.ms_corrector.dtos.CorrectorNuevoDTO;
import sii.ms_corrector.entities.Corrector;
import sii.ms_corrector.security.TokenUtils;
import sii.ms_corrector.services.CorrectorService;
import sii.ms_corrector.services.exceptions.AccesoNoAutorizado;
import sii.ms_corrector.services.exceptions.CorrectorNoEncontrado;
import sii.ms_corrector.services.exceptions.CorrectorYaExiste;
import sii.ms_corrector.services.exceptions.PeticionIncorrecta;

@RestController
@RequestMapping("/correctores")
public class CorrectorController {

    private CorrectorService service;
    
    public CorrectorController(CorrectorService service) {
        this.service = service;
    }

	// Necesitamos una base de datos de Materia solida y predefinida. Donde tengamos tuplas (idMateria, nombre)
	// para poder consultar si existe o no antes de introducirla (por definicion, en el POST podemos elegir si
	// especificar la materia bien por su id o bien por su nombre). Podria implementarse un endpoint fake
	// como '/materias' que devuelva una lista de materias predefinidas, y comprobarlo en base a eso.

	// De momento desarrollamos la idea de: si la materia que trae un CorrectorNuevoDTO no existe, se creará
	// Si se le proporciona solo el nombre, como no tenemos una lista predefinida de materias, el idMateria que
	// se asocia queda como nulo (no afecta al funcionamiento, pero se ve feo al hacer un GET (todos o un corrector))

	/**
	 * Obtiene un corrector concreto
	 * @param id id del corrector a solicitar
	 * @param header cabecera para extraer el token (incluir token al hacer la peticion)
	 * @return {@code 200 OK} - el corrector solicitado {@link CorrectorDTO}
	 * @exception AccesoNoAutorizado {@code 403 Forbidden} Acceso no autorizado
	 * @exception CorrectorNoEncontrado {@code 404 Not Found} El corrector no existe
	 */
	@GetMapping("{id}")
	public ResponseEntity<CorrectorDTO> obtenerCorrector(@PathVariable Long id, @RequestHeader Map<String,String> header) {
		if (!TokenUtils.comprobarAcceso(header, Arrays.asList("VICERRECTORADO")))
			throw new AccesoNoAutorizado();
		Corrector contactoById = service.getCorrectorById(id);
		return ResponseEntity.ok(CorrectorDTO.fromCorrector(contactoById));
	}

	/**
	 * Actualiza un corrector
	 * <p>
	 * Note: Cambiar {@code identificadorConvocatoria} (y opcionalmente la {@code MateriaDTO} asociada)
	 * añadirá dicha materia a la lista de materias en convocatoria asociada al corrector
	 * </p>
	 * @param id id del corrector a modificar
	 * @param corrector {@link CorrectorNuevoDTO} que contiene los nuevos cambios
	 * @param header cabecera para extraer el token (incluir token al hacer la peticion)
	 * @return {@code 200 OK} - {@link Void}
	 * @exception AccesoNoAutorizado {@code 403 Forbidden} Acceso no autorizado
	 * @exception CorrectorNoEncontrado {@code 404 Not Found} El corrector no existe
	 */
	@PutMapping("{id}")
	// [x]: Preguntar qué devuelve un PUT (siempre lo hemos hecho void, pero la API es confusa) (conlleva cambiar tambien los tests)
	public ResponseEntity<?> modificaCorrector(@PathVariable Long id, @RequestBody CorrectorNuevoDTO corrector, @RequestHeader Map<String,String> header) {
		if (!TokenUtils.comprobarAcceso(header, Arrays.asList("VICERRECTORADO")))
			throw new AccesoNoAutorizado();
		service.modificarCorrector(id, corrector);
		return ResponseEntity.ok().build();
	}

	/**
	 * Elimina un corrector
	 * @param id id del corrector
	 * @param header cabecera para extraer el token (incluir token al hacer la peticion)
	 * @return {@code 200 OK} - {@link Void}
	 * @exception AccesoNoAutorizado {@code 403 Forbidden} Acceso no autorizado
	 * @exception CorrectorNoEncontrado {@code 404 Not Found} El corrector no existe
	 */
	@DeleteMapping("{id}")
	@ResponseStatus(code = HttpStatus.OK)
	public void eliminarCorrector(@PathVariable Long id, @RequestHeader Map<String,String> header) {
		if (!TokenUtils.comprobarAcceso(header, Arrays.asList("VICERRECTORADO")))
			throw new AccesoNoAutorizado();
		service.eliminarCorrector(id);
	}

    /**
	 * Obtiene la lista de correctores del sistema
     * @param idConvocatoria (para query, es opcional)
     * @param header cabecera para extraer el token (incluir token al hacer la peticion)
     * @return {@code 200 OK} - {@link List}<{@linkplain CorrectorDTO}> Lista de correctores
	 * @exception AccesoNoAutorizado {@code 403 Forbidden} Acceso no autorizado
     */
    @GetMapping
    public ResponseEntity<List<CorrectorDTO>> obtieneCorrectores(@RequestParam(required = false) Long idConvocatoria, @RequestHeader Map<String,String> header) {
		if (!TokenUtils.comprobarAcceso(header, Arrays.asList("VICERRECTORADO")))
			throw new AccesoNoAutorizado();
		List<Corrector> correctores;
		if (idConvocatoria == null) {
			correctores = service.getTodosCorrectores();
		} else {
			correctores = service.getTodosCorrectoresByConvocatoria(idConvocatoria);
		}
        Function<Corrector, CorrectorDTO> mapper = (correct -> CorrectorDTO.fromCorrector(correct));
		return ResponseEntity.ok(correctores.stream().map(mapper).toList());
    }

    /**
	 * Crea un nuevo corrector para la convocatoria vigente (que se pasa dentro del objeto)
     * @param nuevoCorrector {@link CorrectorNuevoDTO} que contiene los nuevos cambios
     * @param builder {@link UriComponentsBuilder}
     * @param header cabecera para extraer el token (incluir token al hacer la peticion)
     * @return {@code 201 Created} - {@link Void}
	 * @exception AccesoNoAutorizado {@code 403 Forbidden} Acceso no autorizado
	 * @exception CorrectorNoEncontrado {@code 404 Not Found} El corrector no existe
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)	// "aplication/json"
	public ResponseEntity<?> añadirCorrector(@RequestBody CorrectorNuevoDTO nuevoCorrector, UriComponentsBuilder builder, @RequestHeader Map<String,String> header) {
		if (!TokenUtils.comprobarAcceso(header, Arrays.asList("VICERRECTORADO")))
			throw new AccesoNoAutorizado();
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

    @ExceptionHandler(PeticionIncorrecta.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)		// 400
	// para cuando se intenta introducir una materia nula
    public void peticionIncorrecta() {}
}
