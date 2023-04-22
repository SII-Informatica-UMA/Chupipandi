package sii.ms_evalexamenes;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.sql.Timestamp;
import java.time.LocalDateTime;

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
// import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriBuilderFactory;

import sii.ms_evalexamenes.util.JwtGenerator;
import sii.ms_evalexamenes.dtos.ExamenDTO;
import sii.ms_evalexamenes.dtos.ExamenNuevoDTO;
import sii.ms_evalexamenes.dtos.AsignacionDTO;
import sii.ms_evalexamenes.dtos.EstadoCorrecionesDTO;
import sii.ms_evalexamenes.dtos.NotificacionNotasDTO;
import sii.ms_evalexamenes.entities.Examen;
import sii.ms_evalexamenes.repositories.ExamenRepository;


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

	// String token = "eyJhbGciOiJIUzI1NiJ9.eyJleHAiOjE2ODQ1MDI1ODgsInJvbGVzIjpbIkNPUlJFQ1RPUiJdfQ.o6KxDDdxhnGwzvNom1n8pZ9KwUUNKBogLgASOrLQoYI";
	String token = JwtGenerator.createToken("user", 24*30*2, "CORRECTOR", "VICERRECTORADO");

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

	private URI uri(String scheme, String host, String dni, String ap, int port, String ...paths) {
		UriBuilderFactory ubf = new DefaultUriBuilderFactory();
		UriBuilder ub = ubf.builder()
				.scheme(scheme)
				.queryParam("dni", dni)
				.queryParam("apellido", ap)
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

	private RequestEntity<Void> get(String scheme, String host, int port, String path, String tk, String dni, String ap) {
		URI uri = uri(scheme, host, dni, ap, port, path);
		var peticion = RequestEntity.get(uri)
			.header("Authorization", "Bearer " + tk)
			.accept(MediaType.APPLICATION_JSON)
			.build();
		return peticion;
	}
	
	// private RequestEntity<Void> delete(String scheme, String host, int port, String path) {
	// 	URI uri = uri(scheme, host,port, path);
	// 	var peticion = RequestEntity.delete(uri)
	// 		.build();
	// 	return peticion;
	// }
	
	private <T> RequestEntity<T> post(String scheme, String host, int port, String path, T object, String tk) {
		URI uri = uri(scheme, host,port, path);
		var peticion = RequestEntity.post(uri)
			.header("Authorization", "Bearer " + tk)
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
	}

	public boolean compararAsignacionDTO(AsignacionDTO asignacion1, AsignacionDTO asignacion2) {
		return asignacion1.getIdCorrector().equals(asignacion2.getIdCorrector()) &&
				asignacion1.getIdExamen() == asignacion2.getIdExamen();
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
	@DisplayName("Tests Examenes cuando hay examenes")
	public class ExamenesLlenos {
		Examen examenEjemplo[] = {
			new Examen(1L, (float)5.0, new Timestamp(System.currentTimeMillis()), 1L,  1L, 1L),
			new Examen(2L, (float)2.0, new Timestamp(System.currentTimeMillis()), 2L,  2L, 2L),
			new Examen(3L, (float)3.0, new Timestamp(System.currentTimeMillis()), 3L,  3L, 3L),
			new Examen(4L, (float)4.0, new Timestamp(System.currentTimeMillis()), 4L,  4L, 4L),
			new Examen(5L, (float)5.0, new Timestamp(System.currentTimeMillis()), 5L,  5L, 5L),
			new Examen(6L, (float)6.0, new Timestamp(System.currentTimeMillis()), 6L,  6L, 6L)
		};
		ExamenDTO examenDTOEjemplo = ExamenDTO.fromExamen(examenEjemplo[0]);
		
		@BeforeEach
		public void aniadirDatos() {
			examenRepository.save(examenEjemplo[0]);    
			examenRepository.save(examenEjemplo[1]);    
			examenRepository.save(examenEjemplo[2]);    
			examenRepository.save(examenEjemplo[3]);    
			examenRepository.save(examenEjemplo[4]);    
			examenRepository.save(examenEjemplo[5]);    
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

			var peticion = get("http", "localhost",port, "/examenes/9999", token);
			var respuesta = restTemplate.exchange(peticion,
				new ParameterizedTypeReference<ExamenDTO>() {});
	
			assertThat(respuesta.getStatusCode().value()).isEqualTo(404);
			assertThat(respuesta.getBody()).isNull();
		}

		@Test
		@DisplayName("Nota del examen modificada")
		public void modificarNota() {

			Examen examenModificado = examenEjemplo[0];
			examenModificado.setCalificacion((float) 7.0);
			ExamenDTO examenDTOModificado = ExamenDTO.fromExamen(examenModificado);

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

		@Test
		@DisplayName("Asignacion encontrada")
		public void asignacionEncontrada() {
			var peticion = get("http", "localhost",port, "/examenes/asignacion", token);
			var respuesta = restTemplate.exchange(peticion,
				new ParameterizedTypeReference<List<AsignacionDTO>>() {});
			
			assertThat(respuesta.getStatusCode().value()).isEqualTo(200);
			assertThat(compararAsignacionDTO(respuesta.getBody().get(0), AsignacionDTO.fromExamen(examenEjemplo[0]))).isTrue();
			assertThat(respuesta.getBody().size()).isEqualTo(examenEjemplo.length);
		}

		@Test
		@DisplayName("Modificar asignacion")
		public void modificarAsignacion() {
			AsignacionDTO asignacionModificada = AsignacionDTO.fromExamen(examenEjemplo[0]);
			asignacionModificada.setIdCorrector(9999L);
			
			ArrayList<AsignacionDTO> asignaciones = new ArrayList<AsignacionDTO>();
			asignaciones.add(asignacionModificada);
			
			var peticion = put("http", "localhost",port, "/examenes/asignacion", asignaciones, token);
			var respuesta = restTemplate.exchange(peticion,Void.class);
			
			assertThat(respuesta.getStatusCode().value()).isEqualTo(200);
			assertThat(respuesta.hasBody()).isEqualTo(false);
			
			var peticion2 = get("http", "localhost",port, "/examenes/asignacion", token);
			var respuesta2 = restTemplate.exchange(peticion2,
				new ParameterizedTypeReference<List<AsignacionDTO>>() {});
			
			assertThat(respuesta2.getStatusCode().value()).isEqualTo(200);
			assertThat(compararAsignacionDTO(respuesta2.getBody().get(0), asignacionModificada)).isTrue();
		}

		// @Test	// TODO - Internal server error - 500
		// @DisplayName("Post examen")
		// public void postExamen() {
		// 	ExamenNuevoDTO examenNuevoDTO = new ExamenNuevoDTO(99L, 99L);
			
		// 	var peticion = post("http", "localhost",port, "/examenes", examenNuevoDTO, token);
		// 	var respuesta = restTemplate.exchange(peticion,Void.class);
			
		// 	assertThat(respuesta.getStatusCode().value()).isEqualTo(201);
		// 	assertThat(respuesta.hasBody()).isEqualTo(false);
		// }
		
		@Test
		@DisplayName("Get Estado correciones")
		public void getCorrecciones() {
			Examen examenModificado = examenEjemplo[0];
			examenModificado.setCalificacion(null);
			ExamenDTO examenDTOModificado = ExamenDTO.fromExamen(examenModificado);

			var peticion = put("http", "localhost",port, "/examenes/1", examenDTOModificado, token);
		
			var respuesta = restTemplate.exchange(peticion,Void.class);
				
			assertThat(respuesta.getStatusCode().value()).isEqualTo(200);
			assertThat(respuesta.hasBody()).isEqualTo(false);

			var peticion2 = get("http", "localhost", port, "/examenes/correcciones", token);
			var respuesta2 = restTemplate.exchange(peticion2,
				new ParameterizedTypeReference<EstadoCorrecionesDTO>() {});
			
			assertThat(respuesta2.getStatusCode().value()).isEqualTo(200);
			assertThat(respuesta2.getBody().getCorregidos().size()).isEqualTo(examenEjemplo.length - 1);
			assertThat(respuesta2.getBody().getPendientes().size()).isEqualTo(1);
		}
	}

	@Nested
	@DisplayName("Tests Notificaciones")
	public class notificacionesTests {
		@Test
		@DisplayName("Post notificacion de notas")
		public void notificaciones() {
			NotificacionNotasDTO notificacion = 
				new NotificacionNotasDTO(	"Asunto", 
											"Cuerpo", 
											LocalDateTime.now(), 
											new ArrayList<String>(Arrays.asList("SMS"))
										);

			var peticion = post("http", "localhost",port, "/notificaciones/notas", notificacion, token);
			var respuesta = restTemplate.exchange(peticion,Void.class);
			
			assertThat(respuesta.getStatusCode().value()).isEqualTo(201);
			assertThat(respuesta.hasBody()).isEqualTo(false);
		}

	}

	@Nested
	@DisplayName("Tests notas")
	public class notasTests {

		Examen examenEjemplo = new Examen(1L, (float)5.0, new Timestamp(System.currentTimeMillis()), 1L,  1L, 1L);
		
		@BeforeEach
		public void aniadirDatos() {
			examenRepository.save(examenEjemplo);    
		}
		
		@Test	// TODO - No encuentra la nota
		@DisplayName("Get notas por dni y apellidos")
		public void getNotas() {
			var peticion = get("http", "localhost",port, "/notas", token, "1", "rodriguez");
			var respuesta = restTemplate.exchange(peticion,
			new ParameterizedTypeReference<List<ExamenDTO>>() {});
			// Void.class);
			
			assertThat(respuesta.getStatusCode().value()).isEqualTo(200);
			assertThat(respuesta.getBody().size()).isEqualTo(1);
			assertThat(compararExamenDTO(respuesta.getBody().get(0), ExamenDTO.fromExamen(examenEjemplo))).isTrue();
		}
	}

	@Nested
	@DisplayName("Tests sin autorizacion")
	public class noAutorizacion{
		@Test
		@DisplayName("Get examen")
		public void getExamen() {
			var peticion = get("http", "localhost",port, "/examenes/1", "");
			var respuesta = restTemplate.exchange(peticion, Void.class);
			assertThat(respuesta.getStatusCode().value()).isEqualTo(403);
		}
		
		@Test
		@DisplayName("Put examen")
		public void putExamen() {
			var peticion = put("http", "localhost",port, "/examenes/1", new ExamenDTO(), "");
			var respuesta = restTemplate.exchange(peticion, Void.class);
			assertThat(respuesta.getStatusCode().value()).isEqualTo(403);
		}

		@Test
		@DisplayName("Get examenes asignacion")
		public void getExamenesAsignacion() {
			var peticion = get("http", "localhost",port, "/examenes/asignacion", "");
			var respuesta = restTemplate.exchange(peticion, Void.class);
			assertThat(respuesta.getStatusCode().value()).isEqualTo(403);
		}
		
		@Test
		@DisplayName("Put examenes asignacion")
		public void putExamenesAsignacion() {
			var peticion = put("http", "localhost",port, "/examenes/asignacion", new ArrayList<AsignacionDTO>(), "");
			var respuesta = restTemplate.exchange(peticion, Void.class);
			assertThat(respuesta.getStatusCode().value()).isEqualTo(403);
		}

		@Test
		@DisplayName("Post notificacion de notas")
		public void postNotas() {
			var peticion = post("http", "localhost",port, "/notificaciones/notas", new NotificacionNotasDTO(), "");
			var respuesta = restTemplate.exchange(peticion, Void.class);
			assertThat(respuesta.getStatusCode().value()).isEqualTo(403);
		}

		@Test
		@DisplayName("Post examenes")
		public void postExamene() {
			var peticion = post("http", "localhost",port, "/examenes", new ExamenNuevoDTO(), "");
			var respuesta = restTemplate.exchange(peticion, Void.class);
			assertThat(respuesta.getStatusCode().value()).isEqualTo(403);
		}

		@Test
		@DisplayName("Get correcciones")
		public void getCorrecciones() {
			var peticion = get("http", "localhost",port, "/examenes/correcciones", "");
			var respuesta = restTemplate.exchange(peticion, Void.class);
			assertThat(respuesta.getStatusCode().value()).isEqualTo(403);
		}
	}
}