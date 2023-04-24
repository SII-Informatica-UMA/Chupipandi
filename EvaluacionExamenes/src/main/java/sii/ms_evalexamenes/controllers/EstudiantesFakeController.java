package sii.ms_evalexamenes.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.javafaker.Faker;

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

    @GetMapping("{id}")
    public ResponseEntity<Map<String, Object>> getEstudiante(@PathVariable Long id) {
        Optional<JSONObject> estudiante = listaEstudiantes.stream().filter(est -> est.getLong("id") == id).findFirst();
        if (estudiante.isEmpty())
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(estudiante.get().toMap());
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getEstudiantes() {
        return ResponseEntity.ok(listaEstudiantes
                                .stream()
                                .map(est -> est.toMap())
                                .toList());
    }

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

    private static String getValidDNI(String dni) {
        char[] letras = {'T', 'R', 'W', 'A', 'G', 'M', 'Y', 'F', 'P', 'D', 'X',
        'B', 'N', 'J', 'Z', 'S', 'Q', 'V', 'H', 'L', 'C', 'K', 'E'};

        int n = 0;
        String dniNuevo = "";

        for (char c : dni.substring(1).toCharArray()) {
            if (c != '-' && c != ' ') {
                dniNuevo += c; 
                n += Integer.valueOf(c);
            }
        }

        return dniNuevo + letras[n % 23];
    }
}
