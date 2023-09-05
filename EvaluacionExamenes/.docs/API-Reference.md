## API Reference

> **Note**
> Para acceder a casi todos los endpoints es necesario incluir un token en la cabecera de la petición. Este debe seguir el formato especificado [aquí](src/main/java/sii/ms_evalexamenes/security/README.md#formato-ejemplo). La excepción son los endpoints bajo la dirección */token* y la dirección */notas*, pues son endpoints públicos.
>
> La incorporación del token la hace la aplicacion automáticamente, pero si se desea acceder al servicio desde la terminal es necesario proporcionarlo. Se recomienda para ello exportar una variable donde se almacene el token.

#### Obtiene información de un examen concreto

```http
GET /examenes/{id}
```

| Parameter | Type   | Description                          |
| :-------- | :----- | :----------------------------------- |
| `id`      | `long` | **Required**. Id del examen a buscar |

#### Actualiza la nota de un examen concreto

```http
PUT /examenes/{id}
```

| Parameter | Type   | Description                          |
| :-------- | :----- | :----------------------------------- |
| `id`      | `long` | **Required**. Id del examen a editar |

#### Consulta la asignación de exámenes a correctores

```http
GET /examenes/asignacion
```

#### Realiza o actualiza la asignación de exámenes a correctores

```http
PUT /examenes/asignacion
```

#### Programa el envío de notificaciones de notas a los estudiantes

```http
POST /notificaciones/notas
```

#### Crea el examen de un alumno y materia

```http
POST /examenes
```

#### Consulta las notas de un/a estudiante

```http
GET /notas
```

| Parameter            | Type     | Description                                    |
| :------------------- | :------- | :--------------------------------------------- |
| `dni` (*query*)      | `string` | **Required**. DNI del/la estudiante            |
| `apellido` (*query*) | `string` | **Required**. Primer apellido de/la estudiante |

#### Obtiene la lista de exámenes corregidos y por corregir

```http
GET /examenes/correcciones
```

#### Obtiene un nuevo token

```http
GET /token/nuevo
```

Uso/Ejemplo
```bash
curl http://localhost:8080/token/nuevo
```

#### Valida un token

```http
GET /token/validez?token={token}
```

Uso/Ejemplo
```bash
curl http://localhost:8080/token/validez?token=$TOKEN
```
## Relacionado
Para mas detalles consultar: [OpenAPI definition](https://jfrchicanog.github.io/html/evaluaciones.html)