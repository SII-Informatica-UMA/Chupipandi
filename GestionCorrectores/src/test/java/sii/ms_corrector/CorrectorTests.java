package sii.ms_corrector;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

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
import sii.ms_corrector.services.MateriaService;
import sii.ms_corrector.util.JwtGenerator;
import sii.ms_corrector.dtos.*;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DisplayName("En el servicio de corrector:")
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
class CorrectorTests {
	// Logger para debugear
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
	@Autowired
	private MateriaService matService;

	// Utilizamos esta variable para que no se inicialice la base de datos en cada test.
	// De este modo, solo se inicializa una vez, en el primer test (como si fuera un @BeforeAll)
	// No podemos utilizar @BeforeAll porque el método inicializar() del servicio de Materias no es un metodo estático.
	private boolean inicializado = false;

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
		// Si no se ha inicializado la base de datos, lo hacemos
		if (!inicializado) {
			matService.inicializar();
			// Indicamos al servicio que ya se ha inicializado la base de datos
			// (para que no se inicialice en el método 'comprobarMateria()')
			matService.yaInicializado();
			// Ponemos inicializado a true
			inicializado = true;
		}
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

	private void compruebaCampos(CorrectorNuevoDTO expected, Corrector actual) {
		assertThat(expected.getIdentificadorUsuario()).isEqualTo(actual.getIdUsuario());
		assertThat(actual.getMatEnConv().stream().anyMatch(materia -> materia.getIdConvocatoria() == expected.getIdentificadorConvocatoria())).isTrue();
		assertThat(expected.getMaximasCorrecciones()).isEqualTo(actual.getMaximasCorrecciones());
		assertThat(expected.getTelefono()).isEqualTo(actual.getTelefono());
	}

	private void compruebaCamposPut(CorrectorDTO enBD, CorrectorNuevoDTO nuevo, int n) {
		assertThat(enBD.getTelefono()).isEqualTo(nuevo.getTelefono());
		assertThat(enBD.getIdentificadorUsuario()).isEqualTo(nuevo.getIdentificadorUsuario());
		assertThat(enBD.getMaximasCorrecciones()).isEqualTo(nuevo.getMaximasCorrecciones());

		List<MateriaEnConvocatoriaDTO> materias = enBD.getMaterias();
		MateriaEnConvocatoriaDTO materiaNueva = new MateriaEnConvocatoriaDTO();
		materiaNueva.setIdConvocatoria(nuevo.getIdentificadorConvocatoria());
		materiaNueva.setIdMateria(nuevo.getMateria().getId());
		
		// Necesitamos pasarle por parametro el número de materias que tiene el corrector
		// que estamos comprobando porque para uno de los tests el put no introduce una nueva
		// materia porque su convocatoria asociada ya existe
		// Todos los tests de PUT deberían tener tamaño 2, menos ese test, que tiene 1
		assertThat(materias).hasSize(n);
	}

	private void compruebaRespuesta(CorrectorNuevoDTO correctorDTO, ResponseEntity<Void> respuesta, int size, Long idUsuario) {
		assertThat(respuesta.getStatusCode().value()).isEqualTo(201);
		assertThat(respuesta.getHeaders().get("Location").get(0))
			.startsWith("http://localhost:" + port + "/correctores");
		
		List<Corrector> correctores = correctorRepo.findAll();
		assertThat(correctores).hasSize(size);
		
		// Corrector que acabamos de meter con el POST (debe estar en la BD)
		Corrector corrBD = correctores.stream()
				// el id del usuario es el mismo que el id del corrector
				.filter(c -> c.getIdUsuario().equals(idUsuario))
				.findAny()
				.get(); 
		
		assertThat(respuesta.getHeaders().get("Location").get(0))
			.endsWith("/" + corrBD.getId());
		// Convertimos el corrector obtenido de la BD a CorrectorNuevoDTO para compararlos
		// (al añadir un corrector a la BD internamente se le asignan nuevos atributos)
		compruebaCampos(correctorDTO, corrBD);
	}
	
	@Nested
	@DisplayName("Cuando la base de datos esta vacia:")
	public class BaseDatosVacia {

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

