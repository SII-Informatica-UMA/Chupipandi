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
import sii.ms_evalexamenes.entities.Examen;


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

	@BeforeEach
	public void initializeDatabase() {
		examenRepository.deleteAll();
	}

	String token = "eyJhbGciOiJIUzI1NiJ9.eyJleHAiOjE2ODQ1MDI1ODgsInJvbGVzIjpbIkNPUlJFQ1RPUiJdfQ.5cWDJdzmurLrtipgtCyikuccojcIeup6aioNwCgTlPU";
	
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
	
	private RequestEntity<Void> get(String scheme, String host, int port, String path, String tk) {
		URI uri = uri(scheme, host,port, path);
		var peticion = RequestEntity.get(uri)
			.header("Authorization", "Bearer " + tk)
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
	
	private <T> RequestEntity<T> put(String scheme, String host, int port, String path, T object, String tk) {
		URI uri = uri(scheme, host,port, path);
		var peticion = RequestEntity.put(uri)
			.header("Authorization", "Bearer " + tk)
			.contentType(MediaType.APPLICATION_JSON)
			.body(object);
		return peticion;
	}

	private boolean compararExamenDTO(ExamenDTO examen1, ExamenDTO examen2) {
		return examen1.getId() == examen2.getId() &&
				(examen1.getNota() - examen2.getNota() == 0) &&
				examen1.getMateria() == examen2.getMateria() &&
				examen1.getCodigoAlumno() == examen2.getCodigoAlumno();
		// return examen1.toString().equals(examen2.toString());
	}
	
	@Nested
	@DisplayName("Cuando no hay examenes")
	public class ExamenesVacios {
		
		@Test
		@DisplayName("No devuelve ningún examen")
		public void noDevuelveExamen() {
			
			var peticion = get("http", "localhost",port, "/examenes/1", token);

			var respuesta = restTemplate.exchange(peticion,
					new ParameterizedTypeReference<ExamenDTO>() {});
			
			assertThat(respuesta.getStatusCode().value()).isEqualTo(404);
			assertThat(respuesta.getBody()).isNull();
		}
	}

	@Nested
	@DisplayName("Cuando hay examenes")
	public class ExamenesLlenos {
		Examen examenEjemplo = new Examen(1L, (float)5.0, new Timestamp(System.currentTimeMillis()), 1L,  1L, 1L);
		ExamenDTO examenDTOEjemplo = new ExamenDTO().fromExamen(examenEjemplo);
		@BeforeEach
		public void aniadirDatos() {
			// examenRepository.deleteAll();
			examenRepository.save(examenEjemplo);    
			// examenRepository.save(new Examen(0L, (float)5.0, new Timestamp(System.currentTimeMillis()),
			// 						 new Materia(1L, "Materia1", new ArrayList<Long>(), new ArrayList<Examen>())
			// 						 ,  1L, 1L));    
		}
		
		@Test
		@DisplayName("Examen encontrado")
		public void devuelveExamen() {

			var peticion = get("http", "localhost",port, "/examenes/1", token);
			var respuesta = restTemplate.exchange(peticion,
				new ParameterizedTypeReference<ExamenDTO>() {});
			
			assertThat(respuesta.getStatusCode().value()).isEqualTo(200);
			assertThat(respuesta.getBody()).isNotNull();
			assertThat(compararExamenDTO(respuesta.getBody(), examenDTOEjemplo)).isTrue();
		}

		@Test
		@DisplayName("Examen no encontrado")
		public void noDevuelveExamen() {

			var peticion = get("http", "localhost",port, "/examenes/4", token);
			var respuesta = restTemplate.exchange(peticion,
				new ParameterizedTypeReference<ExamenDTO>() {});
	
			assertThat(respuesta.getStatusCode().value()).isEqualTo(404);
			assertThat(respuesta.getBody()).isNull();
		}

		@Test
		@DisplayName("Nota del examen modificada")
		public void modificarNota() {

			Examen examenModificado = examenEjemplo;
			examenModificado.setCalificacion((float) 7.0);
			ExamenDTO examenDTOModificado = new ExamenDTO().fromExamen(examenModificado);

			var peticion = put("http", "localhost",port, "/examenes/1", examenDTOModificado, token);
		
			var respuesta = restTemplate.exchange(peticion,Void.class);
				
			assertThat(respuesta.getStatusCode().value()).isEqualTo(200);
			assertThat(respuesta.hasBody()).isEqualTo(false);
			
			var peticion2 = get("http", "localhost",port, "/examenes/1", token);
			var respuesta2 = restTemplate.exchange(peticion2,
				new ParameterizedTypeReference<ExamenDTO>() {});
				
			assertThat(respuesta2.getStatusCode().value()).isEqualTo(200);
			assertThat(respuesta2.getBody()).isNotNull();
			assertThat(compararExamenDTO(respuesta2.getBody(), examenDTOEjemplo)).isFalse();
			assertThat(compararExamenDTO(respuesta2.getBody(), examenDTOModificado)).isTrue();
		}
	}
}
