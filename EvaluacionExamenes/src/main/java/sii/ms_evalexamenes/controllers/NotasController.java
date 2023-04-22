package sii.ms_evalexamenes.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import sii.ms_evalexamenes.dtos.ExamenDTO;
import sii.ms_evalexamenes.entities.Examen;
import sii.ms_evalexamenes.services.ExamenService;
import sii.ms_evalexamenes.services.exceptions.AlreadyExistsException;
import sii.ms_evalexamenes.services.exceptions.UnauthorizedAccessException;

@RestController
@RequestMapping("/notas")
public class NotasController {
    
    private ExamenService service;

    public NotasController(ExamenService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<ExamenDTO>> getNotas(@RequestParam Long dni, @RequestParam String apellido) throws NotFoundException {

        //El objeto optional que nos devuelve el metodo nunca es null por lo tanto no devulve excepcion
        //Siempre es una lista aunque esta este vacia 
        Optional<List<Examen>> notas = service.getExamenByDniAndApellido(dni, apellido);
        
        if(notas.get().isEmpty()){
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(service
                                .getExamenByDniAndApellido(dni, apellido)
                                .get()
                                .stream()
                                .map(ex -> ExamenDTO
                                .fromExamen(ex))
                                .toList());
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
