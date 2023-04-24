package sii.ms_evalexamenes.controllers;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriBuilderFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import sii.ms_evalexamenes.dtos.ExamenDTO;
import sii.ms_evalexamenes.entities.Examen;
import sii.ms_evalexamenes.services.ExamenService;

@RestController
@RequestMapping("/notas")
public class NotasController {

    private ExamenService service;

    @Autowired
    private ServletWebServerApplicationContext server;

    public NotasController(ExamenService service) {
        this.service = service;
    }

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

    /**
     * Obtiene las notas del alumno con el DNI y primer apellido pasados como parámetros
     * @param dni {@link String} DNI del alumno
     * @param apellido {@link String} Primer apellido del alumno
     * @return {@code 200 OK} - {@link List<ExamenDTO>} - Lista de notas para el estudiante a buscar  
     * @return {@code 404 Not Found} - {@link Void} - En caso de que no encuentre el estudiante
     * @throws JsonMappingException
     * @throws JsonProcessingException
     */
    @GetMapping
    public ResponseEntity<List<ExamenDTO>> getNotas(@RequestParam String dni, @RequestParam String apellido) throws JsonMappingException, JsonProcessingException {
        
        //El objeto optional que nos devuelve el metodo getExamenByDniAndApellido nunca apararece como null por lo tanto no devuelve excepcion
        //Siempre devuelve una lista
        var peticion = get("http", "localhost", server.getWebServer().getPort(), "/estudiantes");

        var respuesta = new RestTemplate().exchange(peticion, new ParameterizedTypeReference<List<Map<String, Object>>>() {});
        Optional<Map<String, Object>> dniRespuesta = respuesta.getBody().stream().filter(est -> est.get("dni").equals(dni)).findFirst();
        if (!(dniRespuesta.isPresent() && dniRespuesta.get().get("apellido1").equals(apellido)))
            return ResponseEntity.notFound().build();
        Long id = Integer.toUnsignedLong((Integer)(dniRespuesta.get().get("id")));
        Optional<List<Examen>> notas = service.getExamenByDniAndApellido(id, apellido);
        
        // notas.isPresent siempre es igual a True 
        // Podemos Comprobar si la lista esta vacia
        if(notas.get().isEmpty()){
            return ResponseEntity.notFound().build();
        }
    
        List<ExamenDTO> lista = new ArrayList<>();
        for (Examen e : notas.get()){
            lista.add(ExamenDTO.fromExamen(e));
        }
        System.out.println(lista);
        return ResponseEntity.ok(lista);
    }
}
