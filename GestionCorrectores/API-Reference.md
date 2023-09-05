## API Reference

> **Note**
> Para acceder a casi todos los endpoints es necesario incluir un token en la cabecera de la petición. Este debe seguir el formato especificado [aquí](src/main/java/sii/ms_corrector/security/README.md#formato-ejemplo). La excepción son los endpoints bajo la dirección */token*, pues son públicos.
>
> La incorporación del token la hace la aplicacion automáticamente, pero si se desea acceder al servicio desde la terminal es necesario proporcionarlo. Se recomienda para ello exportar una variable donde se almacene el token.

#### Obtiene un corrector concreto

```http
GET /correctores/{id}
```

| Parameter | Type   | Description                             |
| :-------- | :----- | :-------------------------------------- |
| `id`      | `long` | **Required**. Id del corrector a buscar |

Uso/Ejemplo
```bash
curl -H "Authorization: Bearer $TOKEN" http://localhost:8081/correctores/1
```

#### Actualiza un corrector

```http
PUT /correctores/{id}
```

| Parameter | Type   | Description                             |
| :-------- | :----- | :-------------------------------------- |
| `id`      | `long` | **Required**. Id del corrector a editar |

Uso/Ejemplo
```bash
curl -X 'PUT' \
    'http://localhost:8081/correctores/1' \
    -H "Authorization: Bearer $TOKEN" \
    -H "Content-Type: application/json" \
    -d '{
        "identificadorUsuario": 1,
        "identificadorConvocatoria": 1,
        "telefono": "111222333",
        "materia": {
            "id": 1,
            "nombre": "Matemáticas II"
        },
        "maximasCorrecciones": 10
    }'
```

> **Important**
> Se podrá proporcionar el ID de la materia, el nombre de esta, o ambos. En cualquier caso, consultar las [materias disponibles](#materias-disponibles) y sus correspondientes IDs. Una incorrespondencia resultará en un error.

#### Elimina un corrector

```http
DELETE /correctores/{id}
```

| Parameter | Type   | Description                             |
| :-------- | :----- | :-------------------------------------- |
| `id`      | `long` | **Required**. Id del corrector a borrar |

Uso/Ejemplo
```bash
curl -X "DELETE" -H "Authorization: Bearer $TOKEN" http://localhost:8081/correctores/1
```

#### Obtiene la lista de correctores del sistema

```http
GET /correctores
```

| Parameter                  | Type   | Description                                  |
| :------------------------- | :----- | :------------------------------------------- |
| `idConvocatoria` (*query*) | `long` | **Optional**. Id de la convocatoria a buscar |

Uso/Ejemplo
```bash
curl -H "Authorization: Bearer $TOKEN" http://localhost:8081/correctores?idConvocatoria=1
```

#### Crea un nuevo corrector para la convocatoria vigente (que se pasa dentro del objeto)

```http
POST /correctores
```

Uso/Ejemplo
```bash
curl -X 'POST' \
    'http://localhost:8081/correctores' \
    -H "Authorization: Bearer $TOKEN" \
    -H "Content-Type: application/json" \
    -d '{
        "identificadorUsuario": 1,
        "identificadorConvocatoria": 1,
        "telefono": "111222333",
        "materia": {
            "id": 1,
            "nombre": "Matemáticas II"
        },
        "maximasCorrecciones": 10
    }'
```

> **Important**
> Se podrá proporcionar el ID de la materia, el nombre de esta, o ambos. En cualquier caso, consultar las [materias disponibles](#materias-disponibles) y sus correspondientes IDs. Una incorrespondencia resultará en un error.

#### Obtiene un nuevo token

```http
GET /token/nuevo
```

Uso/Ejemplo
```bash
curl http://localhost:8081/token/nuevo
```

#### Valida un token

```http
GET /token/validez?token={token}
```

Uso/Ejemplo
```bash
curl http://localhost:8081/token/validez?token=$TOKEN
```

## Materias disponibles

| ID | Nombre Materia                 |
| -- | ------------------------------ |
| 1  | Matemáticas II                 |
| 2  | Física                         |
| 3  | Química                        |
| 4  | Lengua Castellana y Literatura |
| 5  | Historia de España             |
| 6  | Geografía                      |
| 7  | Biología                       |
| 8  | Lengua Extranjera              |
| 9  | Dibujo Técnico II              |
| 10 | Economía                       |
| 11 | Filosofía                      |

## Relacionado
Para mas detalles consultar: [OpenAPI definition](https://jfrchicanog.github.io/html/correctores.html)