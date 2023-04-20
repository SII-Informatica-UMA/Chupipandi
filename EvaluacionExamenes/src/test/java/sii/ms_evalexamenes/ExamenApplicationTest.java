package sii.ms_evalexamenes;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.util.ArrayList;
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
import sii.ms_evalexamenes.dtos.ExamenNuevoDTO;
import sii.ms_evalexamenes.entities.Examen;
import sii.ms_evalexamenes.repositories.ExamenRepository;
//import sii.ms_evalexamenes.repositories.MateriaRepository;


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
    //@Autowired
    //private MateriaRepository materiarepository;



	@Nested
	@DisplayName("cuando la base de datos está vacía")
	public class BaseDatosVacia {
    
        private String accessToken = "eyJhbGciOiJIUzI1NiJ9.eyJleHAiOjE2ODQ1MDI1ODgsInJvbGVzIjpbIkNPUlJFQ1RPUiJdfQ.5cWDJdzmurLrtipgtCyikuccojcIeup6aioNwCgTlPU";

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
        
        
        private RequestEntity<Void> get(String scheme, String host, int port, String path,boolean authorized) {
            URI uri = uri(scheme, host,port, path);
            
            if (authorized){
                var peticion = RequestEntity.get(uri)
                .accept(MediaType.APPLICATION_JSON).header("Authorization", "Bearer "+accessToken)
                .build();
                return peticion;
          
            }else {
                var peticion = RequestEntity.get(uri)
                .accept(MediaType.APPLICATION_JSON)
                .build();
                return peticion;
            }

            
            
            
        }
               
        private RequestEntity<Void> delete(String scheme, String host, int port, String path,boolean authorized) {
            URI uri = uri(scheme, host,port, path);
            if(authorized){
                var peticion = RequestEntity.delete(uri).header("Authorization", "Bearer "+accessToken)
                .build();
                return peticion;
            }
            else{
                var peticion = RequestEntity.delete(uri)
                .build();
                return peticion;
            }
            
        }
        
        private <T> RequestEntity<T> post(String scheme, String host, int port, String path, T object,boolean authorized) {
            URI uri = uri(scheme, host,port, path);
            var peticion = RequestEntity.post(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .body(object);
            return peticion;
        }
        
        private <T> RequestEntity<T> put(String scheme, String host, int port, String path, T object,boolean authorized) {
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
                //materiarepository.deleteAll();
            }

            @Test
            @DisplayName("Devuelve 403 al acceder a un Examen Concreto Sin Autenticacion")
            public void getExamenConcreto() {
                var peticion = get("http", "localhost",port, "/examenes/1",false);
                var respuesta = restTemplate.exchange(peticion,new ParameterizedTypeReference<ExamenDTO>() {});              
                assertThat(respuesta.getStatusCode().value()).isEqualTo(403);
            }

            @Test
            @DisplayName("Devuelve 200 al acceder a un Examen Concreto SI Existente CON Autenticacion")
            public void getExamenConcreto1() { 
                ExamenDTO examen = new ExamenDTO(1L, 1L, 1L, 1F);
                examenrepository.save(examen.examen());
                var peticion = get("http", "localhost",port, "/examenes/1",false);
                var respuesta = restTemplate.exchange(peticion,new ParameterizedTypeReference<ExamenDTO>() {});   
                assertThat(respuesta.getStatusCode().is2xxSuccessful());

            }

            @Test
            @DisplayName("Devuelve 404 al acceder a un Examen Concreto NO Existente CON Autenticacion")
            public void getExamenConcreto2() { 
                var peticion = get("http", "localhost",port, "/examenes/1",true);
                var respuesta = restTemplate.exchange(peticion,new ParameterizedTypeReference<ExamenDTO>() {});   
                assertThat(respuesta.getStatusCode().is4xxClientError());
                assertThat(respuesta.getStatusCode().value()).isEqualTo(404);

            }
            


    
            @Test
            @DisplayName("Devuelve correctamente Lista al acceder a una Asignacion en concreto")
            public void getAsignaciones() {
                var peticion = get("http", "localhost",port, "/examenes/asignacion",true);
                var respuesta = restTemplate.exchange(peticion,new ParameterizedTypeReference <List<AsignacionDTO>>() {});                 
                assertThat(respuesta.getStatusCode().is2xxSuccessful());    
            }


            
        
            
            





        }
    }
}