		/////////// GET CORRECTOR POR ID ///////////

        @Test
        @DisplayName("acceder a un corrector que no existe")
        public void errorGetCorrector() {
            var peticion = get("http", "localhost", port, "/correctores/1", tokenValido);
            var respuesta = restTemplate.exchange(peticion,
                    new ParameterizedTypeReference<CorrectorDTO>() {});
			
            assertThat(respuesta.getStatusCode().value()).isEqualTo(404);
			assertThat(respuesta.getBody()).isNull();
        }

        @Test
        @DisplayName("acceder a un corrector sin permisos")
        public void getCorrectorNoAutorizacion() {
            var peticion = get("http", "localhost", port, "/correctores/1", tokenCaducado);
			var respuesta = restTemplate.exchange(peticion, Void.class);
			
            assertThat(respuesta.getStatusCode().value()).isEqualTo(403);
        }

		/////////// PUT ///////////

		@Test
        @DisplayName("intentar actualizar un corrector que no existe")
        public void errorPutCorrector() {
            var corrector = CorrectorNuevoDTO.builder().maximasCorrecciones(20).build();
            var peticion = put("http", "localhost", port, "/correctores/1", corrector, tokenValido);
            var respuesta = restTemplate.exchange(peticion, CorrectorDTO.class);

			assertThat(respuesta.getStatusCode().value()).isEqualTo(404);

			List<Corrector> correctorBD = correctorRepo.findAll();
			assertThat(correctorBD).isEmpty();
        }

		@Test
        @DisplayName("intentar actualizar un corrector sin permisos")
        public void putCorrectorNoAutorizacion() {
            var corrector = CorrectorNuevoDTO.builder().maximasCorrecciones(20).build();
            var peticion = put("http", "localhost", port, "/correctores/1", corrector, tokenCaducado);
            var respuesta = restTemplate.exchange(peticion, CorrectorDTO.class);

			assertThat(respuesta.getStatusCode().value()).isEqualTo(403);

			List<Corrector> correctorBD = correctorRepo.findAll();
			assertThat(correctorBD).isEmpty();
        }

		/////////// DELETE ///////////

		@Test
		@DisplayName("intentar eliminar un corrector que no existe")
		public void errorEliCorrector() {
			var peticion = delete("http", "localhost", port, "/correctores/1", tokenValido);
			var respuesta = restTemplate.exchange(peticion,Void.class);

			assertThat(respuesta.getStatusCode().value()).isEqualTo(404);
			assertThat(respuesta.getBody()).isNull();
		}

		@Test
		@DisplayName("intentar eliminar un corrector sin permisos")
		public void deleteCorrectorNoAutorizacion() {
			var peticion = delete("http", "localhost", port, "/correctores/1", tokenCaducado);
			var respuesta = restTemplate.exchange(peticion, Void.class);

			assertThat(respuesta.getStatusCode().value()).isEqualTo(403);
		}

		/////////// GET LISTA COMPLETA ///////////

		@Test
		@DisplayName("devuelve una lista vacia de correctores")
		public void listaVaciaCorrectores() {
			var peticion = get("http", "localhost", port, "/correctores", tokenValido);
			var respuesta = restTemplate.exchange(peticion,
					new ParameterizedTypeReference<List<CorrectorDTO>>(){});
			
			assertThat(respuesta.getStatusCode().value()).isEqualTo(200);
			assertThat(respuesta.getBody()).isEmpty();
		}

		@Test
		@DisplayName("intentar acceder a la lista de correctores sin permisos")
		public void getCorrectoresNoAutorizacion() {
			var peticion = get("http", "localhost", port, "/correctores", tokenCaducado);
			var respuesta = restTemplate.exchange(peticion, Void.class);
			
			assertThat(respuesta.getStatusCode().value()).isEqualTo(403);
		}

		/////////// GET LISTA POR CONVOCATORIA ///////////

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
		@DisplayName("intentar acceder a los correctores de una convocatoria sin autorizacion")
		public void getCorrectoresConvNoAutorizacion() {
			var peticion = getConv("http", "localhost", port, "/correctores", 1L, tokenCaducado);
			var respuesta = restTemplate.exchange(peticion, Void.class);
			
			assertThat(respuesta.getStatusCode().value()).isEqualTo(403);
		}

