package sii.ms_corrector;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.BeforeAll;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriBuilderFactory;

import sii.ms_corrector.entities.*;
import sii.ms_corrector.repositories.*;
import sii.ms_corrector.util.JwtGenerator;
import sii.ms_corrector.dtos.*;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DisplayName("En el servicio de corrector:")
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
class CorrectorTests {
	final static Logger LOG = Logger.getLogger("test.CorrectorTests");
	
	@Autowired
	private TestRestTemplate restTemplate;
	
	@Value(value="${local.server.port}")
	private int port;
	
	@Autowired
	private CorrectorRepository correctorRepo;
    // @Autowired
    // private MateriaEnConvocatoriaRepository matConvRepo;
    // @Autowired
    // private MateriaRepository matRepo;

	String tokenValido = JwtGenerator.createToken("user", 5, "VICERRECTORADO");		// valido por 5 horas
	// Los tokens tokenCaducado y tokenNoAuth son equivalentes. No se distingue el error de que un token
	// haya expirado del error de que un token no tenga los permisos (roles) adecuados.
	// Ambos devuelven el codigo 403.
    String tokenCaducado = JwtGenerator.createToken("user", -1, "VICERRECTORADO");				// caducado hace 1 hora
    String tokenNoAuth = JwtGenerator.createToken("user", 5, "CORRECTOR");				// rol incorrecto

	@BeforeAll
	public static void inicializarLogger() throws SecurityException, IOException {
		FileHandler fileTxt = new FileHandler("src/test/TestCorrectorLogger.txt");
		SimpleFormatter formatterTxt = new SimpleFormatter();
		fileTxt.setFormatter(formatterTxt);
		LOG.addHandler(fileTxt);
	}
	
