package sii.ms_evalexamenes;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;
import org.junit.jupiter.api.DisplayName;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriBuilderFactory;

import sii.ms_evalexamenes.util.JwtGenerator;

@ActiveProfiles("dev")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DisplayName("En el servicio de token:")
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
class TokenAPITests {
    @Autowired
	private TestRestTemplate restTemplate;
	
	@Value(value="${local.server.port}")
	private int port;

    String tokenValido = JwtGenerator.createToken("user", 5, "VICERRECTORADO");
    String tokenCaducado = JwtGenerator.createToken("user", -1, "VICERRECTORADO");
    String tokenVacio = "";

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

	private URI uriQuery(String scheme, String host, int port, String token, String ...paths) {
		UriBuilderFactory ubf = new DefaultUriBuilderFactory();
		UriBuilder ub = ubf.builder()
				.scheme(scheme)
				.queryParam("token", token)
				.host(host).port(port);
		for (String path: paths) {
			ub = ub.path(path);
		}
		return ub.build();
	}

    private RequestEntity<Void> get(String scheme, String host, int port, String path) {
		URI uri = uri(scheme, host, port, path);
		var peticion = RequestEntity.get(uri)
			.accept(MediaType.APPLICATION_JSON)
			.build();
		return peticion;
	}

    private RequestEntity<Void> getCheck(String scheme, String host, int port, String path, String token) {
		URI uri = uriQuery(scheme, host, port, token, path);
		var peticion = RequestEntity.get(uri)
			.accept(MediaType.APPLICATION_JSON)
			.build();
		return peticion;
	}

    @Test
    @DisplayName("Se crea un token nuevo")
    public void creaTokenOK() {
        var peticion = get("http", "localhost", port, "/token/nuevo");
        var respuesta = restTemplate.exchange(peticion, new ParameterizedTypeReference<String>() {});
        assertThat(respuesta.getStatusCode().value()).isEqualTo(200);
        assertTrue(respuesta.getBody().contains("\"token\":"));
    }

    @Test
    @DisplayName("Se comprueba token correcto")
    public void checkTokenOK() {
        var peticion = getCheck("http", "localhost", port, "/token/validez", tokenValido);
        var respuesta = restTemplate.exchange(peticion, new ParameterizedTypeReference<Boolean>() {});
        assertThat(respuesta.getStatusCode().value()).isEqualTo(200);
        assertTrue(respuesta.getBody().booleanValue());
    }

    @Test
    @DisplayName("Se comprueba token caducado")
    public void checkTokenCad() {
        var peticion = getCheck("http", "localhost", port, "/token/validez", tokenCaducado);
        var respuesta = restTemplate.exchange(peticion, new ParameterizedTypeReference<Boolean>() {});
        assertThat(respuesta.getStatusCode().value()).isEqualTo(200);
        assertFalse(respuesta.getBody().booleanValue());
    }

    @Test
    @DisplayName("Se comprueba token vacio")
    public void checkTokenVacio() {
        var peticion = getCheck("http", "localhost", port, "/token/validez", tokenVacio);
        var respuesta = restTemplate.exchange(peticion, new ParameterizedTypeReference<Boolean>() {});
        assertThat(respuesta.getStatusCode().value()).isEqualTo(200);
        assertFalse(respuesta.getBody().booleanValue());
    }
}
