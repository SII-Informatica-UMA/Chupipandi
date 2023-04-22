package sii.ms_corrector.security;

import java.util.List;
import java.util.Map;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.SignatureException;

@SuppressWarnings("unchecked")  // saltaba un aviso por la no comprobacion de tipos (en 'claims.get()')
public class TokenUtils {
    private final static String KEY = "sistemasinformacioninternet20222023sistemasinformacioninternet20222023";

    public static boolean comprobarAcceso(Map<String,String> header, List<String> allowedRoles) {
        // Falta definir un formato estandar para el token
        // Cuando el microservicio que deba ocuparse lo haga, se cambiará el código
        // Postman añade la cabecera como 'authorization', si fuera nula, pruebo con 'Authorization' para confirmar
        String tokenHeader = header.get("authorization") == null ? header.get("Authorization") : header.get("authorization");
        if (tokenHeader == null || !tokenHeader.startsWith("Bearer "))
            return false;
        String token = tokenHeader.substring(7);
        Claims claims;
        try {
            // Si el token no es valido (por firma incorrecta o por fecha exp) salta la excepcion
            claims = Jwts.parserBuilder().setSigningKey(KEY.getBytes()).build().parseClaimsJws(token).getBody();
        } catch (SignatureException | ExpiredJwtException e) {
            return false;
        }
        List<String> userRoles = claims.get("roles", List.class).stream().map(rol -> (String) rol).toList();
        return userRoles.stream().anyMatch(rol -> allowedRoles.contains(rol));
    }

}