	@BeforeEach
	public void initializeDatabase() {
		correctorRepo.deleteAll();
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

	// Para poder añadir un query param a la URI, utilizamos este metodo
	private URI uriConv(String scheme, String host, int port, Long conv, String ...paths) {
		UriBuilderFactory ubf = new DefaultUriBuilderFactory();
		UriBuilder ub = ubf.builder()
				.scheme(scheme)
				.queryParam("idConvocatoria", conv)
				.host(host).port(port);
		for (String path: paths) {
			ub = ub.path(path);
		}
		return ub.build();
	}
	
	private RequestEntity<Void> get(String scheme, String host, int port, String path, String token) {
		URI uri = uri(scheme, host, port, path);
		var peticion = RequestEntity.get(uri)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
			.accept(MediaType.APPLICATION_JSON)
			.build();
		return peticion;
	}

	// Para poder realizar una peticion GET con un query param, utilizamos este metodo
	private RequestEntity<Void> getConv(String scheme, String host, int port, String path, Long conv, String token) {
		URI uri = uriConv(scheme, host, port, conv, path);
		var peticion = RequestEntity.get(uri)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
			.accept(MediaType.APPLICATION_JSON)
			.build();
		return peticion;
	}
	
	private RequestEntity<Void> delete(String scheme, String host, int port, String path, String token) {
		URI uri = uri(scheme, host, port, path);
		var peticion = RequestEntity.delete(uri)
		    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
			.build();
		return peticion;
	}
	
	private <T> RequestEntity<T> post(String scheme, String host, int port, String path, T object, String token) {
		URI uri = uri(scheme, host, port, path);
		var peticion = RequestEntity.post(uri)
		    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
			.contentType(MediaType.APPLICATION_JSON)
			.body(object);
		return peticion;
	}
	
	private <T> RequestEntity<T> put(String scheme, String host, int port, String path, T object, String token) {
		URI uri = uri(scheme, host, port, path);
		var peticion = RequestEntity.put(uri)
		    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
			.contentType(MediaType.APPLICATION_JSON)
			.body(object);
		return peticion;
	}
	
	private void compruebaCamposDTO(CorrectorNuevoDTO expected, CorrectorNuevoDTO actual) {
		assertThat(actual.getIdentificadorUsuario()).isEqualTo(expected.getIdentificadorUsuario());
		assertThat(actual.getTelefono()).isEqualTo(expected.getTelefono());
		assertThat(actual.getMaximasCorrecciones()).isEqualTo(expected.getMaximasCorrecciones());

		// Al convertir una entidad Corrector a un CorrectorNuevoDTO para compararlos, estos atributos no estan disponibles
		assertThat(actual.getIdentificadorConvocatoria()).isEqualTo(expected.getIdentificadorConvocatoria());
		// No debemos comprobar materia, pues solamente se usa internamente para guardar en la BD
		// un corrector a traves del POST y asociar dicha materia a su lista de materias en convocatoria
		// (un CorrectorNuevoDTO la tiene, pero un Corrector almacenado en la base de datos no)
		// assertThat(actual.getMateria()).isEqualTo(expected.getMateria());
	}

	private void compruebaCampos(CorrectorNuevoDTO expected, Corrector actual) {
		assertThat(expected.getIdentificadorUsuario()).isEqualTo(actual.getIdUsuario());
		assertThat(actual.getMatEnConv().stream().anyMatch(materia -> materia.getIdConvocatoria() == expected.getIdentificadorConvocatoria())).isTrue();
		assertThat(expected.getMaximasCorrecciones()).isEqualTo(actual.getMaximasCorrecciones());
		assertThat(expected.getTelefono()).isEqualTo(actual.getTelefono());
	}
	
	@Nested
	@DisplayName("Cuando la base de datos esta vacia:")
	public class BaseDatosVacia {
        // get un corrector por id
        @Test
        @DisplayName("acceder a un corrector que no existe")
        public void errorGetCorrector() {
            var peticion = get("http", "localhost", port, "/correctores/1", tokenValido);
            var respuesta = restTemplate.exchange(peticion,
                    new ParameterizedTypeReference<CorrectorDTO>() {});
			
            assertThat(respuesta.getStatusCode().value()).isEqualTo(404);
			assertThat(respuesta.getBody()).isNull();
        }

		// get un corrector por id
        @Test
        @DisplayName("acceder a un corrector sin permisos")
        public void getCorrectorNoAutorizacion() {
            var peticion = get("http", "localhost", port, "/correctores/1", tokenCaducado);
			var respuesta = restTemplate.exchange(peticion, Void.class);
			
            assertThat(respuesta.getStatusCode().value()).isEqualTo(403);
        }

        // put un corrector por id
		@Test
        @DisplayName("actualizar un corrector que no existe")
        public void errorPutCorrector() {
            var corrector = CorrectorNuevoDTO.builder().maximasCorrecciones(20).build();
            var peticion = put("http", "localhost", port, "/correctores/1", corrector, tokenValido);
            var respuesta = restTemplate.exchange(peticion,Void.class);

			assertThat(respuesta.getStatusCode().value()).isEqualTo(404);

			List<Corrector> correctorBD = correctorRepo.findAll();
			assertThat(correctorBD).isEmpty();
        }

		@Test
        @DisplayName("actualizar un corrector sin permisos")
        public void putCorrectorNoAutorizacion() {
            var corrector = CorrectorNuevoDTO.builder().maximasCorrecciones(20).build();
            var peticion = put("http", "localhost", port, "/correctores/1", corrector, tokenCaducado);
            var respuesta = restTemplate.exchange(peticion, Void.class);

			assertThat(respuesta.getStatusCode().value()).isEqualTo(403);

			List<Corrector> correctorBD = correctorRepo.findAll();
			assertThat(correctorBD).isEmpty();
        }

        // delete un corrector por id
		@Test
		@DisplayName("eliminar un corrector que no existe")
		public void errorEliCorrector() {
			var peticion = delete("http", "localhost", port, "/correctores/1", tokenValido);
			var respuesta = restTemplate.exchange(peticion,Void.class);

			assertThat(respuesta.getStatusCode().value()).isEqualTo(404);
			assertThat(respuesta.getBody()).isNull();
		}

		@Test
		@DisplayName("eliminar un corrector sin permisos")
		public void deleteCorrectorNoAutorizacion() {
			var peticion = delete("http", "localhost", port, "/correctores/1", tokenCaducado);
			var respuesta = restTemplate.exchange(peticion, Void.class);

			assertThat(respuesta.getStatusCode().value()).isEqualTo(403);
		}

        // get todos los correctores sin especificar convocatoria
		@Test
		@DisplayName("devuelve lista vacia de correctores")
		public void listaVaciaCorrectores() {
			var peticion = get("http", "localhost", port, "/correctores", tokenValido);
			var respuesta = restTemplate.exchange(peticion,
					new ParameterizedTypeReference<List<CorrectorDTO>>(){});
			
			assertThat(respuesta.getStatusCode().value()).isEqualTo(200);
			assertThat(respuesta.getBody()).isEmpty();
		}

		@Test
		@DisplayName("acceder a la lista de correctores sin permisos")
		public void getCorrectoresNoAutorizacion() {
			var peticion = get("http", "localhost", port, "/correctores", tokenCaducado);
			var respuesta = restTemplate.exchange(peticion, Void.class);
			
			assertThat(respuesta.getStatusCode().value()).isEqualTo(403);
		}

		// get todos los correctores especificando convocatoria
		@Test
		@DisplayName("devuelve la lista vacia de correctores de una convocatoria")
		public void listaVaciaCorrectoresConv() {
			var peticion = getConv("http", "localhost", port, "/correctores", 1L, tokenValido);
			var respuesta = restTemplate.exchange(peticion,
					new ParameterizedTypeReference<List<CorrectorDTO>>(){});
			
			assertThat(respuesta.getStatusCode().value()).isEqualTo(200);
			assertThat(respuesta.getBody()).isEmpty();
		}

		@Test
		@DisplayName("acceder a los correctores de una convocatoria sin autorizacion")
		public void getCorrectoresConvNoAutorizacion() {
			var peticion = getConv("http", "localhost", port, "/correctores", 1L, tokenCaducado);
			var respuesta = restTemplate.exchange(peticion, Void.class);
			
			assertThat(respuesta.getStatusCode().value()).isEqualTo(403);
		}

        // post un corrector
		@Test
		@DisplayName("inserta correctamente un corrector")
		public void postCorrector() {
			// crear corrector
			var materia = MateriaDTO.builder().id(2L).nombre("Matematicas").build();
			var c = CorrectorNuevoDTO.builder()
									.identificadorUsuario(1L)
									.identificadorConvocatoria(1L)
									.maximasCorrecciones(20)
									.materia(materia)
									.telefono("112233445")
									.build();

			var peticion = post("http", "localhost", port, "/correctores", c, tokenValido);
			var respuesta = restTemplate.exchange(peticion, Void.class);
			compruebaRespuesta(c, respuesta);
		}

		private void compruebaRespuesta(CorrectorNuevoDTO correctorNuevoDTO, ResponseEntity<Void> respuesta) {
			assertThat(respuesta.getStatusCode().value()).isEqualTo(201);
			assertThat(respuesta.getHeaders().get("Location").get(0))
				.startsWith("http://localhost:" + port + "/correctores");
			
			List<Corrector> correctores = correctorRepo.findAll();
			assertThat(correctores).hasSize(1);
			
			Corrector corrBD = correctores.stream()
					.filter(c -> c.getIdUsuario().equals(1L))
					.findAny()
					.get();
			LOG.warning("Corrector esperado: " + correctorNuevoDTO);
			LOG.warning("Corrector esperado: " + CorrectorNuevoDTO.fromCorrector(corrBD));
			
			assertThat(respuesta.getHeaders().get("Location").get(0))
				.endsWith("/" + corrBD.getId());
			// Construimos el corrector a comparar con los atributos que faltan
			// CorrectorNuevoDTO.fromCorrector(corrBD)
			// Convertimos el corrector obtenido de la BD a CorrectorNuevoDTO para compararlos
			// (al añadir un corrector a la BD internamente se le asignan nuevos atributos)

			compruebaCampos(correctorNuevoDTO, corrBD);
		}

		@Test
		@DisplayName("no inserta un corrector porque no tiene permisos")
		public void postCorrectorSinPermisos() {
			// crear corrector
			var materia = MateriaDTO.builder().id(2L).nombre("Matematicas").build();
			var c = CorrectorNuevoDTO.builder()
									.identificadorUsuario(1L)
									.identificadorConvocatoria(1L)
									.maximasCorrecciones(20)
									.materia(materia)
									.telefono("112233445")
									.build();

			var peticion = post("http", "localhost", port, "/correctores", c, tokenCaducado);
			var respuesta = restTemplate.exchange(peticion, Void.class);
			
			assertThat(respuesta.getStatusCode().value()).isEqualTo(403);
			
			List<Corrector> correctores = correctorRepo.findAll();
			assertThat(correctores).isEmpty();
		}

	}

    @Nested
    @DisplayName("Cuando la base de datos tiene datos:")
    public class BaseDatosLLena {

		Long idCorrector, idMatConv; //, idMateria;

        @BeforeEach
		public void introduceDatos(){
			CorrectorNuevoDTO correctorDTO = CorrectorNuevoDTO.builder()
                        .identificadorUsuario(1L)
                        .identificadorConvocatoria(3L)
                        .telefono("111-222-333")
                        .materia(MateriaDTO.builder()
                                    .id(5L)
                                    .nombre("mates")
                                    .build())
                        .maximasCorrecciones(11)
                        .build();
            Corrector corrector = correctorRepo.save(correctorDTO.corrector());
            idCorrector = corrector.getId();
			idMatConv = correctorDTO.getIdentificadorConvocatoria();
		}

		// get todos los correctores sin especificar convocatoria
		@Test
		@DisplayName("devuelve la lista de correctores")
		public void correctores() {
			var peticion = get("http", "localhost", port, "/correctores", tokenValido);
			var respuesta = restTemplate.exchange(peticion,
					new ParameterizedTypeReference<List<CorrectorDTO>>(){});
			
			assertThat(respuesta.getStatusCode().value()).isEqualTo(200);
			assertThat(respuesta.getBody()).hasSize(1);
		}

		// get todos los correctores especificando convocatoria
		@Test
		@DisplayName("devuelve la lista de correctores de una convocatoria")
		public void correctoresConv() {
			var peticion = getConv("http", "localhost", port, "/correctores", idMatConv, tokenValido);
			var respuesta = restTemplate.exchange(peticion,
					new ParameterizedTypeReference<List<CorrectorDTO>>(){});
			
			assertThat(respuesta.getStatusCode().value()).isEqualTo(200);
			// assertThat(respuesta.getBody()).isNotNull();
			//assertThat(respuesta.getBody()).hasSize(1);
			respuesta.getBody().stream().forEach(c -> assertThat(c.getMaterias().stream().allMatch(materia -> materia.getIdConvocatoria() == idMatConv)).isTrue());
		}


        // get un corrector por id
		@Test
        @DisplayName("acceder a un corrector por id existente")
        public void getCorrector() {
            var peticion = get("http", "localhost", port, "/correctores/" + idCorrector.toString(), tokenValido);
            var respuesta = restTemplate.exchange(peticion,
                    new ParameterizedTypeReference<CorrectorDTO>() {});
			
            assertThat(respuesta.getStatusCode().value()).isEqualTo(200);
			assertThat(respuesta.getBody()).isNotNull();
        }

		@Test
        @DisplayName("acceder a un corrector por id no existente")
        public void getCorrNoExiste() {
            var peticion = get("http", "localhost", port, "/correctores/24", tokenValido);
            var respuesta = restTemplate.exchange(peticion,
                    new ParameterizedTypeReference<CorrectorDTO>() {});
			
            assertThat(respuesta.getStatusCode().value()).isEqualTo(404);
			assertThat(respuesta.hasBody()).isFalse();
        }
		
        // delete un corrector
		@Test
		@DisplayName("elimina un corrector cuando existe")
		public void eliminarCorrector() {
			var peticion = delete("http", "localhost", port, "/correctores/" + idCorrector.toString(), tokenValido);
			var respuesta = restTemplate.exchange(peticion,Void.class);

			assertThat(respuesta.getStatusCode().value()).isEqualTo(200);
			List<Corrector> corrDesp = correctorRepo.findAll();
			//assertThat(corrDesp).hasSize(0);
			assertThat(corrDesp).isEmpty();
			//assertThat(corrDesp).allMatch(c->c.getId()!=idCorrector);
		}

        // put un corrector
		@Test
		@DisplayName("modifica un corrector cuando existe")
		public void modificarCorrector() {
			// crear nuevo corrector
			CorrectorNuevoDTO correctorDTO = CorrectorNuevoDTO.builder()
                        .identificadorUsuario(5L)
                        .identificadorConvocatoria(2L)
                        .telefono("222-111-333")
                        .materia(MateriaDTO.builder()
                                    .id(4L)
                                    .nombre("Lengua")
                                    .build())
                        .maximasCorrecciones(15)
                        .build();

			var peticion = put("http", "localhost", port, "/correctores/" + idCorrector.toString(), correctorDTO, tokenValido);
			var respuesta = restTemplate.exchange(peticion, Corrector.class);
			
			assertThat(respuesta.getStatusCode().value()).isEqualTo(200);
			Corrector corrBD = correctorRepo.findById(1L).get();
			// Convertimos el corrector obtenido de la BD a CorrectorNuevoDTO para compararlos
			// (al añadir un corrector a la BD internamente se le asignan nuevos atributos)
			compruebaCampos(correctorDTO, corrBD);
			// assertThat(CorrectorDTO.fromCorrector(corrBD).getMaterias()).hasSize(2);
		}
		
		@Test
		@DisplayName("modifica un corrector cuando no existe")
		public void modCorrNoExiste() {
			Corrector corrector = new Corrector();

			var peticion = put("http", "localhost", port, "/correctores/24", corrector, tokenValido);
			var respuesta = restTemplate.exchange(peticion, Corrector.class);
					
			assertThat(respuesta.getStatusCode().value()).isEqualTo(404);
			assertThat(respuesta.hasBody()).isFalse();
		}
		
		// post un corrector
		@Test
		@DisplayName("post corrector con ID existente")
		public void postConIDEx() {
			var materia = MateriaDTO.builder().id(2L).nombre("Matematicas").build();
			var c = CorrectorNuevoDTO.builder()
									.maximasCorrecciones(20)
									.identificadorConvocatoria(1L)
									.identificadorUsuario(1L)
									.materia(materia)
									.telefono("112233445")
									.build();

			var peticion = post("http", "localhost", port, "/correctores", c, tokenValido);
			var respuesta = restTemplate.exchange(peticion, Void.class);
			
			assertThat(respuesta.getStatusCode().value()).isEqualTo(409);
			List<Corrector> correctores = correctorRepo.findAll();
			assertThat(correctores).hasSize(1);
		}

		@Test
		@DisplayName("post corrector con ID nuevo")
		public void postConIDNuevo() {
			// crear corrector
			var materia = MateriaDTO.builder().id(2L).nombre("Matematicas").build();
			var c = CorrectorNuevoDTO.builder()
									.maximasCorrecciones(20)
									.identificadorConvocatoria(1L)
									.identificadorUsuario(2L)
									.materia(materia)
									.telefono("112233445")
									.build();

			var peticion = post("http", "localhost", port, "/correctores", c, tokenValido);
			var respuesta = restTemplate.exchange(peticion, Void.class);
				
			compruebaRespuesta(c, respuesta);
		}

		private void compruebaRespuesta(CorrectorNuevoDTO correctorDTO, ResponseEntity<Void> respuesta) {
			assertThat(respuesta.getStatusCode().value()).isEqualTo(201);
			assertThat(respuesta.getHeaders().get("Location").get(0))
				.startsWith("http://localhost:"+port+"/correctores");
			
			List<Corrector> correctores = correctorRepo.findAll();
			assertThat(correctores).hasSize(2);
			
			Corrector corrBD = correctores.stream()
					.filter(c->c.getIdUsuario().equals(2L))
					.findAny()
					.get(); 
			
			assertThat(respuesta.getHeaders().get("Location").get(0))
				.endsWith("/" + corrBD.getId());
			// Convertimos el corrector obtenido de la BD a CorrectorNuevoDTO para compararlos
			// (al añadir un corrector a la BD internamente se le asignan nuevos atributos)
			compruebaCampos(correctorDTO, corrBD);
		}
		
    }
}
