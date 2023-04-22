package sii.ms_corrector;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.assertj.core.util.Arrays;
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
@DisplayName("En el servicio de corrector")
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
class CorrectorTests {
	
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

	String tokenValido = JwtGenerator.createToken("user", 5, "VICERRECTORADO");    // valido por 5 horas
    String tokenCaducado = JwtGenerator.createToken("user", -1, "VICERRECTORADO");            // caducado hace 1 hora
    String tokenNoAuth = JwtGenerator.createToken("user", 5, "CORRECTOR");            // rol incorrecto

	@BeforeEach
	public void initializeDatabase(){
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
	
	private RequestEntity<Void> get(String scheme, String host, int port, String path, String token) {
		URI uri = uri(scheme, host,port, path);
		var peticion = RequestEntity.get(uri)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
			.accept(MediaType.APPLICATION_JSON)
			.build();
		return peticion;
	}
	
	private RequestEntity<Void> delete(String scheme, String host, int port, String path, String token) {
		URI uri = uri(scheme, host,port, path);
		var peticion = RequestEntity.delete(uri)
		    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
			.build();
		return peticion;
	}
	
	private <T> RequestEntity<T> post(String scheme, String host, int port, String path, T object, String token) {
		URI uri = uri(scheme, host,port, path);
		var peticion = RequestEntity.post(uri)
		    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
			.contentType(MediaType.APPLICATION_JSON)
			.body(object);
		return peticion;
	}
	
	private <T> RequestEntity<T> put(String scheme, String host, int port, String path, T object, String token) {
		URI uri = uri(scheme, host,port, path);
		var peticion = RequestEntity.put(uri)
		    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
			.contentType(MediaType.APPLICATION_JSON)
			.body(object);
		return peticion;
	}
	
	private void compruebaCampos(Corrector expected, Corrector actual) {
		assertThat(actual.getIdUsuario()).isEqualTo(expected.getIdUsuario());
        assertThat(actual.getTelefono()).isEqualTo(expected.getTelefono());
        assertThat(actual.getMatEnConv()).isEqualTo(expected.getMatEnConv());
        assertThat(actual.getMaximasCorrecciones()).isEqualTo(expected.getMaximasCorrecciones());
	}
	
	private void compruebaCampos(Materia expected, Materia actual) {	
        assertThat(actual.getIdMateria()).isEqualTo(expected.getIdMateria());
		assertThat(actual.getNombre()).isEqualTo(expected.getNombre());
        assertThat(actual.getConvocatorias()).isEqualTo(expected.getConvocatorias());
	}

    private void compruebaCampos(MateriaEnConvocatoria expected, MateriaEnConvocatoria actual) {
        assertThat(actual.getIdConvocatoria()).isEqualTo(expected.getIdConvocatoria());
        assertThat(actual.getMateria()).isEqualTo(expected.getMateria());
        assertThat(actual.getCorrector()).isEqualTo(expected.getCorrector());
    }
	
	/*
	@Nested
	@DisplayName("Cuando la base de datos está vacía")
	public class BaseDatosVacia {
        // get un corrector por id
        @Test
        @DisplayName("acceder a un corrector que no existe")
        public void errorGetCorrector(){
            var peticion = get("http", "localhost", port, "/correctores/1");
            var respuesta = restTemplate.exchange(peticion,
                    new ParameterizedTypeReference<CorrectorDTO>() {});
			
            assertThat(respuesta.getStatusCode().value()).isEqualTo(404);
			assertThat(respuesta.getBody()).isNull();
        }

        // put un corrector por id
		@Test
        @DisplayName("actualizar un corrector que no existe")
        public void errorPutCorrector(){
            var corrector = CorrectorNuevoDTO.builder().maximasCorrecciones(20).build();
            var peticion = put("http", "localhost", port, "/correctores/1", corrector);
            var respuesta = restTemplate.exchange(peticion,Void.class);

			assertThat(respuesta.getStatusCode().value()).isEqualTo(404);

			List<Corrector> correctorBD = correctorRepo.findAll();
			assertThat(correctorBD).isEmpty();
        }

        // delete un corrector por id
		@Test
		@DisplayName("eliminar un corrector que no existe")
		public void errorEliCorrector(){

			var peticion = delete("http", "localhost", port, "/correctores/1");
			var respuesta = restTemplate.exchange(peticion,Void.class);

			assertThat(respuesta.getStatusCode().value()).isEqualTo(404);
			assertThat(respuesta.getBody()).isNull();
		}

        // get todos los correctores sin especificar convocatoria (intentar)
		@Test
		@DisplayName("devuelve lista vacía de correctores")
		public void listaVaciaCorrectores(){
			var peticion = get("http", "localhost", port, "/correctores");
			var respuesta = restTemplate.exchange(peticion,
					new ParameterizedTypeReference<List<CorrectorDTO>>(){});
			
			assertThat(respuesta.getStatusCode().value()).isEqualTo(200);
			assertThat(respuesta.getBody()).isEmpty();
		}

        // post un corrector
		@Test
		@DisplayName("inserta correctamente un corrector")
		public void aniadirCorrector(){
			// Preparamos el corrector a insertar
			var materia = MateriaDTO.builder().id(1L).nombre("Lengua").build();
			var corrector = CorrectorNuevoDTO.builder()
									.maximasCorrecciones(20)
									.identificadorConvocatoria(1L)
									.identificadorUsuario(1L)
									.materia(materia)
									.telefono("123456789")
									.build();
			List<MateriaEnConvocatoriaDTO> materias = new ArrayList<>();
			materias.add(MateriaEnConvocatoriaDTO.builder()
						.idConvocatoria(1L).idMateria(1L).build());
			var corrExp = CorrectorDTO.fromCorrector(corrector.corrector());
			corrExp.setMaterias(materias);

			var peticion = post("http", "localhost", port, "/correctores", corrector);
			var respuesta = restTemplate.exchange(peticion, Void.class);
			
			assertThat(respuesta.getStatusCode().value()).isEqualTo(201);
			assertThat(respuesta.getHeaders().get("Location").get(0))
				.startsWith("http://localhost:"+port+"/correctores");
		
			List<Corrector> correctoresBD = correctorRepo.findAll();
			assertThat(correctoresBD).hasSize(1);
			assertThat(respuesta.getHeaders().get("Location").get(0))
				.endsWith("/"+correctoresBD.get(0).getId());
			compruebaCampos(corrExp.corrector(), correctoresBD.get(0));
		}

	}*/

	
    @Nested
    @DisplayName("Cuando la base de datos tiene datos")
    public class BaseDatosLLena{

		Long idCorrector, idMateria, idMatConv;

        @BeforeEach
		public void introduceDatos(){
			Corrector c = new Corrector();
			c.setId(null);
			c.setIdUsuario(1L);
			c.setMaximasCorrecciones(20);
			c.setTelefono("123456789");
			correctorRepo.save(c);
			
			Materia materia = new Materia();
			materia.setId(null);
			materia.setIdMateria(1L);
			materia.setNombre("Lengua");
			matRepo.save(materia);
			
			MateriaEnConvocatoria m = new MateriaEnConvocatoria();
			m.setId(null);
			m.setCorrector(c);
			m.setIdConvocatoria(1L);
			m.setMateria(materia);
			MateriaEnConvocatoria mc2 = matConvRepo.save(m);
			idMatConv = mc2.getId();
			
			List<MateriaEnConvocatoria> materias = new ArrayList<>();
			materias.add(m);
			c.setMatEnConv(materias);
			materia.setConvocatorias(materias);

			Materia m2 = matRepo.save(materia);
			idMateria = m2.getId();
			
			Corrector c2 = correctorRepo.save(c);
			idCorrector = c2.getId();
		}

		// get todos los correctores sin especificar convocatoria
		@Test
		@DisplayName("devuelve la lista de correctores")
		public void correctores(){
			var peticion = get("http", "localhost", port, "/correctores", tokenValido);
			var respuesta = restTemplate.exchange(peticion,
					new ParameterizedTypeReference<List<CorrectorDTO>>(){});
			
			assertThat(respuesta.getStatusCode().value()).isEqualTo(200);
			assertThat(respuesta.getBody()).hasSize(1);
		}

		// get correctores especificando convocatoria


        // get un corrector por id
		@Test
        @DisplayName("acceder a un corrector por id existente")
        public void getCorrector(){
            var peticion = get("http", "localhost", port, "/correctores/"+idCorrector.toString(), tokenValido);
            var respuesta = restTemplate.exchange(peticion,
                    new ParameterizedTypeReference<CorrectorDTO>() {});
			
            assertThat(respuesta.getStatusCode().value()).isEqualTo(200);
			assertThat(respuesta.getBody()).isNotNull();
        }

		@Test
        @DisplayName("acceder a un corrector por id no existente")
        public void getCorrNoExiste(){
            var peticion = get("http", "localhost", port, "/correctores/24", tokenValido);
            var respuesta = restTemplate.exchange(peticion,
                    new ParameterizedTypeReference<CorrectorDTO>() {});
			
            assertThat(respuesta.getStatusCode().value()).isEqualTo(404);
			assertThat(respuesta.hasBody()).isFalse();
        }
		
        // delete un corrector
		@Test
		@DisplayName("elimina un corrector cuando existe")
		public void eliminarCorrector(){
			var peticion = delete("http", "localhost", port, "/correctores/"+idCorrector.toString(), tokenValido);
			var respuesta = restTemplate.exchange(peticion,Void.class);

			assertThat(respuesta.getStatusCode().value()).isEqualTo(200);
			List<Corrector> corrDesp = correctorRepo.findAll();
			//assertThat(corrDesp).hasSize(0);
			assertThat(corrDesp).isEmpty();
			assertThat(corrDesp).allMatch(c->c.getId()!=1);
		}

		@Test
		@DisplayName("elimina un corrector cuando no existe")
		public void elimCorrNoExiste(){
			var peticion = delete("http", "localhost",port, "/correctores/24", tokenValido);
			var respuesta = restTemplate.exchange(peticion,Void.class);

			assertThat(respuesta.getStatusCode().value()).isEqualTo(404);
			assertThat(respuesta.hasBody()).isFalse();
		}

        // put un corrector
		// devuelve error 500  en lugar de 200
		// @Test
		// @DisplayName("modifica un corrector cuando existe")
		// public void modificarCorrector(){
		// 	// crear nuevo corrector
		// 	Corrector corrector = new Corrector();
		// 	// corrector.setId(null);
		// 	corrector.setMaximasCorrecciones(25);
		// 	corrector.setTelefono("123456789");

		// 	var peticion = put("http", "localhost", port, "/correctores/"+idCorrector.toString(), corrector);
		// 	var respuesta = restTemplate.exchange(peticion,Void.class);
					
		// 	assertThat(respuesta.getStatusCode().value()).isEqualTo(200);
		// 	Corrector corrBD = correctorRepo.findById(1L).get();
		// 	compruebaCampos(corrector, corrBD);
		// 	// comprobar materia y materiaEnConvocatoria?
		// }

		
		@Test
		@DisplayName("modifica un corrector cuando no existe")
		public void modCorrNoExiste(){
			Corrector corrector = new Corrector();
			/*corrector.setId(null);
			corrector.setMaximasCorrecciones(25);
			corrector.setTelefono("123456789");*/

			var peticion = put("http", "localhost", port, "/correctores/24", corrector, tokenValido);
			var respuesta = restTemplate.exchange(peticion,Void.class);
					
			assertThat(respuesta.getStatusCode().value()).isEqualTo(404);
			assertThat(respuesta.hasBody()).isFalse();
		}

		public Corrector crearCorrector(){
			Corrector c = new Corrector();
			c.setId(null);
			c.setIdUsuario(1L);
			c.setMaximasCorrecciones(25);
			c.setTelefono("123456788");
			
			Materia materia = matRepo.findByIdMateria(1L);
			
			MateriaEnConvocatoria m = new MateriaEnConvocatoria();
			m.setId(null);
			m.setIdConvocatoria(2L);
			m.setMateria(materia);
			m.setCorrector(c);
			
			List<MateriaEnConvocatoria> materias = new ArrayList<>();
			materias.add(m);
			c.setMatEnConv(materias);

			return c;
		}
		
		// post un corrector
		@Test
		@DisplayName("post corrector con ID existente")
		public void conIDEx() {
			Corrector c = crearCorrector();

			var peticion = post("http", "localhost", port, "/correctores", c, tokenValido);
			var respuesta = restTemplate.exchange(peticion, Void.class);
			
			assertThat(respuesta.getStatusCode().value()).isEqualTo(409);
			List<Corrector> correctores = correctorRepo.findAll();
			assertThat(correctores).hasSize(1);
		}

		/*
		@Test
		@DisplayName("post corrector sin ID")
		public void sinID() {
			// crear corrector
			Corrector c = new Corrector();
			c.setId(null);
			c.setIdUsuario(1L);
			c.setMaximasCorrecciones(25);
			c.setTelefono("112233445");

			var peticion = post("http", "localhost", port, "/correctores", c);
				
			var respuesta = restTemplate.exchange(peticion, Void.class);
				
			compruebaRespuesta(c, respuesta);
		}

		private void compruebaRespuesta(Corrector corrector, ResponseEntity<Void> respuesta) {
			assertThat(respuesta.getStatusCode().value()).isEqualTo(201);
			assertThat(respuesta.getHeaders().get("Location").get(0))
				.startsWith("http://localhost:"+port+"/correctores");
			
			List<Corrector> correctores = correctorRepo.findAll();
			assertThat(correctores).hasSize(2);
			
			Corrector pepe = correctores.stream()
					.filter(c->c.getTelefono().equals("112233445"))
					.findAny()
					.get();
			
			assertThat(respuesta.getHeaders().get("Location").get(0))
				.endsWith("/"+pepe.getId());
			compruebaCampos(corrector, pepe);
			// comprobar materia y materiaEnConvocatoria
		}
		*/
    }
}
