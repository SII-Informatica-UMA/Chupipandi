package sii.ms_corrector;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.util.List;

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
        // get un corrector por id
        @Test
        @DisplayName("acceder a un corrector que no existe")
        public void errorGetCorrector(){
            var peticion = get("http", "localhost", port, "/correctores/1");
            var respuesta = restTemplate.exchange(peticion,
                    new ParameterizedTypeReference<CorrectorDTO>() {});
			
            assertThat(respuesta.getStatusCode().value()).isEqualTo(404);
        }

        // put un corrector por id
		@Test
        @DisplayName("actualizar un corrector que no existe")
        public void errorPutCorrector(){
            var corrector = CorrectorDTO.builder().maximasCorrecciones(20).build();
            var peticion = put("http", "localhost", port, "/correctores/1", corrector);
            var respuesta = restTemplate.exchange(peticion,Void.class);

			assertThat(respuesta.getStatusCode().value()).isEqualTo(404);
        }

        // delete un corrector por id
		@Test
		@DisplayName("eliminar un corrector que no existe")
		public void errorEliCorrector(){
			var peticion = delete("http", "localhost",port, "/correctores/1");
			var respuesta = restTemplate.exchange(peticion,Void.class);

			assertThat(respuesta.getStatusCode().value()).isEqualTo(404);
		}

        // get todos los correctores sin especificar convocatoria (intentar)
		

        // post un corrector
		@Test
		@DisplayName("inserta correctamente un corrector")
		public void aniadirCorrector(){
			// Preparamos el corrector a insertar
			var corrector = CorrectorNuevoDTO.builder()
									.maximasCorrecciones(20)
									.telefono("123456789")
									.build();
			// Preparamos la petición con el corrector dentro
			var peticion = post("http", "localhost", port, "/correctores/1", corrector);
			
			// Invocamos al servicio REST 
			var respuesta = restTemplate.exchange(peticion, Void.class);
			
			// Comprobamos el resultado
			assertThat(respuesta.getStatusCode().value()).isEqualTo(201);
			assertThat(respuesta.getHeaders().get("Location").get(0))
				.startsWith("http://localhost:"+port+"/correctores");
		
			List<Corrector> correctoresBD = correctorRepo.findAll();
			assertThat(correctoresBD).hasSize(1);
			assertThat(respuesta.getHeaders().get("Location").get(0))
				.endsWith("/"+correctoresBD.get(0).getId());
			compruebaCampos(corrector.corrector(), correctoresBD.get(0));
		}

	}

    @Nested
    @DisplayName("Cuando la base de datos tiene datos")
    public class BaseDatosLLena{
        // get todos los correctores sin especificar convocatoria (intentar)
        // post un corrector
        // get un corrector por id
        // delete un corrector
        // put un corrector

    }
}
