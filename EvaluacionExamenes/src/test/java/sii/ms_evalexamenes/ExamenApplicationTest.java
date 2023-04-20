package sii.ms_evalexamenes;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.util.List;

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

import sii.ms_evalexamenes.dtos.AsignacionDTO;
import sii.ms_evalexamenes.dtos.ExamenDTO;
import sii.ms_evalexamenes.entities.Examen;
import sii.ms_evalexamenes.entities.Materia;
import sii.ms_evalexamenes.repositories.ExamenRepository;


@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DisplayName("En el servicio de Examenes")
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)


class ExamenApplicationTest {
	
    @Autowired
	private TestRestTemplate restTemplate;

	@Value(value="${local.server.port}")
	private int port;

    @Autowired
    private ExamenRepository examenrepository;
    



	@Nested
	@DisplayName("cuando la base de datos está vacía")
	public class BaseDatosVacia {
        

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
		



        @Nested
        @DisplayName("Cuando no hay Examenes")
        public class checkExamenes {
            
            @BeforeEach
            public void initializeDatabase() {
                examenrepository.deleteAll();
            }

            //get a examenes/{id}
            @Test
            @DisplayName("Devuelve 404 al acceder a un Examen Concreto cuando no hay Examenes")
            public void getExamenConcreto() {
                var peticion = get("http", "localhost",port, "/examenes/1");
                var respuesta = restTemplate.exchange(peticion,new ParameterizedTypeReference<ExamenDTO>() {});              
                assertThat(respuesta.getStatusCode().value()).isEqualTo(404);
            }

            //get a examenes/{id} introduciendo un examen
             
            /* 
            @Test
            @DisplayName("Devuelve 200 al acceder a un Examen Concreto Existente")
            public void getExamenConcreto1() {    
                Materia materia = new Materia();
                materia.setId(1L);  
                examenrepository.save(materia);     
                Examen examen = new Examen();
                examen.setId(1L);
                examenrepository.save(examen);

                var peticion = get("http", "localhost",port, "/examenes/1");
                var respuesta = restTemplate.exchange(peticion,new ParameterizedTypeReference<ExamenDTO>() {});              
                assertThat(respuesta.getStatusCode().is2xxSuccessful());
            }
            */
            /*

            //put a examenes/{id}
            @Test
            @DisplayName("Devuelve 404 al modifificar nota de un Examen cuando no hay Examenes")
            public void getExamenConcreto2() {
                examenrepository.deleteAll();
                var examen = ExamenDTO.builder().materia(1L).build();
                var peticion = put("http", "localhost",port, "/examenes/1",examen);
                var respuesta = restTemplate.exchange(peticion,Void.class);
                //assertThat(respuesta.getStatusCode().value()).isEqualTo(404);
                assertThat(respuesta.getStatusCode().is2xxSuccessful());  
            }
            */

            //get a examenes/asignacion
            @Test
            @DisplayName("Devuelve correctamente Lista al acceder a una Asignacion en concreto")
            public void getAsignaciones() {
                var peticion = get("http", "localhost",port, "/examenes/asignacion");
                var respuesta = restTemplate.exchange(peticion,new ParameterizedTypeReference <List<AsignacionDTO>>() {});                 
                assertThat(respuesta.getStatusCode().is2xxSuccessful());    
            }


            @Test
            @DisplayName("Comprueba que la Lista de Asignacion este Vacia")
            public void getAsignaciones1() {
                var peticion = get("http", "localhost",port, "/examenes/asignacion");
                var respuesta = restTemplate.exchange(peticion,new ParameterizedTypeReference <List<AsignacionDTO>>() {});                 
                assertThat(respuesta.getBody().isEmpty());       
            }
        
            
            @Test
            @DisplayName("Comprueba que la Lista de Asignacion este Vacia")
            public void getAsignaciones2() {
                var peticion = get("http", "localhost",port, "/examenes/asignacion");
                var respuesta = restTemplate.exchange(peticion,new ParameterizedTypeReference <List<AsignacionDTO>>() {});                 
                assertThat(respuesta.getBody().isEmpty());       
            }





        }
    }
}