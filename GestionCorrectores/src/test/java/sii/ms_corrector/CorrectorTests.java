package sii.ms_corrector;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

//import org.assertj.core.util.Arrays;
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
    @Autowired
    private MateriaEnConvocatoriaRepository matConvRepo;
    @Autowired
    private MateriaRepository matRepo;

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
		for (String path : paths) {
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
	
	// private void compruebaCamposDTO(CorrectorNuevoDTO expected, CorrectorNuevoDTO actual) {
	// 	assertThat(actual.getIdentificadorUsuario()).isEqualTo(expected.getIdentificadorUsuario());
	// 	assertThat(actual.getTelefono()).isEqualTo(expected.getTelefono());
	// 	assertThat(actual.getMaximasCorrecciones()).isEqualTo(expected.getMaximasCorrecciones());

	// 	// Al convertir una entidad Corrector a un CorrectorNuevoDTO para compararlos, estos atributos no estan disponibles
	// 	assertThat(actual.getIdentificadorConvocatoria()).isEqualTo(expected.getIdentificadorConvocatoria());
	// 	// No debemos comprobar materia, pues solamente se usa internamente para guardar en la BD
	// 	// un corrector a traves del POST y asociar dicha materia a su lista de materias en convocatoria
	// 	// (un CorrectorNuevoDTO la tiene, pero un Corrector almacenado en la base de datos no)
	// 	// assertThat(actual.getMateria()).isEqualTo(expected.getMateria());
	// }

	private void compruebaCampos(CorrectorNuevoDTO expected, Corrector actual) {
		assertThat(expected.getIdentificadorUsuario()).isEqualTo(actual.getIdUsuario());
		assertThat(actual.getMatEnConv().stream().anyMatch(materia -> materia.getIdConvocatoria() == expected.getIdentificadorConvocatoria())).isTrue();
		assertThat(expected.getMaximasCorrecciones()).isEqualTo(actual.getMaximasCorrecciones());
		assertThat(expected.getTelefono()).isEqualTo(actual.getTelefono());
	}
	
	
	@Nested
	@DisplayName("Cuando la base de datos esta vacia:")
	public class BaseDatosVacia {

		// Usamos GET pero podriamos utilizar cualquiera es indiferente del metodo
		// Vamos a comprobar que si no proporcionamos un token no tenemos acceso
		@Test
        @DisplayName("acceso sin token")
        public void accesoSinToken() {
			URI uri = uri("http", "localhost", port, "/correctores");
			var peticion = RequestEntity.get(uri)
				.accept(MediaType.APPLICATION_JSON)
				.build();
			
			var respuesta = restTemplate.exchange(peticion, Void.class);
			
			assertThat(respuesta.getStatusCode().value()).isEqualTo(403);

		}

		@Test
        @DisplayName("cabecera incorrecta")
        public void cabeceraIncorrecta() {
			URI uri = uri("http", "localhost", port, "/correctores");
			var peticion = RequestEntity.get(uri)
				.header(HttpHeaders.AUTHORIZATION, "Papa " + tokenValido)
				.accept(MediaType.APPLICATION_JSON)
				.build();
			
			var respuesta = restTemplate.exchange(peticion, Void.class);
			
			assertThat(respuesta.getStatusCode().value()).isEqualTo(403);

		}

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
            var respuesta = restTemplate.exchange(peticion, Void.class);

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
			var materia = MateriaDTO.builder().id(2L).nombre("Fisica").build();
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

		Long idCorrector, idMatConv, idMateria;
		String nombreMateria;

        @BeforeEach
		public void introduceDatos(){
			MateriaDTO materiaDTOconId = MateriaDTO.builder()
											.id(1L)
											.build();
			CorrectorNuevoDTO correctorDTO = CorrectorNuevoDTO.builder()
                        .identificadorUsuario(1L)
                        .identificadorConvocatoria(3L)
                        .telefono("111-222-333")
                        .materia(materiaDTOconId)
                        .maximasCorrecciones(11)
                        .build();
            Corrector corrector = correctorRepo.save(correctorDTO.corrector());
			Materia materia = matRepo.save(materiaDTOconId.materia());
            idCorrector = corrector.getId();
			idMatConv = correctorDTO.getIdentificadorConvocatoria();
			idMateria = materia.getIdMateria();

			MateriaDTO materiaDTOconNombre = MateriaDTO.builder()
											.nombre("mates")
											.build();
			CorrectorNuevoDTO correctorDTO2 = CorrectorNuevoDTO.builder()
                        .identificadorUsuario(2L)
                        .identificadorConvocatoria(3L)
                        .telefono("444-555-666")
                        .materia(materiaDTOconNombre)
                        .maximasCorrecciones(20)
                        .build();
            //Corrector corrector2 = correctorRepo.save(correctorDTO2.corrector());
			correctorRepo.save(correctorDTO2.corrector());
			LOG.warning("Existe (en inicializar): " + matRepo.existsByIdMateria(materiaDTOconNombre.materia().getId()));
			LOG.warning("Su id es: " + materiaDTOconNombre.materia().getIdMateria());
			LOG.warning("Todas las materias: " + matRepo.findAll());
			Materia materia2 = matRepo.save(materiaDTOconNombre.materia());
			LOG.warning("Todas las materias despues de guardar: " + matRepo.findAll());
            //idCorrector2 = corrector2.getId();
			//idMatConv2 = correctorDTO2.getIdentificadorConvocatoria();
			nombreMateria = materia2.getNombre();
		}

		// get todos los correctores sin especificar convocatoria
		@Test
		@DisplayName("devuelve la lista de correctores")
		public void correctores() {
			var peticion = get("http", "localhost", port, "/correctores", tokenValido);
			var respuesta = restTemplate.exchange(peticion,
					new ParameterizedTypeReference<List<CorrectorDTO>>(){});
			
			assertThat(respuesta.getStatusCode().value()).isEqualTo(200);
			assertThat(respuesta.getBody()).hasSize(2);
		}

		// get todos los correctores especificando convocatoria
		// FIXME: hasSize
		@Test
		@DisplayName("devuelve la lista de correctores de una convocatoria")
		public void correctoresConv() {
			var peticion = getConv("http", "localhost", port, "/correctores", idMatConv, tokenValido);
			var respuesta = restTemplate.exchange(peticion,
					new ParameterizedTypeReference<List<CorrectorDTO>>(){});
			
			assertThat(respuesta.getStatusCode().value()).isEqualTo(200);
			// assertThat(respuesta.getBody()).isNotNull();
			// assertThat(respuesta.getBody()).hasSize(1);
			// LOG.warning(respuesta.getBody().toString());
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
			assertThat(corrDesp).hasSize(1);
			// assertThat(corrDesp).isEmpty();
			//assertThat(corrDesp).allMatch(c->c.getId()!=idCorrector);
		}

        // put un corrector
		@Test
		@DisplayName("modifica un corrector cuando existe y nueva materia")
		public void modificarCorrectorMateriaNueva() {
			// crear nuevo corrector
			CorrectorNuevoDTO correctorDTO = CorrectorNuevoDTO.builder()
                        .identificadorUsuario(1L)
                        .identificadorConvocatoria(2L)
                        .telefono("222-111-333")
                        .materia(MateriaDTO.builder()
                                    .id(4L)
                                    .nombre("Lengua")
                                    .build())
                        .maximasCorrecciones(15)
                        .build();

			var peticion = put("http", "localhost", port, "/correctores/" + idCorrector, correctorDTO, tokenValido);
			var respuesta = restTemplate.exchange(peticion, Corrector.class);
			
			assertThat(respuesta.getStatusCode().value()).isEqualTo(200);
			Corrector corrBD = correctorRepo.findById(idCorrector).get();
			// Convertimos el corrector obtenido de la BD a CorrectorNuevoDTO para compararlos
			// (al añadir un corrector a la BD internamente se le asignan nuevos atributos)
			compruebaCampos(correctorDTO, corrBD);
			// assertThat(CorrectorDTO.fromCorrector(corrBD).getMaterias()).hasSize(2);
		}
		
		@Test
		@DisplayName("modifica un corrector cuando existe y materia existente buscandola por id")
		public void modificarCorrectorMateriaPorId() {
			// crear nuevo corrector
			CorrectorNuevoDTO correctorDTO = CorrectorNuevoDTO.builder()
                        .identificadorUsuario(1L)
                        .identificadorConvocatoria(2L)
                        .telefono("222-111-333")
                        .materia(MateriaDTO.fromMateria(matRepo.findByIdMateria(idMateria)))
                        .maximasCorrecciones(15)
                        .build();

			var peticion = put("http", "localhost", port, "/correctores/" + idCorrector, correctorDTO, tokenValido);
			var respuesta = restTemplate.exchange(peticion, Corrector.class);
			
			assertThat(respuesta.getStatusCode().value()).isEqualTo(200);
			Corrector corrBD = correctorRepo.findById(idCorrector).get();
			// Convertimos el corrector obtenido de la BD a CorrectorNuevoDTO para compararlos
			// (al añadir un corrector a la BD internamente se le asignan nuevos atributos)
			compruebaCampos(correctorDTO, corrBD);
			// assertThat(CorrectorDTO.fromCorrector(corrBD).getMaterias()).hasSize(2);
		}

		private Long inicializarMatConv() {
			// Creamos corrector sin lista materiasConv y lo guardamos
			Corrector c = new Corrector();
			c.setId(null);
			c.setIdUsuario(5L);
			c.setMaximasCorrecciones(20);
			c.setTelefono("123-456-789");
			correctorRepo.save(c);

			// (Independiente de corrector)
			Materia materia = new Materia();
			materia.setId(null);
			materia.setIdMateria(10L);
			materia.setNombre("Historia");
			matRepo.save(materia);

			// Asociamos el corrector (debemos tenerlo guardado)
			// Asociamos la materia (debemos tenerla guardada)
			// Guardamos materia en convocatoria
			MateriaEnConvocatoria matConvoc = new MateriaEnConvocatoria();
			matConvoc.setId(null);
			matConvoc.setIdConvocatoria(7L);
			matConvoc.setMateria(materia);
			matConvoc.setCorrector(c);
			matConvRepo.save(matConvoc);

			List<MateriaEnConvocatoria> materias = new ArrayList<>();
			materias.add(matConvoc);
			c.setMatEnConv(materias);

			return correctorRepo.save(c).getId();
		
			// matRepo.save(MateriaDTO.builder().id(10L).nombre("Historia").build().materia());
			// matConvRepo.save(MateriaEnConvocatoriaDTO.builder()
			// 			.idConvocatoria(7L)
			// 			.idMateria(7L).build().materiaEnConvocatoria());
		}

		@Test
		@DisplayName("modifica un corrector cuando existe y materia existente en convocatoria")
		public void modificarCorrectorMateriaEnConvocatoria() {
			Long id = inicializarMatConv();
			CorrectorNuevoDTO correctorDTO = CorrectorNuevoDTO.builder()
                        .identificadorUsuario(5L)
                        .identificadorConvocatoria(7L)
                        .telefono("222-111-333")
                        .materia(MateriaDTO.builder().id(7L).nombre("Historia").build())
                        .maximasCorrecciones(15)
                        .build();

			var peticion = put("http", "localhost", port, "/correctores/" + id, correctorDTO, tokenValido);
			var respuesta = restTemplate.exchange(peticion, Corrector.class);
			
			assertThat(respuesta.getStatusCode().value()).isEqualTo(200);
			Corrector corrBD = correctorRepo.findById(id).get();
			// Convertimos el corrector obtenido de la BD a CorrectorNuevoDTO para compararlos
			// (al añadir un corrector a la BD internamente se le asignan nuevos atributos)
			compruebaCampos(correctorDTO, corrBD);
			// assertThat(CorrectorDTO.fromCorrector(corrBD).getMaterias()).hasSize(2);
		}
		
		@Test
		@DisplayName("modifica un corrector cuando existe y materia existente buscandola por nombre")
		public void modificarCorrectorMateriaPorNombre() {
			// crear nuevo corrector
			CorrectorNuevoDTO correctorDTO = CorrectorNuevoDTO.builder()
                        .identificadorUsuario(1L)
                        .identificadorConvocatoria(2L)
                        .telefono("222-111-333")
                        .materia(MateriaDTO.fromMateria(matRepo.findByNombre(nombreMateria)))
                        .maximasCorrecciones(15)
                        .build();

			var peticion = put("http", "localhost", port, "/correctores/" + idCorrector.toString(), correctorDTO, tokenValido);
			var respuesta = restTemplate.exchange(peticion, Corrector.class);
			
			assertThat(respuesta.getStatusCode().value()).isEqualTo(200);
			Corrector corrBD = correctorRepo.findById(idCorrector).get();
			// Convertimos el corrector obtenido de la BD a CorrectorNuevoDTO para compararlos
			// (al añadir un corrector a la BD internamente se le asignan nuevos atributos)
			compruebaCampos(correctorDTO, corrBD);
			// assertThat(CorrectorDTO.fromCorrector(corrBD).getMaterias()).hasSize(2);
		}

		@Test
		@DisplayName("put corrector con ID existente y materia existente con id y nombre nulos")
		// este caso no se va a dar, siempre debemos proporcionar o el idMateria o el nombre para una Materia
		public void putIdNombreNulos() {
			// crear corrector
			Materia materia = new Materia();
			// materia.setId(null);
			// materia.setIdMateria(null);
			// materia.setNombre(null);
			matRepo.save(materia);

			var materiaNula = MateriaDTO.builder().build();
			var c = CorrectorNuevoDTO.builder()
									.identificadorUsuario(3L)
									.identificadorConvocatoria(1L)
									.materia(materiaNula)
									.telefono("112-233-445")
									.maximasCorrecciones(20)
									.build();

			var peticion = put("http", "localhost", port, "/correctores/" + idCorrector, c, tokenValido);
			var respuesta = restTemplate.exchange(peticion, Void.class);
				
			assertThat(respuesta.getStatusCode().value()).isEqualTo(200);
			assertThat(respuesta.hasBody()).isFalse();
		}
		
		@Test
		@DisplayName("modifica un corrector cuando no existe")
		public void modCorrNoExiste() {
			CorrectorNuevoDTO corrector = CorrectorNuevoDTO.builder().build();

			var peticion = put("http", "localhost", port, "/correctores/24", corrector, tokenValido);
			var respuesta = restTemplate.exchange(peticion, Corrector.class);
					
			assertThat(respuesta.getStatusCode().value()).isEqualTo(404);
			assertThat(respuesta.hasBody()).isFalse();
		}
		
		// post un corrector
		@Test
		@DisplayName("post corrector con ID existente")
		public void postConIDEx() {
			var materia = MateriaDTO.builder().id(2L).nombre("Lengua").build();
			var c = CorrectorNuevoDTO.builder()
									.identificadorUsuario(1L)
									.identificadorConvocatoria(1L)
									.materia(materia)
									.telefono("112-233-445")
									.maximasCorrecciones(20)
									.build();

			var peticion = post("http", "localhost", port, "/correctores", c, tokenValido);
			var respuesta = restTemplate.exchange(peticion, Void.class);
			
			assertThat(respuesta.getStatusCode().value()).isEqualTo(409);
			List<Corrector> correctores = correctorRepo.findAll();
			assertThat(correctores).hasSize(2);
		}

		@Test
		@DisplayName("post corrector con ID nuevo y materia existente buscandola por id")
		public void postConIDNuevoMateriaExistentePorId() {
			// crear corrector
			var materia = MateriaDTO.fromMateria(matRepo.findByIdMateria(idMateria));
			var c = CorrectorNuevoDTO.builder()
									.identificadorUsuario(3L)
									.identificadorConvocatoria(1L)
									.materia(materia)
									.telefono("112-233-445")
									.maximasCorrecciones(20)
									.build();

			var peticion = post("http", "localhost", port, "/correctores", c, tokenValido);
			var respuesta = restTemplate.exchange(peticion, Void.class);
				
			compruebaRespuesta(c, respuesta);
		}
		
		@Test
		@DisplayName("post corrector con ID nuevo y materia existente buscandola por nombre")
		public void postConIDNuevoMateriaExistentePorNombre() {
			// crear corrector
			var materia = MateriaDTO.fromMateria(matRepo.findByNombre(nombreMateria));
			LOG.warning("Id materia: " + materia.getId());	// nulo
			LOG.warning("Existe Id?: " + matRepo.existsByIdMateria(null));	// true
			LOG.warning("Nombre materia: " + materia.materia().getNombre());	// mates
			LOG.warning("Existe materia?: " + matRepo.existsByNombre(materia.materia().getNombre())); // true
			//var materia = MateriaDTO.builder().nombre(nombreMateria).build();
			var c = CorrectorNuevoDTO.builder()
									.identificadorUsuario(3L)
									.identificadorConvocatoria(1L)
									.materia(materia)
									.telefono("112-233-445")
									.maximasCorrecciones(20)
									.build();

			var peticion = post("http", "localhost", port, "/correctores", c, tokenValido);
			var respuesta = restTemplate.exchange(peticion, Void.class);
				
			compruebaRespuesta(c, respuesta);
		}

		@Test
		@DisplayName("post corrector con ID nuevo y nueva materia")
		public void postConIDNuevo() {
			// crear corrector
			var materia = MateriaDTO.builder().id(2L).nombre("Fisica").build();
			var c = CorrectorNuevoDTO.builder()
									.identificadorUsuario(3L)
									.identificadorConvocatoria(1L)
									.materia(materia)
									.telefono("112-233-445")
									.maximasCorrecciones(20)
									.build();

			var peticion = post("http", "localhost", port, "/correctores", c, tokenValido);
			var respuesta = restTemplate.exchange(peticion, Void.class);
				
			compruebaRespuesta(c, respuesta);
		}

		@Test
		@DisplayName("post corrector con ID nuevo y nueva materia (id y nombre a nulo)")
		// este caso no se va a dar, siempre debemos proporcionar o el idMateria o el nombre para una Materia
		public void postIdNombreNulos() {
			// crear corrector
			Materia materia = new Materia();
			// materia.setId(null);
			// materia.setIdMateria(null);
			// materia.setNombre(null);
			matRepo.save(materia);

			var materiaNula = MateriaDTO.builder().build();
			var c = CorrectorNuevoDTO.builder()
									.identificadorUsuario(3L)
									.identificadorConvocatoria(1L)
									.materia(materiaNula)
									.telefono("112-233-445")
									.maximasCorrecciones(20)
									.build();

			var peticion = post("http", "localhost", port, "/correctores", c, tokenValido);
			var respuesta = restTemplate.exchange(peticion, Void.class);
				
			compruebaRespuesta(c, respuesta);
		}

		private void compruebaRespuesta(CorrectorNuevoDTO correctorDTO, ResponseEntity<Void> respuesta) {
			assertThat(respuesta.getStatusCode().value()).isEqualTo(201);
			assertThat(respuesta.getHeaders().get("Location").get(0))
				.startsWith("http://localhost:" + port + "/correctores");
			
			List<Corrector> correctores = correctorRepo.findAll();
			assertThat(correctores).hasSize(3);
			
			// Corrector que acabamos de meter con el POST (debe estar en la BD)
			Corrector corrBD = correctores.stream()
					.filter(c -> c.getIdUsuario().equals(3L))
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
