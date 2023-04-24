package sii.ms_evalexamenes.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

public class JwtGenerator {

    private final static String ACCESS_TOKEN_SECRET = "sistemasinformacioninternet20222023sistemasinformacioninternet20222023";
    private final static Function<Integer, Long> ACCESS_TOKEN_VALIDITY_SECONDS = (time -> time * 60 * 60 * 1_000L);

    /**
     * Crea y devuelve un JWT
     * @param sub sujeto a quien va dirigido
     * @param expTime tiempo de validez (en horas)
     * @param roles roles asociados al sujeto
     * @return token de tipo JWT
     */
    public static String createToken(String sub, Integer expTime, String ...roles) {
        long expirationTime = ACCESS_TOKEN_VALIDITY_SECONDS.apply(expTime);
        
        Long creationTime = System.currentTimeMillis();

        Date expirationDate = new Date(creationTime + expirationTime);

        Map<String, Object> extra = new HashMap<>();
        List<String> rolesAuth = new ArrayList<>();
        for (String rol : roles) {
            rolesAuth.add(rol);
        }
        extra.put("roles", roles);

        return Jwts.builder()
                .addClaims(extra)
                .setSubject(sub)
                .setIssuedAt(new Date(creationTime))
                .setExpiration(expirationDate)
                .signWith(Keys.hmacShaKeyFor(ACCESS_TOKEN_SECRET.getBytes()))
                .compact();
    }
}