        /////////// POST ///////////

		@Test
		@DisplayName("insertar correctamente un corrector")
		public void postCorrector() {
			var materia = MateriaDTO.builder().id(2L).nombre("Física").build();
			var c = CorrectorNuevoDTO.builder()
									.identificadorUsuario(1L)
									.identificadorConvocatoria(1L)
									.maximasCorrecciones(20)
									.materia(materia)
									.telefono("112233445")
									.build();

			var peticion = post("http", "localhost", port, "/correctores", c, tokenValido);
			var respuesta = restTemplate.exchange(peticion, Void.class);
			compruebaRespuesta(c, respuesta, 1, 1L);
		}

		@Test
		@DisplayName("no insertar un corrector porque no tiene permisos")
		public void postCorrectorSinPermisos() {
			var materia = MateriaDTO.builder().id(1L).nombre("Matemáticas II").build();
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

		@Test
		@DisplayName("no insertar un corrector porque la materia proporcionada no existe por nombre")
		public void postMateriaNoExistePorNombre() {
			var materiaNula = MateriaDTO.builder().nombre("Tecnología").build();
			var c = CorrectorNuevoDTO.builder()
									.identificadorUsuario(3L)
									.identificadorConvocatoria(1L)
									.materia(materiaNula)
									.telefono("112-233-445")
									.maximasCorrecciones(20)
									.build();

			var peticion = post("http", "localhost", port, "/correctores", c, tokenValido);
			var respuesta = restTemplate.exchange(peticion, Void.class);
				
			assertThat(respuesta.getStatusCode().value()).isEqualTo(400);
		}

		@Test
		@DisplayName("no insertar un corrector porque la materia proporcionada no existe por id")
		public void postMateriaNoExistePorId() {
			var materiaNula = MateriaDTO.builder().id(25L).build();
			var c = CorrectorNuevoDTO.builder()
									.identificadorUsuario(3L)
									.identificadorConvocatoria(1L)
									.materia(materiaNula)
									.telefono("112-233-445")
									.maximasCorrecciones(20)
									.build();

			var peticion = post("http", "localhost", port, "/correctores", c, tokenValido);
			var respuesta = restTemplate.exchange(peticion, Void.class);
				
			assertThat(respuesta.getStatusCode().value()).isEqualTo(400);
		}

	}

    @Nested
    @DisplayName("Cuando la base de datos tiene datos:")
    public class BaseDatosLLena {

		Long idCorrector, idMatConv, idMateria;
		String nombreMateria;

        @BeforeEach
		public void introduceDatos(){
			// Crea materia Matemáticas II
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
            idCorrector = corrector.getId();
			idMatConv = correctorDTO.getIdentificadorConvocatoria();
			Materia materia = matRepo.findByIdMateria(1L);
			idMateria = materia.getIdMateria();

			MateriaEnConvocatoria materiaConv = new MateriaEnConvocatoria();
            materiaConv.setIdConvocatoria(idMatConv);
            materiaConv.setMateria(materia);
            materiaConv.setCorrector(corrector);
            matConvRepo.save(materiaConv);

			// con nombre pero no id
			MateriaDTO materiaDTOconNombre = MateriaDTO.builder()
											.nombre("Filosofía")
											.build();
											
			CorrectorNuevoDTO correctorDTO2 = CorrectorNuevoDTO.builder()
                        .identificadorUsuario(2L)
                        .identificadorConvocatoria(2L)
                        .telefono("444-555-666")
                        .materia(materiaDTOconNombre)
                        .maximasCorrecciones(20)
                        .build();

			Corrector c2 = correctorRepo.save(correctorDTO2.corrector());
            Long idMatConv2 = correctorDTO2.getIdentificadorConvocatoria();
			Materia materia2 = matRepo.findByNombre("Filosofía");
            nombreMateria = materia2.getNombre();

            MateriaEnConvocatoria materiaConv2 = new MateriaEnConvocatoria();
            materiaConv2.setIdConvocatoria(idMatConv2);
            materiaConv2.setMateria(materia2);
            materiaConv2.setCorrector(c2);
            matConvRepo.save(materiaConv2);
		}

