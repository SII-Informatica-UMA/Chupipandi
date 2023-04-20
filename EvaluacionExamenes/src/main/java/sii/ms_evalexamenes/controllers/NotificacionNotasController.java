package sii.ms_evalexamenes.controllers;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import sii.ms_evalexamenes.dtos.NotificacionNotasDTO;
import sii.ms_evalexamenes.services.exceptions.AlreadyExistsException;
import sii.ms_evalexamenes.services.exceptions.NotFoundException;
import sii.ms_evalexamenes.services.exceptions.UnauthorizedAccessException;

@RestController
@RequestMapping("/notificaciones/notas")
public class NotificacionNotasController {

    private static final List<String> medios = new ArrayList<>();

    public NotificacionNotasController() {
        medios.add("EMAIL");
        medios.add("SMS");
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addNotificacion(@RequestBody NotificacionNotasDTO notificacion, UriComponentsBuilder builder) {
        if (!medios.containsAll(notificacion.getMedios())) {
            System.out.println(medios);
            System.out.println(notificacion.getMedios());
            return ResponseEntity.badRequest().build();
        }
        URI uri = builder
                .path("/notificaciones/notas")
                .build()
                .toUri();
        return ResponseEntity.created(uri).build();
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
