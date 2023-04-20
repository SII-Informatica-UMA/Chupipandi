package sii.ms_corrector;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
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

import sii.ms_corrector.entities.*;
import sii.ms_corrector.repositories.*;
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
	
	private void compruebaCampos(Corrector expected, Corrector actual) {
		assertThat(actual.getIdUsuario()).isEqualTo(expected.getIdUsuario());
        assertThat(actual.getTelefono()).isEqualTo(expected.getTelefono());
        assertThat(actual.getMatEnConv()).isEqualTo(expected.getMatEnConv());
		//System.out.println("\n actual " + actual.getMatEnConv());
		//System.out.println("\nexpected " + expected.getMatEnConv());
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
	
	@Nested
	@DisplayName("Cuando la base de datos está vacía")
	public class BaseDatosVacia {
		@BeforeEach
		public void eliminar(){
			correctorRepo.deleteAll();
		}

        // get un corrector por id
        @Test
        @DisplayName("acceder a un corrector que no existe")
        public void errorGetCorrector(){
            var peticion = get("http", "localhost", port, "/correctores/1");
            var respuesta = restTemplate.exchange(peticion,
                    new ParameterizedTypeReference<CorrectorDTO>() {});
			
            assertThat(respuesta.getStatusCode().value()).isEqualTo(404);
			// comprobar que está vacía
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
		// @Test
		// @DisplayName("inserta correctamente un corrector")
		// public void aniadirCorrector(){
		// 	Preparamos el corrector a insertar
		// 	var materia = MateriaDTO.builder().id(1L).nombre("Lengua").build();
		// 	var corrector = CorrectorNuevoDTO.builder()
		// 							.maximasCorrecciones(20)
		// 							.identificadorConvocatoria(1L)
		// 							.identificadorUsuario(1L)
		// 							.materia(materia)
		// 							.telefono("123456789")
		// 							.build();
		// 	List<MateriaEnConvocatoriaDTO> materias = new ArrayList<>();
		// 	materias.add(MateriaEnConvocatoriaDTO.builder()
		// 				.idConvocatoria(1L).idMateria(1L).build());
		// 	var corrExp = CorrectorDTO.fromCorrector(corrector.corrector());
		// 	corrExp.setMaterias(materias);
		// 	Preparamos la petición con el corrector dentro
		// 	var peticion = post("http", "localhost", port, "/correctores", corrector);
			
		// 	Invocamos al servicio REST 
		// 	var respuesta = restTemplate.exchange(peticion, Void.class);
			
		// 	Comprobamos el resultado
		// 	assertThat(respuesta.getStatusCode().value()).isEqualTo(201);
		// 	assertThat(respuesta.getHeaders().get("Location").get(0))
		// 		.startsWith("http://localhost:"+port+"/correctores");
		
		// 	List<Corrector> correctoresBD = correctorRepo.findAll();
		// 	assertThat(correctoresBD).hasSize(1);
		// 	assertThat(respuesta.getHeaders().get("Location").get(0))
		// 		.endsWith("/"+correctoresBD.get(0).getId());
		// 	compruebaCampos(corrExp.corrector(), correctoresBD.get(0));
		// }

	}

    @Nested
    @DisplayName("Cuando la base de datos tiene datos")
	@TestInstance(Lifecycle.PER_CLASS)
    public class BaseDatosLLena{
        @BeforeAll
		public void anyadir(){
			Corrector c = new Corrector();
			c.setIdUsuario(1L);
			c.setMaximasCorrecciones(20);
			c.setTelefono("123456789");

			Materia materia = new Materia();
			materia.setIdMateria(1L);
			materia.setNombre("Lengua");
			matRepo.save(materia);

			MateriaEnConvocatoria m = new MateriaEnConvocatoria();
			m.setIdConvocatoria(1L);
			m.setMateria(materia);
			
			List<MateriaEnConvocatoria> materias = new ArrayList<>();
			materias.add(m);
			c.setMatEnConv(materias);

			correctorRepo.save(c);
			m.setCorrector(c);
			matConvRepo.save(m);
		}

		// get todos los correctores sin especificar convocatoria (intentar)
		@Test
		@DisplayName("devuelve la lista de correctores")
		public void correctores(){
			var peticion = get("http", "localhost", port, "/correctores");
			var respuesta = restTemplate.exchange(peticion,
					new ParameterizedTypeReference<List<CorrectorDTO>>(){});
			
			assertThat(respuesta.getStatusCode().value()).isEqualTo(200);
			assertThat(respuesta.getBody()).hasSize((int)correctorRepo.count());
			//assertThat((int)correctorRepo.count()).isEqualTo(1);
		}

        // post un corrector
        // get un corrector por id
        // delete un corrector
        // put un corrector

    }
}