		/////////// GET LISTA COMPLETA ///////////

		@Test
		@DisplayName("devuelve la lista de correctores")
		public void correctores() {
			var peticion = get("http", "localhost", port, "/correctores", tokenValido);
			var respuesta = restTemplate.exchange(peticion,
					new ParameterizedTypeReference<List<CorrectorDTO>>(){});
			
			assertThat(respuesta.getStatusCode().value()).isEqualTo(200);
			assertThat(respuesta.getBody()).hasSize(2);
		}

		/////////// GET LISTA POR CONVOCATORIA ///////////

		@Test
		@DisplayName("devuelve la lista de correctores de una convocatoria")
		public void correctoresConv() {
			var peticion = getConv("http", "localhost", port, "/correctores", idMatConv, tokenValido);
			var respuesta = restTemplate.exchange(peticion,
					new ParameterizedTypeReference<List<CorrectorDTO>>(){});
			
			assertThat(respuesta.getStatusCode().value()).isEqualTo(200);
			assertThat(respuesta.getBody()).hasSize(1);
			respuesta.getBody().stream().forEach(c -> assertThat(c.getMaterias().stream().allMatch(materia -> materia.getIdConvocatoria() == idMatConv)).isTrue());
		}

		/////////// GET POR ID ///////////

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
        @DisplayName("intentar acceder a un corrector por id no existente")
        public void getCorrNoExiste() {
            var peticion = get("http", "localhost", port, "/correctores/24", tokenValido);
            var respuesta = restTemplate.exchange(peticion,
                    new ParameterizedTypeReference<CorrectorDTO>() {});
			
            assertThat(respuesta.getStatusCode().value()).isEqualTo(404);
			assertThat(respuesta.hasBody()).isFalse();
        }
		
        /////////// DELETE ///////////

		@Test
		@DisplayName("eliminar un corrector cuando existe")
		public void eliminarCorrector() {
			var peticion = delete("http", "localhost", port, "/correctores/" + idCorrector.toString(), tokenValido);
			var respuesta = restTemplate.exchange(peticion,Void.class);

			assertThat(respuesta.getStatusCode().value()).isEqualTo(200);
			List<Corrector> corrDesp = correctorRepo.findAll();
			assertThat(corrDesp).hasSize(1).allMatch(c -> !c.getId().equals(idCorrector));
		}

		/////////// PUT ///////////

		@Test
		@DisplayName("modificar un corrector asignandole una nueva materia")
		public void modificarCorrectorMateriaNueva() {
			CorrectorNuevoDTO correctorNuevoDTO = CorrectorNuevoDTO.builder()
                        .identificadorUsuario(1L)
                        .identificadorConvocatoria(2L)
                        .telefono("222-111-333")
                        .materia(MateriaDTO.builder()
                                    .id(4L)
                                    .nombre("Lengua Castellana y Literatura")
                                    .build())
                        .maximasCorrecciones(15)
                        .build();

			var peticion = put("http", "localhost", port, "/correctores/" + idCorrector, correctorNuevoDTO, tokenValido);
			var respuesta = restTemplate.exchange(peticion, CorrectorDTO.class);
			
			assertThat(respuesta.getStatusCode().value()).isEqualTo(200);
			compruebaCamposPut(respuesta.getBody(), correctorNuevoDTO, 2);
		}

