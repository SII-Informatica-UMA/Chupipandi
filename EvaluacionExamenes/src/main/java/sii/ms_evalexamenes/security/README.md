## Implementación de la tarea #23. Seguridad mediante JWT.
Para hacer uso de la clase `TokenUtils`, basta con seguir estos pasos para cada endpoint que queramos segurizar.
```java
@GetMapping("{id}")
// Añadimos a los parámetros la cabecera de la petición
public ResponseEntity<T> obtener( . . . @RequestHeader Map<String, String> header) {
    // Le pasamos la cabecera y el rol (o roles) que deberia tener quien quiera acceder a este endpoint
    // Una lista vacia implica que el endpoint es público (todos tiene acceso)
    if (!TokenUtils.comprobarAcceso(header, Arrays.asList("VICERRECTOR", "CORRECTOR"))) {
        throw new AccesoNoAutorizado();
    }
    // . . . continuamos con el metodo como habitualmente
}
```
## Creación de tokens
1. Seguir el enlace a la [web de JWT](https://jwt.io/).
2. Incluir el el *payload* unos datos como el ejemplo. El campo 'Subject' (`sub`) en principio no es relevante para este microservicio.
3. Copiar el token y pegar junto con la clave secreta en *postman* para hacer las pruebas.

Siendo la clave secreta: `sistemasinformacioninternet20222023sistemasinformacioninternet20222023`
Y el algoritmo de firma: `HS512`
### Formato ejemplo
```json
{
  "roles": [
    "CORRECTOR",
    "VICERRECTORADO"
  ],
  "sub": "user",
  "iat": 1681910588,
  "exp": 1684502588
}
```
La fecha de expiración del token ejemplo está establecida con una duración de un mes (hasta el 19 de Mayo de 2023).