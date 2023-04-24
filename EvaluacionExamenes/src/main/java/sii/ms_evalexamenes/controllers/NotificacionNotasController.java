package sii.ms_evalexamenes.controllers;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.format.datetime.standard.DateTimeFormatterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
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

import sii.ms_evalexamenes.dtos.NotificacionNotasDTO;
import sii.ms_evalexamenes.security.TokenUtils;
import sii.ms_evalexamenes.services.exceptions.AlreadyExistsException;
import sii.ms_evalexamenes.services.exceptions.NotFoundException;
import sii.ms_evalexamenes.services.exceptions.UnauthorizedAccessException;
import sii.ms_evalexamenes.util.JwtGenerator;

@RestController
@RequestMapping("/notificaciones")
public class NotificacionNotasController {

    private static final List<String> medios = new ArrayList<>();

    public NotificacionNotasController() {
        medios.add("EMAIL");
        medios.add("SMS");
    }

    String tokenValido = JwtGenerator.createToken("user", 5, "VICERRECTORADO", "CORRECTOR");
    
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

	private RequestEntity<Void> get(String scheme, String host, int port, String path) {
		URI uri = uri(scheme, host,port, path);
		var peticion = RequestEntity.get(uri)
			.accept(MediaType.APPLICATION_JSON)
			.build();
		return peticion;
	}

    private <T> RequestEntity<T> post(String scheme, String host, int port, String path, T object, String tk) {
		URI uri = uri(scheme, host,port, path);
		var peticion = RequestEntity.post(uri)
			.header("Authorization", "Bearer " + tk)
			.contentType(MediaType.APPLICATION_JSON)
			.body(object);
		return peticion;
	}

    @PostMapping(value="/notas", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addNotificacionNotas(@RequestBody NotificacionNotasDTO notificacion, UriComponentsBuilder builder, @RequestHeader Map<String, String> header) {
        if (!TokenUtils.comprobarAcceso(header, Arrays.asList("CORRECTOR")))
            throw new UnauthorizedAccessException();
        if (!medios.containsAll(notificacion.getMedios()))
            return ResponseEntity.badRequest().build();
        var peticion = get("http", "localhost", 8080, "/estudiantes");

        var respuesta = new RestTemplate().exchange(peticion, new ParameterizedTypeReference<List<Map<String, Object>>>() {});
        if (respuesta.getBody().isEmpty())
            return ResponseEntity.notFound().build();
        String asunto = notificacion.getPlantillaAsunto();
        String cuerpo = notificacion.getPlantillaCuerpo();
        String programacionEnvio = notificacion.getProgramacionEnvio().format(new DateTimeFormatterFactory("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").createDateTimeFormatter());
        String[] medios = (String[])(notificacion.getMedios().toArray());
        for (Map<String, Object> estudiante : respuesta.getBody()) {
            String emailDestino = estudiante.get("email").toString();
            String telefonoDestino = estudiante.get("telefonoDestino").toString();
            JSONObject notificacionNotas = buildNotificacion(asunto, cuerpo, emailDestino, telefonoDestino, programacionEnvio, medios, "ANUNCIO_NOTA_ESTUDIANTE");
            
            var peticionPOST = post("http", "localhost", 8080, "/notificaciones", notificacionNotas, tokenValido);

            var respuestaPOST = new RestTemplate().exchange(peticionPOST, Void.class);
            if (!respuestaPOST.getStatusCode().equals(HttpStatus.CREATED))
                return ResponseEntity.status(respuestaPOST.getStatusCode()).build();
        }
        URI uri = builder
                .path("/notificaciones/notas")
                .build()
                .toUri();
        return ResponseEntity.created(uri).build();
    }

    // ENDPOINT FAKE DE NOTIFICACIONES
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addNotificacion(@RequestBody JSONObject notificacion, UriComponentsBuilder builder, @RequestHeader Map<String, String> header) {
        if (!TokenUtils.comprobarAcceso(header, Arrays.asList("CORRECTOR")))
            throw new UnauthorizedAccessException();
        URI uri = builder
        .path("/notificaciones")
        .build()
        .toUri();
        return ResponseEntity.created(uri).build();
    }

    private static JSONObject buildNotificacion(String asunto, String cuerpo, String emailDestino, String telefonoDestino, String programacionEnvio, String[] medios, String tipoNotificacion) {
        return new JSONObject()
                        .put("asunto", asunto)
                        .put("cuerpo", cuerpo)
                        .put("emailDestino", emailDestino)
                        .put("telefonoDestino", telefonoDestino)
                        .put("programacionEnvio", programacionEnvio)
                        .put("medios", new JSONArray(medios))
                        .put("tipoNotificacion", tipoNotificacion);
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
