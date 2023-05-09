package sii.ms_evalexamenes.controllers;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.format.datetime.standard.DateTimeFormatterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
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
import sii.ms_evalexamenes.services.exceptions.UnauthorizedAccessException;
import sii.ms_evalexamenes.util.GeneratedFakeEndpoint;
import sii.ms_evalexamenes.util.JwtGenerator;

@RestController
@RequestMapping("/notificaciones")
@CrossOrigin(origins = "http://localhost:4200/")
public class NotificacionNotasController {

    private static final List<String> medios = new ArrayList<>();

    @Autowired
    private ServletWebServerApplicationContext server;

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

    /**
     * Añade una notificación para cada estudiante de publicación de notas
     * @param notificacion {@link NotificacionNotasDTO} Contiene la información de la notificación
     * @param builder {@link UriComponentsBuilder}
     * @param header Cabecera para extraer el JWT Bearer token
     * @return {@code 201 Created} - {@link Void}
     * @exception UnauthorizedAccessException {@code 403 Forbidden} Accesso no autorizado
     */
    @PostMapping(value="/notas", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addNotificacionNotas(@RequestBody NotificacionNotasDTO notificacion, UriComponentsBuilder builder, @RequestHeader Map<String, String> header) {
        if (!TokenUtils.comprobarAcceso(header, Arrays.asList("CORRECTOR")))
            throw new UnauthorizedAccessException();
        if (!medios.containsAll(notificacion.getMedios()))
            return ResponseEntity.badRequest().build();
        var peticion = get("http", "localhost", server.getWebServer().getPort(), "/estudiantes");

        var respuesta = new RestTemplate().exchange(peticion, new ParameterizedTypeReference<List<Map<String, Object>>>() {});

        String asunto = notificacion.getPlantillaAsunto();
        String cuerpo = notificacion.getPlantillaCuerpo();
        String programacionEnvio = notificacion.getProgramacionEnvio().format(new DateTimeFormatterFactory("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").createDateTimeFormatter());
        String[] medios = notificacion.getMedios().toArray(new String[notificacion.getMedios().size()]);
        for (Map<String, Object> estudiante : respuesta.getBody()) {
            String emailDestino = estudiante.get("email").toString();
            String telefonoDestino = estudiante.get("telefono").toString();
            JSONObject notificacionNotas = buildNotificacion(asunto, cuerpo, emailDestino, telefonoDestino, programacionEnvio, medios, "ANUNCIO_NOTA_ESTUDIANTE");
            
            var peticionPOST = post("http", "localhost", server.getWebServer().getPort(), "/notificaciones", notificacionNotas, tokenValido);

            new RestTemplate().exchange(peticionPOST, Void.class);
        }
        URI uri = builder
                .path("/notificaciones/notas")
                .build()
                .toUri();
        return ResponseEntity.created(uri).build();
    }

    /**
     * Endpoint que imita al endpoint del microservicio de gestión de notificaciones (necesario para hacer POST de notas)
     * @param notificacion {@link JSONObject} Contiene la información de la notificación
     * @param builder {@link UriComponentsBuilder}
     * @param header Cabecera para extraer el JWT Bearer token
     * @return {@code 201 Created} - {@link Void}
     * @exception UnauthorizedAccessException {@code 403 Forbidden} Acceso no autorizado
     * @annotation {@link GeneratedFakeEndpoint} Anotación para evitar el recubrimiento de JaCoCo al ser un endpoint que no pertenece a nuestro microservicio
     */
    @GeneratedFakeEndpoint
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

    /**
     * Método auxiliar para construir el DTO de una notificación para el POST del endpoint falso de notificaciones
     * @param asunto {@link String} Asunto de la notificación
     * @param cuerpo {@link String} Cuerpo de la notificación
     * @param emailDestino {@link String} Email destino de la persona
     * @param telefonoDestino {@link String} Teléfono destino de la persona
     * @param programacionEnvio {@link String} Fecha en la que se enviará la notificación
     * @param medios {@link String[]} Medios permitidos para enviar la notificación (SMS o EMAIL) 
     * @param tipoNotificacion {@link String} Tipo de la notificación
     * @return {@link JSONObject} Objeto creado para la notificación
     */
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

    @ExceptionHandler(UnauthorizedAccessException.class)
    @ResponseStatus(code = HttpStatus.FORBIDDEN)
    public void unauthorizedAccess() {}
}
