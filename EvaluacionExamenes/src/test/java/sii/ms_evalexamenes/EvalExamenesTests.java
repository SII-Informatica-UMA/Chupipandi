package sii.ms_evalexamenes;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.util.ArrayList;
import java.sql.Timestamp;
import java.lang.Float;



import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriBuilderFactory;

import sii.ms_evalexamenes.dtos.ExamenDTO;
import sii.ms_evalexamenes.entities.Examen;
import sii.ms_evalexamenes.repositories.ExamenRepository;
import sii.ms_evalexamenes.repositories.MateriaRepository;
import sii.ms_evalexamenes.entities.Examen;
import sii.ms_evalexamenes.entities.Materia;


@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DisplayName("En el servicio de Evaluación de Examenes")
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class EvalExamenesTests {
	@Autowired
	private TestRestTemplate restTemplate;
	
	@Value(value="${local.server.port}")
	private int port;
	
	@Autowired
	private ExamenRepository examenRepository;

	@Autowired
	private MateriaRepository materiaRepository;

	@BeforeEach
	public void initializeDatabase() {
		examenRepository.deleteAll();
	}

	
	private URI uri(String scheme, String host, int port, String ...paths) {
		UriBuilderFactory ubf = new DefaultUriBuilderFactory();
		UriBuilder ub = ubf.builder()
				.scheme(scheme)
				.host(host).port(port);
		for (String path: paths) {
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
	
	private RequestEntity<Void> delete(String scheme, String host, int port, String path) {
		URI uri = uri(scheme, host,port, path);
		var peticion = RequestEntity.delete(uri)
			.build();
		return peticion;
	}
	
	private <T> RequestEntity<T> post(String scheme, String host, int port, String path, T object) {
		URI uri = uri(scheme, host,port, path);
		var peticion = RequestEntity.post(uri)
			.contentType(MediaType.APPLICATION_JSON)
			.body(object);
		return peticion;
	}
	
	private <T> RequestEntity<T> put(String scheme, String host, int port, String path, T object) {
		URI uri = uri(scheme, host,port, path);
		var peticion = RequestEntity.put(uri)
			.contentType(MediaType.APPLICATION_JSON)
			.body(object);
		return peticion;
	}

	private boolean compararExamen(Examen examen1, Examen examen2) {
		// return examen1.getId() == examen2.getId() &&
		// 		Float.compare(examen1.getCalificacion(), examen2.getCalificacion()) == 0 &&
		// 		examen1.getFechaYHora().equals(examen2.getFechaYHora()) &&
		// 		examen1.getMateria().getId() == examen2.getMateria().getId() &&
		// 		examen1.getAlumnoId() == examen2.getAlumnoId() &&
		// 		examen1.getCorrectorId() == examen2.getCorrectorId();
		return examen1.toString().equals(examen2.toString());
	}

	
	@Nested
	@DisplayName("cuando no hay examenes")
	public class ExamenesVacios {
		
		@Test
		@DisplayName("No devuelve ningún examen")
		public void noDevuelveExamen() {
			
			var peticion = get("http", "localhost",port, "/examenes/1");

			var respuesta = restTemplate.exchange(peticion,
					new ParameterizedTypeReference<Examen>() {});
			
			assertThat(respuesta.getStatusCode().value()).isEqualTo(404);
			assertThat(respuesta.getBody()).isNull();
		}
	}

	@Nested
	@DisplayName("cuando hay examenes")
	public class ExamenesLlenos {
		Materia materiaEjemplo = new Materia(1L, "Materia1", new ArrayList<Long>(), new ArrayList<Examen>()); 
		Examen examenEjemplo = new Examen(1L, (float)5.0, new Timestamp(System.currentTimeMillis()), materiaEjemplo,  1L, 1L);
		@BeforeEach
		public void aniadirDatos() {
			// examenRepository.deleteAll();
			materiaRepository.save(materiaEjemplo);
			examenRepository.save(examenEjemplo);    
			// examenRepository.save(new Examen(0L, (float)5.0, new Timestamp(System.currentTimeMillis()),
			// 						 new Materia(1L, "Materia1", new ArrayList<Long>(), new ArrayList<Examen>())
			// 						 ,  1L, 1L));    
		}
		
		@Test
		@DisplayName("examen encontrado")
		public void devuelveExamen() {

			var peticion = get("http", "localhost",port, "/examenes/1");
			var respuesta = restTemplate.exchange(peticion,
				new ParameterizedTypeReference<Examen>() {});
	
			assertThat(respuesta.getBody()).isNotNull();
			assertThat(compararExamen(respuesta.getBody(), examenEjemplo)).isTrue();
			assertThat(respuesta.getStatusCode().value()).isEqualTo(200);
		}

		@Test
		@DisplayName("examen no encontrado")
		public void noDevuelveExamen() {

			var peticion = get("http", "localhost",port, "/examenes/4");
			var respuesta = restTemplate.exchange(peticion,
				new ParameterizedTypeReference<Examen>() {});
	
			assertThat(respuesta.getBody()).isNull();
			assertThat(respuesta.getStatusCode().value()).isEqualTo(404);
		}

		@Test
		@DisplayName("Nota del examen modificada")
		public void modificarNota() {

			Examen examenModificado = examenEjemplo;
			examenModificado.setCalificacion((float) 7.0);

			var peticion = put("http", "localhost",port, "/examenes/1", examenModificado.toJson());
			System.out.println("wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww");			
			System.out.println(peticion);
			var respuesta = restTemplate.exchange(peticion,
				new ParameterizedTypeReference<Examen>() {});
			assertThat(respuesta.getStatusCode().value()).isEqualTo(200);
			
			var peticion2 = get("http", "localhost",port, "/examenes/1");
			var respuesta2 = restTemplate.exchange(peticion2,
			new ParameterizedTypeReference<Examen>() {});
			
			assertThat(respuesta2.getBody()).isNotNull();
			assertThat(compararExamen(respuesta2.getBody(), examenEjemplo)).isFalse();
			assertThat(compararExamen(respuesta2.getBody(), examenModificado)).isTrue();
			assertThat(respuesta2.getStatusCode().value()).isEqualTo(200);
		}
	}
}
