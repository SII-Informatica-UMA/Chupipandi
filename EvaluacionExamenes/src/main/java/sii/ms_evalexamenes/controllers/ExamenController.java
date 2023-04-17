package sii.ms_evalexamenes.controllers;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import sii.ms_evalexamenes.dtos.AsignacionDTO;
import sii.ms_evalexamenes.dtos.EstadoCorrecionesDTO;
import sii.ms_evalexamenes.dtos.ExamenDTO;
import sii.ms_evalexamenes.entities.Examen;
import sii.ms_evalexamenes.services.ExamenService;
import sii.ms_evalexamenes.services.exceptions.AlreadyExistsException;
import sii.ms_evalexamenes.services.exceptions.UnauthorizedAccessException;

@RestController
@RequestMapping("/examenes")
public class ExamenController {
    
    private ExamenService service;

    public ExamenController(ExamenService service) {
        this.service = service;
    }

    @GetMapping("{id}")
    public ResponseEntity<Examen> getExamen(@PathVariable Long id) {
        Optional<Examen> examen = service.getExamenById(id);
        return ResponseEntity.of(examen);
    }

    @PutMapping("{id}")
    public ResponseEntity<?> updateExamen(@PathVariable Long id, @RequestBody ExamenDTO examen) {
        Examen ex = examen.examen();
        ex.setId(id);
        service.updateExamen(ex);
        return ResponseEntity.ok().build();
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addExamen(@RequestBody ExamenDTO examen, UriComponentsBuilder builder) {
        Long id = service.addExamen(examen.examen());
        URI uri = builder
                .path("/examenes")
                .path(String.format("/%d", id))
                .build()
                .toUri();
        return ResponseEntity.created(uri).build();
    }

    @GetMapping("/asignacion")
    public ResponseEntity<List<AsignacionDTO>> getAsignacion() {
        List<AsignacionDTO> response = new ArrayList<>();
        for (Examen ex : service.getAllExamenes().get()) {
            AsignacionDTO dto = AsignacionDTO.fromExamen(ex);
            response.add(dto);
        }
        return ResponseEntity.ok(response);
    }

    @PutMapping("/asignacion")
    public ResponseEntity<?> updateAsignacion(@RequestBody List<AsignacionDTO> asignacion) {
        for (AsignacionDTO asig : asignacion) {
            Examen ex = service.getExamenById(asig.getIdExamen()).get();
            ex.setCorrectorId(asig.getIdCorrector());
            service.updateExamen(ex);
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/correcciones")
    public ResponseEntity<EstadoCorrecionesDTO> getCorreciones() {
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

    @ExceptionHandler(AlreadyExistsException.class)
    @ResponseStatus(code = HttpStatus.CONFLICT)
    public void alreadyExists() {}

}