		@Test
		@DisplayName("modificar un corrector asignandole una materia existente buscandola por id")
		public void modificarCorrectorMateriaPorId() {
			CorrectorNuevoDTO correctorDTO = CorrectorNuevoDTO.builder()
                        .identificadorUsuario(1L)
                        .identificadorConvocatoria(2L)
                        .telefono("222-111-333")
                        .materia(MateriaDTO.fromMateria(matRepo.findByIdMateria(idMateria)))
                        .maximasCorrecciones(15)
                        .build();

			var peticion = put("http", "localhost", port, "/correctores/" + idCorrector, correctorDTO, tokenValido);
			var respuesta = restTemplate.exchange(peticion, CorrectorDTO.class);
			
			assertThat(respuesta.getStatusCode().value()).isEqualTo(200);
			compruebaCamposPut(respuesta.getBody(), correctorDTO, 2);
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
			Materia materia = matRepo.findByIdMateria(5L);
			// matRepo.save(materia);

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
		}
		// FIXME
		// Como está intentando asignarle una materia con id y nombre, se le asigna
		// la materia que tiene esa id en la BD, aunque el nombre sea diferente
		@Test
		@DisplayName("modificar un corrector asignandole una materia existente en esa convocatoria")
		public void modificarCorrectorMateriaEnConvocatoria() {
			Long id = inicializarMatConv();
			CorrectorNuevoDTO correctorDTO = CorrectorNuevoDTO.builder()
                        .identificadorUsuario(5L)
                        .identificadorConvocatoria(7L)
                        .telefono("222-111-333")
                        .materia(MateriaDTO.builder().id(5L).nombre("Historia de España").build())
                        .maximasCorrecciones(15)
                        .build();

			var peticion = put("http", "localhost", port, "/correctores/" + id, correctorDTO, tokenValido);
			var respuesta = restTemplate.exchange(peticion, CorrectorDTO.class);
			
			assertThat(respuesta.getStatusCode().value()).isEqualTo(200);
			compruebaCamposPut(respuesta.getBody(), correctorDTO, 1);
		}
		
		@Test
		@DisplayName("modificar un corrector asignandole una materia buscandola por nombre")
		public void modificarCorrectorMateriaPorNombre() {
			CorrectorNuevoDTO correctorDTO = CorrectorNuevoDTO.builder()
                        .identificadorUsuario(1L)
                        .identificadorConvocatoria(2L)
                        .telefono("222-111-333")
                        .materia(MateriaDTO.fromMateria(matRepo.findByNombre(nombreMateria)))
                        .maximasCorrecciones(15)
                        .build();

			var peticion = put("http", "localhost", port, "/correctores/" + idCorrector.toString(), correctorDTO, tokenValido);
			var respuesta = restTemplate.exchange(peticion, CorrectorDTO.class);
			
			assertThat(respuesta.getStatusCode().value()).isEqualTo(200);
			compruebaCamposPut(respuesta.getBody(), correctorDTO, 2);
		}

		@Test
		@DisplayName("intentar modificar un corrector asignandole una materia con id y nombre nulos")
		// este caso no se va a dar, siempre debemos proporcionar o el idMateria o el nombre para una Materia
        // incluimos este test para contribuir a la completa cobertura de los tests
		public void putIdNombreNulos() {
			var materiaNula = MateriaDTO.builder().build();
			var c = CorrectorNuevoDTO.builder()
									.identificadorUsuario(3L)
									.identificadorConvocatoria(1L)
									.materia(materiaNula)
									.telefono("112-233-445")
									.maximasCorrecciones(20)
									.build();

			var peticion = put("http", "localhost", port, "/correctores/" + idCorrector, c, tokenValido);
			var respuesta = restTemplate.exchange(peticion, CorrectorDTO.class);
				
			assertThat(respuesta.getStatusCode().value()).isEqualTo(400);
			assertThat(respuesta.hasBody()).isFalse();
		}

		@Test
		@DisplayName("intentar modificar un corrector poniendo un idUsuario ya existente (y que no es el suyo)")
		public void putIdUsuarioExistente(){
			var materia = MateriaDTO.builder().id(2L).nombre("Física").build();
			var c = CorrectorNuevoDTO.builder()
									.identificadorUsuario(2L)
									.identificadorConvocatoria(1L)
									.materia(materia)
									.telefono("112-233-445")
									.maximasCorrecciones(20)
									.build();

			var peticion = put("http", "localhost", port, "/correctores/" + idCorrector, c, tokenValido);
			var respuesta = restTemplate.exchange(peticion, CorrectorDTO.class);
				
			assertThat(respuesta.getStatusCode().value()).isEqualTo(409);
			assertThat(respuesta.hasBody()).isFalse();
		}

