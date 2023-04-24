package sii.ms_evalexamenes;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.net.URI;
import java.util.List;
import java.util.Optional;
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
		
		private RequestEntity<Void> delete(String scheme, String host, int port, String path, String tk) {
			URI uri = uri(scheme, host,port, path);
			var peticion = RequestEntity.delete(uri)
			.header("Authorization", "Bearer " + tk)
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
	@DisplayName("Base de datos Vacia")
	public class ExamenesVacios {

		@BeforeEach
		public void initializeDatabase() {
			examenRepository.deleteAll();

		}

		/**
		 * Pruebas GET /examenes/{id}
		 */

		@Test
		@DisplayName("Devuelve 403 al acceder a un Examen Concreto NO Existente SIN Autenticacion")
		public void testgetExamen1() {
			var peticion = get("http", "localhost",port, "/examenes/1","");
			var respuesta = restTemplate.exchange(peticion,Void.class); 

			assertThat(respuesta.getStatusCode().is4xxClientError());           
			assertThat(respuesta.getStatusCode().value()).isEqualTo(403);
			assertEquals(respuesta.getHeaders().getContentLength(),0);
			assertFalse(respuesta.hasBody());
			
		}

		@Test
		@DisplayName("Devuelve 200 al acceder a un Examen Concreto SI Existente CON Autenticacion")
		public void testgetExamen2() { 
			ExamenDTO examen = new ExamenDTO(1L, 1L, 1L, 1F);
			examenRepository.save(examen.examen());

			var peticion = get("http", "localhost",port, "/examenes/1",token);
			var respuesta = restTemplate.exchange(peticion,new ParameterizedTypeReference<ExamenDTO>() {});

			assertThat(respuesta.getStatusCode().is2xxSuccessful());
			assertThat(respuesta.getStatusCode().value()).isEqualTo(200);
			assertEquals(examen.getId(),respuesta.getBody().getId());
			assertEquals(examen.getMateria(),respuesta.getBody().getMateria());
			assertEquals(examen.getCodigoAlumno(),respuesta.getBody().getCodigoAlumno());
			assertEquals(examen.getNota(),respuesta.getBody().getNota());

		}

		@Test
		@DisplayName("Devuelve 404 al acceder a un Examen Concreto NO Existente CON Autenticacion")
		public void testgetExamen3() { 
			var peticion = get("http", "localhost",port, "/examenes/1",token);
			var respuesta = restTemplate.exchange(peticion,Void.class);   
			assertThat(respuesta.getStatusCode().is4xxClientError());
			assertThat(respuesta.getStatusCode().value()).isEqualTo(404);
			assertEquals(respuesta.getHeaders().getContentLength(),0);
			assertFalse(respuesta.hasBody());

		}
		
		/**
		 * Pruebas PUT /examenes/{id}
		 */



		@Test
		@DisplayName("Devuelve 200 al modificar nota a un Examen Concreto SI Existente CON Autenticacion")
		public void testputexamenes() {
			ExamenDTO examen = new ExamenDTO(1L, 1L, 1L, 1F);
			ExamenDTO nuevoexamen = new ExamenDTO(2L, 2L, 2L, 2F);
			examenRepository.save(examen.examen());

			var peticion = put("http", "localhost",port, "/examenes/1",nuevoexamen,token);
			var respuesta = restTemplate.exchange(peticion,new ParameterizedTypeReference <List<ExamenDTO>>() {});                 
			


			Optional<Examen> examenModificado = examenRepository.findById(1L);

			assertThat(respuesta.getStatusCode().is2xxSuccessful());
			assertThat(respuesta.getStatusCode().value()).isEqualTo(200);
			assertEquals(examenModificado.get().getCalificacion(),nuevoexamen.getNota());
			assertNotEquals(examenModificado.get().getMateriaId(),nuevoexamen.getMateria());
			assertNotEquals(examenModificado.get().getId(),nuevoexamen.getId());
			assertNotEquals(examenModificado.get().getAlumnoId(),nuevoexamen.getCodigoAlumno());


		} 

		@Test
		@DisplayName("Devuelve 404 al modificar nota a un Examen Concreto NO Existente CON Autenticacion")
		public void testputexamenes1() {
			ExamenDTO nuevoexamen = new ExamenDTO(1L, 1L, 1L, 2F);

			var peticion = put("http", "localhost",port, "/examenes/1",nuevoexamen,token);
			var respuesta = restTemplate.exchange(peticion,new ParameterizedTypeReference <List<ExamenDTO>>() {});    

			assertThat(respuesta.getStatusCode().is4xxClientError());
			assertThat(respuesta.getStatusCode().value()).isEqualTo(404);
			assertEquals(respuesta.getHeaders().getContentLength(),0);
			assertFalse(respuesta.hasBody());
		} 

		@Test
		@DisplayName("Devuelve 403 al modificar nota a un Examen Concreto SI Existente SIN Autenticacion")
		public void testputexamenes2() {
			ExamenDTO examen = new ExamenDTO(1L, 1L, 1L, 1F);
			examenRepository.save(examen.examen());
			ExamenDTO nuevoexamen = new ExamenDTO(1L, 1L, 1L, 2F);

			var peticion = put("http", "localhost",port, "/examenes/1",nuevoexamen,"");
			var respuesta = restTemplate.exchange(peticion,Void.class);

			assertThat(respuesta.getStatusCode().is4xxClientError());
			assertThat(respuesta.getStatusCode().value()).isEqualTo(403);
			assertEquals(respuesta.getHeaders().getContentLength(),0);
			assertFalse(respuesta.hasBody());
		} 



		/**
		 * Pruebas GET /examenes/asignacion
		 */




		@Test
		@DisplayName("Devuelve 200 al acceder a Asignaciones CON Autenticacion")
		public void testgetasignacion1() {
			var peticion = get("http", "localhost",port, "/examenes/asignacion",token);
			var respuesta = restTemplate.exchange(peticion,new ParameterizedTypeReference <List<AsignacionDTO>>() {});  



			assertThat(respuesta.getStatusCode().is2xxSuccessful()); 
			assertThat(respuesta.getStatusCode().value()).isEqualTo(200);
			assertThat(respuesta.hasBody());
			assertThat(respuesta.getBody().isEmpty()); // No Existen Examenes
			

		}

		@Test
		@DisplayName("Devuelve 403 al acceder a Asignaciones SIN Autenticacion")
		public void testgetasignacion2() {
			var peticion = get("http", "localhost",port, "/examenes/asignacion","");
			var respuesta = restTemplate.exchange(peticion,Void.class); 

			assertThat(respuesta.getStatusCode().is4xxClientError());
			assertThat(respuesta.getStatusCode().value()).isEqualTo(403);
			assertEquals(respuesta.getHeaders().getContentLength(),0);
			assertFalse(respuesta.hasBody());
		}


		/**
		 * Pruebas PUT /examenes/asignacion
		 */


		@Test
		@DisplayName("Devuelve 200 al modificar una Asignacion CON Autenticacion")
		public void testputasignacion1() {
			//Añadimos Examenes 1 y 2, Asignamos Corrector 1 y 2 respectivamente 
			//Cambiarmos Corrector del Examen 2 a 1 
			//Comprobamos si los Correctores son identicos


			Examen examen1 = new Examen(1L, 1L, new Timestamp(System.currentTimeMillis()), 1L, 1L, 1L);
			Examen examen2 = new Examen(2L, 2L, new Timestamp(System.currentTimeMillis()), 2L, 2L, 2L);
			examenRepository.save(examen1);
			examenRepository.save(examen2);

			AsignacionDTO asignacion = new AsignacionDTO(1L, 2L);
			List<AsignacionDTO> asignacionList = new ArrayList<>();
			asignacionList.add(asignacion);


			var peticion = put("http", "localhost",port, "/examenes/asignacion",asignacionList,token);
			var respuesta = restTemplate.exchange(peticion,Void.class);                 
			
			Optional<Examen> examenModificado = examenRepository.findById(2L);
			assertEquals(examenModificado.get().getCorrectorId(),examen1.getCorrectorId());

			assertThat(respuesta.getStatusCode().is2xxSuccessful());
			assertThat(respuesta.getStatusCode().value()).isEqualTo(200);
			assertEquals(respuesta.getHeaders().getContentLength(),0);
			assertFalse(respuesta.hasBody());
			
		}

		@Test
		@DisplayName("Devuelve 403 al modificar una Asignacion SIN Autenticacion")
		public void testputasignacion2() {			
			AsignacionDTO asignacion = new AsignacionDTO(1L, 1L);

			List<AsignacionDTO> asignacionList = new ArrayList<>();
			asignacionList.add(asignacion);

			var peticion = put("http", "localhost",port, "/examenes/asignacion",asignacionList,"");
			var respuesta = restTemplate.exchange(peticion, new ParameterizedTypeReference <List<AsignacionDTO>>() {}); 
			assertThat(respuesta.getStatusCode().is4xxClientError());
			assertThat(respuesta.getStatusCode().value()).isEqualTo(403);
			assertEquals(respuesta.getHeaders().getContentLength(),0);
			assertFalse(respuesta.hasBody());
			
		}

		
		/**
		 * Pruebas POST /notificaciones/notas
		 */

		@Test
		@DisplayName("Devuelve 200 al añadir notificaciones notas CON Autenticacion")
		public void postNotificacionesNotas() {

			NotificacionNotasDTO notificacion = new NotificacionNotasDTO(
			"Asunto", 
			"Cuerpo", 
			LocalDateTime.now(), 
			new ArrayList<String>(Arrays.asList("SMS"))
			);

			var peticion = post("http", "localhost",port, "/notificaciones/notas", notificacion, token);
			var respuesta = restTemplate.exchange(peticion,Void.class);
			
			assertThat(respuesta.getStatusCode().is2xxSuccessful());
			assertThat(respuesta.getStatusCode().value()).isEqualTo(201);
			assertFalse(respuesta.hasBody());


			

		}

		@Test
		@DisplayName("Devuelve 403 al añadir notificaciones/notas SIN Autenticacion")
		public void postNotificacionesNotas1() {

			NotificacionNotasDTO notificacion = new NotificacionNotasDTO(
			"Asunto", 
			"Cuerpo", 
			LocalDateTime.now(), 
			new ArrayList<String>(Arrays.asList("SMS"))
			);

			var peticion = post("http", "localhost",port, "/notificaciones/notas", notificacion, "");
			var respuesta = restTemplate.exchange(peticion,Void.class);
										
			assertThat(respuesta.getStatusCode().is4xxClientError());
			assertThat(respuesta.getStatusCode().value()).isEqualTo(403);
			assertEquals(respuesta.getHeaders().getContentLength(),0);
			assertFalse(respuesta.hasBody());

		}		


		/**
		 * Pruebas POST /examenes
		 */

		@Test
		@DisplayName("Devuelve 200 al añadir un Examen CON Autenticacion")
		public void testpostExamen() { 
			
			ExamenNuevoDTO examen = new ExamenNuevoDTO(1L, 1L);
			var peticion = post("http", "localhost",port, "/examenes",examen,token);
			var respuesta = restTemplate.exchange(peticion,new ParameterizedTypeReference<ExamenDTO>() {});

			assertThat(respuesta.getStatusCode().is2xxSuccessful());
			assertThat(respuesta.getStatusCode().value()).isEqualTo(201);
			assertEquals(respuesta.getHeaders().getContentLength(),0);
			assertFalse(respuesta.hasBody());


		}
		
		@Test
		@DisplayName("Devuelve 403 al añadir un Examen SIN Autenticacion")
		public void testpostExamen1() { 
			ExamenNuevoDTO examen = new ExamenNuevoDTO(1L, 1L);
			var peticion = post("http", "localhost",port, "/examenes",examen,"");
			var respuesta = restTemplate.exchange(peticion,Void.class);

			assertThat(respuesta.getStatusCode().is4xxClientError());
			assertThat(respuesta.getStatusCode().value()).isEqualTo(403);
			assertEquals(respuesta.getHeaders().getContentLength(),0);
			assertFalse(respuesta.hasBody());

		}


		/**
		 * Pruebas GET /notas
		 * @throws URISyntaxException
		 */
		
		
		 @Test
		@DisplayName("Devuelve 200 al acceder a las Notas de un estudiante CON Autenticacion")
		public void testgetnotas(){ 
			Examen examenEjemplo = new Examen(1L, (float)5.0, new Timestamp(System.currentTimeMillis()), 1L,  1L, 1L);
			examenRepository.save(examenEjemplo);   

			var peticion = get("http", "localhost",port, "/notas", token, "1", "rodriguez");
			var respuesta = restTemplate.exchange(peticion,new ParameterizedTypeReference<List<ExamenDTO>>() {});
	
			
			assertThat(respuesta.getStatusCode().value()).isEqualTo(200);
			assertThat(respuesta.getBody().size()).isEqualTo(1);
			assertThat(compararExamenDTO(respuesta.getBody().get(0), ExamenDTO.fromExamen(examenEjemplo))).isTrue();
	 		
		}

		

		@Test
		@DisplayName("Devuelve 404 al acceder a las Notas de un estudiante CON Autenticacion")
		public void testgetnotas1() { 

			
			var peticion = get("http", "localhost",port, "/notas", token, "1", "Cocainomano");
			var respuesta = restTemplate.exchange(peticion,new ParameterizedTypeReference<List<ExamenDTO>>() {});

			assertThat(respuesta.getStatusCode().is4xxClientError());
			assertThat(respuesta.getStatusCode().value()).isEqualTo(404);
			assertEquals(respuesta.getHeaders().getContentLength(),0);
			assertFalse(respuesta.hasBody());
		}

		/**
		 * Pruebas GET /examenes/correcciones
		 */
		
		 @Test
		 @DisplayName("Devuelve 200 (al acceder a las correcciones) gget /examenes/correciones CON Autenticacion")
		 public void getCorrecciones() {
			var peticion = get("http", "localhost", port, "/examenes/correcciones", token);
			var respuesta = restTemplate.exchange(peticion,new ParameterizedTypeReference<EstadoCorrecionesDTO>() {});
			 

			assertThat(respuesta.getStatusCode().is2xxSuccessful());
			assertThat(respuesta.getStatusCode().value()).isEqualTo(200);
			assertThat(respuesta.hasBody());
			assertThat(respuesta.getBody().getPendientes().isEmpty());
			assertThat(respuesta.getBody().getCorregidos().isEmpty());
		 }

		 @Test
		 @DisplayName("Devuelve 403 (Acceso denegado) get /examenes/correciones SIN Autenticacion")
		 public void getCorrecciones1() {
			var peticion = get("http", "localhost", port, "/examenes/correcciones", "");
			var respuesta = restTemplate.exchange(peticion,new ParameterizedTypeReference<EstadoCorrecionesDTO>() {});
			 

			assertThat(respuesta.getStatusCode().is4xxClientError());
			assertThat(respuesta.getStatusCode().value()).isEqualTo(403);
			assertEquals(respuesta.getHeaders().getContentLength(),0);
			assertFalse(respuesta.hasBody());
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
		@DisplayName("Devuelve 200 (Examen encontrado) get /examenes/{id} CON Autorizacion")
		public void devuelveExamen() {

			var peticion = get("http", "localhost",port, "/examenes/1", token);
			var respuesta = restTemplate.exchange(peticion,
				new ParameterizedTypeReference<ExamenDTO>() {});
			
			assertThat(respuesta.getStatusCode().value()).isEqualTo(200);
			assertThat(respuesta.getBody()).isNotNull();
			assertThat(compararExamenDTO(respuesta.getBody(), examenDTOEjemplo)).isTrue();
		}

		@Test
		@DisplayName("Devuelve 404 (Examen no encontrado) get /examenes/{id} CON Autorizacion")
		public void noDevuelveExamen() {

			var peticion = get("http", "localhost",port, "/examenes/9999", token);
			var respuesta = restTemplate.exchange(peticion,
				new ParameterizedTypeReference<ExamenDTO>() {});
	
			assertThat(respuesta.getStatusCode().value()).isEqualTo(404);
			assertThat(respuesta.getBody()).isNull();
		}

		@Test
		@DisplayName("Devuelve 200 (Nota Examen Modificada) put /examenes/{id} CON Autorizacion")
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
		@DisplayName("Devuelve 200 (Asignacion encontrada) get /examenes/asignacion CON Autorizacion")
		public void asignacionEncontrada() {
			var peticion = get("http", "localhost",port, "/examenes/asignacion", token);
			var respuesta = restTemplate.exchange(peticion,
				new ParameterizedTypeReference<List<AsignacionDTO>>() {});
			
			assertThat(respuesta.getStatusCode().value()).isEqualTo(200);
			assertThat(compararAsignacionDTO(respuesta.getBody().get(0), AsignacionDTO.fromExamen(examenEjemplo[0]))).isTrue();
			assertThat(respuesta.getBody().size()).isEqualTo(examenEjemplo.length);
		}

		@Test
		@DisplayName("Devuelve 200 (Modificiacion Correcta) put /examenes/asignacion CON Autorizacion")
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

		@Test
		@DisplayName("Devuelve 200 (Examen Añadido) Post /examenes CON autorizacion")
		public void postExamen() {
			ExamenNuevoDTO examenNuevoDTO = new ExamenNuevoDTO(99L, 99L);
			
			var peticion = post("http", "localhost",port, "/examenes", examenNuevoDTO, token);
			var respuesta = restTemplate.exchange(peticion,Void.class);
			
			assertTrue(respuesta.getStatusCode().value() == 201,
			"El microservicio 'Gestion de correctores' debe estar levantado para pasar este Test."); 
			assertThat(respuesta.hasBody()).isEqualTo(false);
		}
		
		@Test
		@DisplayName("Devuelve 200 (Correciones Disponibles) Get /examenes/correcciones CON autorizacion")
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

		@Test
		@DisplayName("Devuelve 200 al añadir un Examen CON Autenticacion")
		public void testpostExamen() { 

			String payload = "{\"identificadorUsuario\": 99, \"identificadorConvocatoria\": 1, \"telefono\": \"123456789\", \"materia\": {\"id\": 1, \"nombre\": \"Matematicas\"}, \"maximasCorrecciones\": 20}";
			var peticion0 = post("http", "localhost", 8081, "/correctores", payload, token);
			var respuesta0 = restTemplate.exchange(peticion0, Void.class);
			assertThat(respuesta0.getStatusCode().value()).isEqualTo(201);
			String pathID = respuesta0.getHeaders().get("Location").get(0).substring(respuesta0.getHeaders().get("Location").get(0).length()-1);



			ExamenNuevoDTO examen = new ExamenNuevoDTO(1L, 99L);
			var peticion = post("http", "localhost",port, "/examenes",examen,token);
			var respuesta1 = restTemplate.exchange(peticion,new ParameterizedTypeReference<ExamenDTO>() {});
			assertThat(respuesta1.getStatusCode().is2xxSuccessful());
			assertThat(respuesta1.getStatusCode().value()).isEqualTo(201);
			var respuesta2 = restTemplate.exchange(peticion,new ParameterizedTypeReference<ExamenDTO>() {});

			assertThat(respuesta2.getStatusCode().is2xxSuccessful());
			assertThat(respuesta2	.getStatusCode().value()).isEqualTo(201);
			assertEquals(respuesta2.getHeaders().getContentLength(),0);
			assertFalse(respuesta2.hasBody());
			
			var peticion3 = delete("http", "localhost", 8081, "/correctores/" + pathID, token);
			var respuesta3 = restTemplate.exchange(peticion3,Void.class);
			assertThat(respuesta3	.getStatusCode().value()).isEqualTo(200);

		}

		@Test
		@DisplayName("Devuelve 409 al añadir un Examen CON Autenticacion")
		public void testpostExamenDemasiados() { 

			String payload = "{\"identificadorUsuario\": 99, \"identificadorConvocatoria\": 1, \"telefono\": \"123456789\", \"materia\": {\"id\": 1, \"nombre\": \"Matematicas\"}, \"maximasCorrecciones\": -1}";
			var peticion0 = post("http", "localhost", 8081, "/correctores", payload, token);
			var respuesta0 = restTemplate.exchange(peticion0, Void.class);
			assertThat(respuesta0.getStatusCode().value()).isEqualTo(201);

			String pathID = respuesta0.getHeaders().get("Location").get(0).substring(respuesta0.getHeaders().get("Location").get(0).length()-1);

			ExamenNuevoDTO examen = new ExamenNuevoDTO(1L, 99L);
			var peticion = post("http", "localhost",port, "/examenes",examen,token);
			var respuesta1 = restTemplate.exchange(peticion,new ParameterizedTypeReference<ExamenDTO>() {});
			assertThat(respuesta1.getStatusCode().is2xxSuccessful());
			assertThat(respuesta1.getStatusCode().value()).isEqualTo(201);
			var respuesta2 = restTemplate.exchange(peticion,new ParameterizedTypeReference<ExamenDTO>() {});

			assertThat(respuesta2.getStatusCode().is2xxSuccessful());
			assertThat(respuesta2	.getStatusCode().value()).isEqualTo(201);
			assertEquals(respuesta2.getHeaders().getContentLength(),0);
			assertFalse(respuesta2.hasBody());
			var peticion3 = delete("http", "localhost", 8081, "/correctores/" + pathID, token);
			var respuesta3 = restTemplate.exchange(peticion3,Void.class);
			assertThat(respuesta3	.getStatusCode().value()).isEqualTo(200);
		}
	}

	@Nested
	@DisplayName("Tests Notificaciones")
	public class notificacionesTests {

		@Test
		@DisplayName("Devuelve 200 (Medios SI contemplados) Post /notificaciones/nostas CON autorizacion")
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

		@Test
		@DisplayName("Devuelve 400 (Medios SI contemplados) Post /notificaciones/nostas CON autorizacion")
		public void notificacionesTodosMedios() {
			NotificacionNotasDTO notificacion = 
				new NotificacionNotasDTO(	"Asunto", 
											"Cuerpo", 
											LocalDateTime.now(), 
											new ArrayList<String>(Arrays.asList("MySpace"))
										);

			var peticion = post("http", "localhost",port, "/notificaciones/notas", notificacion, token);
			var respuesta = restTemplate.exchange(peticion,Void.class);
			
			assertThat(respuesta.getStatusCode().value()).isEqualTo(400);
		}

	}

	@Nested
	@DisplayName("Tests GET notas")
	public class notasTests {

		Examen examenEjemplo = new Examen(1L, (float)5.0, new Timestamp(System.currentTimeMillis()), 1L,  1L, 1L);
		
		@BeforeEach
		public void aniadirDatos() {
			examenRepository.save(examenEjemplo);    
		}
		
		@Test
		@DisplayName("Devuelve 200  Get /notas?dni=1&apellido=rodriguez de examenes SI Existentes CON autorizacion")
		public void getNotas() {
			var peticion = get("http", "localhost",port, "/notas", token, "1", "rodriguez");
			var respuesta = restTemplate.exchange(peticion,
			new ParameterizedTypeReference<List<ExamenDTO>>() {});


			
			assertThat(respuesta.getStatusCode().value()).isEqualTo(200);
			assertThat(respuesta.getBody().size()).isEqualTo(1);
			assertThat(compararExamenDTO(respuesta.getBody().get(0), ExamenDTO.fromExamen(examenEjemplo))).isTrue();
		}
	}

	@Nested
	@DisplayName("Pruebas 403 Token no Validos ")
	public class noAutorizacion{
		@Test
		@DisplayName("Devuelve 403 Get /examen/{id} SIN autenticacion")
		public void getExamen() {
			var peticion = get("http", "localhost",port, "/examenes/1", "");
			var respuesta = restTemplate.exchange(peticion, Void.class);
			assertThat(respuesta.getStatusCode().value()).isEqualTo(403);
		}
		
		@Test
		@DisplayName("Devuelve 403 Put /examen/{id} SIN autenticacion")
		public void putExamen() {
			var peticion = put("http", "localhost",port, "/examenes/1", new ExamenDTO(), "");
			var respuesta = restTemplate.exchange(peticion, Void.class);
			assertThat(respuesta.getStatusCode().value()).isEqualTo(403);
		}

		@Test
		@DisplayName("Devuelve 403 Get examenes asignacion SIN autenticacion")
		public void getExamenesAsignacion() {
			var peticion = get("http", "localhost",port, "/examenes/asignacion", "");
			var respuesta = restTemplate.exchange(peticion, Void.class);
			assertThat(respuesta.getStatusCode().value()).isEqualTo(403);
		}
		
		@Test
		@DisplayName("Devuelve 403 Put /examenes asignacion SIN autenticacion")
		public void putExamenesAsignacion() {
			var peticion = put("http", "localhost",port, "/examenes/asignacion", new ArrayList<AsignacionDTO>(), "");
			var respuesta = restTemplate.exchange(peticion, Void.class);
			assertThat(respuesta.getStatusCode().value()).isEqualTo(403);
		}

		@Test
		@DisplayName("Devuelve 403 Post /notificaciones/notas SIN autenticacion")
		public void postNotas() {
			var peticion = post("http", "localhost",port, "/notificaciones/notas", new NotificacionNotasDTO(), "");
			var respuesta = restTemplate.exchange(peticion, Void.class);
			assertThat(respuesta.getStatusCode().value()).isEqualTo(403);
		}

		@Test
		@DisplayName("Devuelve 403 Post /examenes SIN autenticacion")
		public void postExamene() {
			var peticion = post("http", "localhost",port, "/examenes", new ExamenNuevoDTO(), "");
			var respuesta = restTemplate.exchange(peticion, Void.class);
			assertThat(respuesta.getStatusCode().value()).isEqualTo(403);
		}

		@Test
		@DisplayName("Devuelve 403 Get /examenes/correcciones SIN autenticacion")
		public void getCorrecciones() {
			var peticion = get("http", "localhost",port, "/examenes/correcciones", "");
			var respuesta = restTemplate.exchange(peticion, Void.class);
			assertThat(respuesta.getStatusCode().value()).isEqualTo(403);
		}
	}
}
