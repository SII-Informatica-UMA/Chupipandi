package sii.ms_evalexamenes.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.javafaker.Faker;

import sii.ms_evalexamenes.util.GeneratedFakeEndpoint;

@RestController
@RequestMapping("/estudiantes")
public class EstudiantesFakeController {
    private static final List<JSONObject> listaEstudiantes = new ArrayList<>();

    public EstudiantesFakeController() {
        Faker faker = Faker.instance(new Locale("es", "ES"));
        JSONObject estudianteFijo = new JSONObject()
                                        .put("id", 1)
                                        .put("apellido1", "González")
                                        .put("dni", "05981804X")
                                        .put("telefono", "+34 675118982")
                                        .put("email", "magc6102@uma.es");
        listaEstudiantes.add(estudianteFijo);
        for (int i = 2; i <= 20; ++i) {
            String dni = getValidDNI(faker.idNumber().ssnValid());
            JSONObject estudiante = generarEstudiante(Integer.toUnsignedLong(i), faker.name().lastName(), dni, "+34 " + faker.phoneNumber().cellPhone(), faker.internet().emailAddress());
            listaEstudiantes.add(estudiante);
        }
    }
    
    /**
     * Endpoint que imita el endpoint del microservicio de gestión de estudiantes (necesario para la comprobación del GET de /notas)
     * @return {@code 200 OK} - Siempre devuelve OK ya que siempre hay estudiantes en la lista
     * @annotation {@link GeneratedFakeEndpoint} Anotación para evitar el recubrimiento de JaCoCo al ser un endpoint que no pertenece a nuestro microservicio
     */
    @GeneratedFakeEndpoint
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getEstudiantes() {
        return ResponseEntity.ok(listaEstudiantes
                                .stream()
                                .map(est -> est.toMap())
                                .toList());
    }

    /**
     * Método auxiliar para generar estudiantes y rellenar la lista
     * @param id {@link Long} ID del estudiante
     * @param apellido1 {@link String} Primer apellido del estudiante
     * @param dni {@link String} DNI del estudiante
     * @param telefono {@link String} Teléfono del estudiante
     * @param email {@link String} Email del estudiante
     * @return {@link JSONObject} Objeto del estudiante creado
     */
    private static JSONObject generarEstudiante(Long id, String apellido1, String dni, String telefono, String email) {
        JSONObject estudiante = new JSONObject();
        estudiante
                .put("id", id)
                .put("apellido1", apellido1)
                .put("dni", dni)
                .put("telefono", telefono)
                .put("email", email);
        return estudiante;

    }

    /**
     * Método auxiliar para generar DNIs válidos
     * @param dni {@link String} SSN estadounidense
     * @return {@link String} DNI válido con letra
     * @annotation {@link GeneratedFakeEndpoint} Anotación para evitar el recubrimiento de JaCoCo al ser un método de un controller que no pertenece a nuestro microservicio
     */
    @GeneratedFakeEndpoint
    private static String getValidDNI(String dni) {
        char[] letras = {'T', 'R', 'W', 'A', 'G', 'M', 'Y', 'F', 'P', 'D', 'X',
        'B', 'N', 'J', 'Z', 'S', 'Q', 'V', 'H', 'L', 'C', 'K', 'E'};

        String dniNuevo = "";

        for (char c : dni.substring(1).toCharArray()) {
            if (c != '-' && c != ' ')
                dniNuevo += c; 
        }

        return dniNuevo + letras[Integer.valueOf(dniNuevo) % 23];
    }
}
