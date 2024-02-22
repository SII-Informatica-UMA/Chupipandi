package sii.ms_evalexamenes.controllers;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import sii.ms_evalexamenes.util.JwtGenerator;

@RestController
@CrossOrigin(origins = "http://localhost:4200/")
@RequestMapping("/token")
public class TokenAPI {
    private final static String KEY = "sistemasinformacioninternet20222023sistemasinformacioninternet20222023";

    /**
	 * Comprueba si un token pasado por la URL es válido o no
     * @param token (para query) token a comprobar
     * @return {@code 200 OK} - Booleano que indica la validez
     */
    @GetMapping("/validez")
    public ResponseEntity<Boolean> comprobarValidez(@RequestParam String token) {
        boolean isValid = true;
        try {
            // Si el token no es valido (por firma incorrecta o por fecha exp) salta la excepcion
            Jwts.parserBuilder().setSigningKey(KEY.getBytes()).build().parseClaimsJws(token).getBody();
        } catch (SignatureException | ExpiredJwtException | MalformedJwtException e) {
            isValid = false;
        } catch (Exception e) {
            isValid = false;
        }
        return ResponseEntity.ok(isValid);
    }

    /**
	 * Genera y devuelve un nuevo token, con una validez de 30 minutos
     * @return {@code 200 OK} - Token generado
     */
    @GetMapping("/nuevo")
    public ResponseEntity<String> nuevoToken() {
        String token = JwtGenerator.createToken("user", 30, new String[]{"VICERRECTORADO", "CORRECTOR"});
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).body(String.format("{\"token\": \"%s\"}", token));
    }
}
