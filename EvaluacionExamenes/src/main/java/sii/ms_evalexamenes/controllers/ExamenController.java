package sii.ms_evalexamenes.controllers;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriBuilderFactory;
import org.springframework.web.util.UriComponentsBuilder;

import sii.ms_evalexamenes.dtos.AsignacionDTO;
import sii.ms_evalexamenes.dtos.EstadoCorrecionesDTO;
import sii.ms_evalexamenes.dtos.ExamenDTO;
import sii.ms_evalexamenes.dtos.ExamenNuevoDTO;
import sii.ms_evalexamenes.entities.Examen;
import sii.ms_evalexamenes.security.TokenUtils;
import sii.ms_evalexamenes.services.ExamenService;
import sii.ms_evalexamenes.services.exceptions.NotFoundException;
import sii.ms_evalexamenes.services.exceptions.UnauthorizedAccessException;
import sii.ms_evalexamenes.util.JwtGenerator;

@RestController
@RequestMapping("/examenes")
public class ExamenController {
    
    private ExamenService service;

    public ExamenController(ExamenService service) {
        this.service = service;
    }

    String tokenValido = JwtGenerator.createToken("user", 5, "VICERRECTORADO");
    
    private URI uri(String scheme, String host, int port, String ...paths) {
        UriBuilderFactory ubf = new DefaultUriBuilderFactory();
        UriBuilder ub    = ubf.builder()
                        .scheme(scheme)
                        .host(host)
                        .port(port);
        for (String path : paths) {
            ub = ub.path(path);
        }
        return ub.build();
    }

	private RequestEntity<Void> get(String scheme, String host, int port, String path, String tk) {
		URI uri = uri(scheme, host,port, path);
		var peticion = RequestEntity.get(uri)
			.header("Authorization", "Bearer " + tk)
			.accept(MediaType.APPLICATION_JSON)
			.build();
		return peticion;
	}

    @GetMapping("{id}")
    public ResponseEntity<ExamenDTO> getExamen(@PathVariable Long id, @RequestHeader Map<String, String> header) {
        if (!TokenUtils.comprobarAcceso(header, Arrays.asList("CORRECTOR")))
            throw new UnauthorizedAccessException();
            
        ExamenDTO examen = ExamenDTO.fromExamen(service.getExamenById(id).orElseThrow(NotFoundException::new));
        return ResponseEntity.ok(examen);
    }

    @PutMapping("{id}")
    public ResponseEntity<?> updateExamen(@PathVariable Long id, @RequestBody ExamenDTO examen, @RequestHeader Map<String, String> header) {
        if (!TokenUtils.comprobarAcceso(header, Arrays.asList("CORRECTOR")))
            throw new UnauthorizedAccessException();
        Examen ex = examen.examen();
        ex.setId(id);
        service.updateExamen(ex);
        return ResponseEntity.ok().build();
    }

   @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addExamen(@RequestBody ExamenNuevoDTO examen, UriComponentsBuilder builder, @RequestHeader Map<String, String> header) {
        
        
        if (!TokenUtils.comprobarAcceso(header, Arrays.asList("CORRECTOR"))){
       
            throw new UnauthorizedAccessException();
        }
        Examen examenNuevo = examen.examen();
        var peticion = get("http", "localhost", 8081, "/correctores",tokenValido);
    
        
        
        var respuesta = new RestTemplate().exchange(peticion, String.class);
       

        JSONArray correctores = new JSONArray(respuesta.getBody());
        
        corrLoop:
        for (int i = 0; i < correctores.length(); ++i) {
        
            JSONObject corrector = correctores.getJSONObject(i);
            if (service.getCorrectoresById(examenNuevo.getId()).get().size() + 1 <= corrector.getInt("maximasCorrecciones")) {
               
                JSONArray materias = corrector.getJSONArray("materias");
                for (int j = 0; j < materias.length(); ++j) {
                  
                    JSONObject materia = materias.getJSONObject(j);
                    if (materia.getLong("idMateria") == examen.getMateria()) {
        
                        examenNuevo.setCorrectorId(corrector.getLong("id"));
                        break corrLoop;
                    }
                    else
                        examenNuevo.setCorrectorId(-1L);
                }
            } else {
          
                examenNuevo.setCorrectorId(-1L);
            }
        }
        Long id = service.addExamen(examenNuevo);
        
        URI uri = builder
                .path("/examenes")
                .path(String.format("/%d", id))
                .build()
                .toUri();
        return ResponseEntity.created(uri).build();
    }

    @GetMapping("/asignacion")
    public ResponseEntity<List<AsignacionDTO>> getAsignacion(@RequestHeader Map<String, String> header) {
        if (!TokenUtils.comprobarAcceso(header, Arrays.asList("CORRECTOR")))
            throw new UnauthorizedAccessException();
        List<AsignacionDTO> response = new ArrayList<>();
        for (Examen ex : service.getAllExamenes().get()) {
            AsignacionDTO dto = AsignacionDTO.fromExamen(ex);
            response.add(dto);
        }
        return ResponseEntity.ok(response);
    }

    @PutMapping("/asignacion")
    public ResponseEntity<?> updateAsignacion(@RequestBody List<AsignacionDTO> asignacion, @RequestHeader Map<String, String> header) {
        if (!TokenUtils.comprobarAcceso(header, Arrays.asList("CORRECTOR")))
            throw new UnauthorizedAccessException();
        for (AsignacionDTO asig : asignacion) {
            Examen ex = service.getExamenById(asig.getIdExamen()).get();
            ex.setCorrectorId(asig.getIdCorrector());
            service.updateExamen(ex);
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/correcciones")
    public ResponseEntity<EstadoCorrecionesDTO> getCorreciones(@RequestHeader Map<String, String> header) {
        if (!TokenUtils.comprobarAcceso(header, Arrays.asList("VICERRECTOR", "CORRECTOR")))
            throw new UnauthorizedAccessException();
        List<Long> corregidos = new ArrayList<>();
        List<Long> pendientes = new ArrayList<>();

        for (Examen ex : service.getAllExamenes().get()) {
            if (ex.getCalificacion() == null) pendientes.add(ex.getId());
            else corregidos.add(ex.getId());
        }

        EstadoCorrecionesDTO response = EstadoCorrecionesDTO.builder()
                                        .corregidos(corregidos)
                                        .pendientes(pendientes)
                                        .build();
        return ResponseEntity.ok(response);
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public void notFound() {}

    @ExceptionHandler(UnauthorizedAccessException.class)
    @ResponseStatus(code = HttpStatus.FORBIDDEN)
    public void unauthorizedAccess() {}
}
