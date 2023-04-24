package sii.ms_evalexamenes.controllers;

import java.util.ArrayList;
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
    public ResponseEntity<List<ExamenDTO>> getNotas(@RequestParam Long dni, @RequestParam String apellido) {
        
        //El objeto optional que nos devuelve el metodo getExamenByDniAndApellido nunca apararece como null por lo tanto no devuelve excepcion
        //Siempre devuelve una lista 
        Optional<List<Examen>> notas = service.getExamenByDniAndApellido(dni, apellido);
        // notas.isPresent siempre es igual a True 
        // Podemos Comprobar si la lista esta vacia
        if(notas.get().isEmpty()){
            return ResponseEntity.notFound().build();
        }
       

        List<ExamenDTO>lista = new ArrayList<>();
        for (Examen e : notas.get()){
           lista.add(ExamenDTO.fromExamen(e));
        }
        System.out.print(lista.getClass());
        return ResponseEntity.ok(lista);
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