		@Test
        @DisplayName("modificar un corrector asignandole una materia por nombre (con id nulo)")
        public void putIdNuloMateriaNueva() {
            var materia = MateriaDTO.builder().nombre("Física").build();
            var correctorDTO = CorrectorNuevoDTO.builder()
                                    .identificadorUsuario(3L)
                                    .identificadorConvocatoria(1L)
                                    .materia(materia)
                                    .telefono("112-233-445")
                                    .maximasCorrecciones(20)
                                    .build();

            var peticion = put("http", "localhost", port, "/correctores/" + idCorrector, correctorDTO, tokenValido);
            var respuesta = restTemplate.exchange(peticion, CorrectorDTO.class);
			
			assertThat(respuesta.getStatusCode().value()).isEqualTo(200);
			compruebaCamposPut(respuesta.getBody(), correctorDTO, 2);
        }
		
		@Test
		@DisplayName("intentar modificar un corrector cuando no existe")
		public void modCorrNoExiste() {
			CorrectorNuevoDTO corrector = CorrectorNuevoDTO.builder().build();

			var peticion = put("http", "localhost", port, "/correctores/24", corrector, tokenValido);
			var respuesta = restTemplate.exchange(peticion, CorrectorDTO.class);
					
			assertThat(respuesta.getStatusCode().value()).isEqualTo(404);
			assertThat(respuesta.hasBody()).isFalse();
		}
		
		/////////// POST ///////////

		@Test
        @DisplayName("añadir un corrector asignandole una materia con id nulo")
        public void postIdNuloMateriaNueva() {
            var materia = MateriaDTO.builder().nombre("Física").build();
            var c = CorrectorNuevoDTO.builder()
                                    .identificadorUsuario(3L)
                                    .identificadorConvocatoria(1L)
                                    .materia(materia)
                                    .telefono("112-233-445")
                                    .maximasCorrecciones(20)
                                    .build();

            var peticion = post("http", "localhost", port, "/correctores", c, tokenValido);
            var respuesta = restTemplate.exchange(peticion, Void.class);

            compruebaRespuesta(c, respuesta, 3, 3L);
        }

		@Test
		@DisplayName("intentar añadir un corrector con idUsuario existente")
		public void postConIDEx() {
			var materia = MateriaDTO.builder().id(4L).nombre("Lengua Castellana y Literatura").build();
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
		@DisplayName("añadir un corrector asignandole una materia buscandola por id")
		public void postConIDNuevoMateriaExistentePorId() {
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
				
			compruebaRespuesta(c, respuesta, 3, 3L);
		}
		
		@Test
		@DisplayName("añadir un corrector asignandole una materia buscandola por nombre")
		public void postConIDNuevoMateriaExistentePorNombre() {
			var materia = MateriaDTO.fromMateria(matRepo.findByNombre(nombreMateria));
			var c = CorrectorNuevoDTO.builder()
									.identificadorUsuario(3L)
									.identificadorConvocatoria(1L)
									.materia(materia)
									.telefono("112-233-445")
									.maximasCorrecciones(20)
									.build();

			var peticion = post("http", "localhost", port, "/correctores", c, tokenValido);
			var respuesta = restTemplate.exchange(peticion, Void.class);
				
			compruebaRespuesta(c, respuesta, 3, 3L);
		}

		@Test
		@DisplayName("añadir un corrector asignandole una materia con id y nombre")
		public void postConIDNuevo() {
			var materia = MateriaDTO.builder().id(2L).nombre("Física").build();
			var c = CorrectorNuevoDTO.builder()
									.identificadorUsuario(3L)
									.identificadorConvocatoria(1L)
									.materia(materia)
									.telefono("112-233-445")
									.maximasCorrecciones(20)
									.build();

			var peticion = post("http", "localhost", port, "/correctores", c, tokenValido);
			var respuesta = restTemplate.exchange(peticion, Void.class);
				
			compruebaRespuesta(c, respuesta, 3, 3L);
		}

		@Test
		@DisplayName("añadir un corrector asignandole una materia con (id y nombre a nulo)")
		// este caso no se va a dar, siempre debemos proporcionar o el idMateria o el nombre para una Materia
        // incluimos este test para contribuir a la completa cobertura de los tests
		public void postIdNombreNulos() {
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
				
			assertThat(respuesta.getStatusCode().value()).isEqualTo(400);
		}
		
    }
}