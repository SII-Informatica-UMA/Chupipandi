
# Proyecto Microservicios

Implementación de los microservicios de Gestión de Correctores y Evaluación de Exámenes para la asignatura de Sistemas de Información para Internet.
- **GestionCorrectores**: puede ejecutarse independientemente del otro microservicio.
- **EvaluacionExamenes**: depende del microservicio de correctores para su correcto funcionamiento.

## Despliegue con Docker
### Docker compose
Para poner en marcha los dos microservicios al completo haremos uso de **[docker-compose](https://docs.docker.com/compose/gettingstarted/)**. Las imágenes se construirán automáticamente y se pondrán en ejecución **5 contenedores**: correctores back y front, evalexamenes back y front, más una base de datos común a ambos microservicios. En conjunto a ellos, se crean una red virtual **chupipandi-net** y un volumen **db-data** donde persistirán los datos de la BD.

```bash
# Arranca en background los contenedores
docker compose up -d
# Para y borra los contenedores (incluir el flag indicado para borrar tambien el volumen de datos)
docker compose down [-v, --volumes]
```

### Direcciones web
| Microservicio | URL | Microservicio | URL |
| -------- | --- | ------ | ---- |
| Correctores backend | http://localhost:8081/ | Evalexamenes backend | http://localhost:8080/ |
| Correctores frontend | http://localhost:4242/ | Evalexamenes frontend | http://localhost:4200/ |

Consulta: [GestionCorrectores API](GestionCorrectores/API-Reference.md) y [EvaluacionExamenes API](EvaluacionExamenes/API-Reference.md)

---

En caso de querer correr los contenedores individualmente, cada carpeta contiene su correspondiente `Dockerfile`, a excepción de el la base de datos, que se encuentra en la raiz del proyecto (`Dockerfile.database`).

> **Note**
> Tener en cuenta que al ejecutar con docker, ambos microservicios necesitan tener el contenedor de la base de datos corriendo para poder iniciarse correctamente. Además, para comunicar los contenedores entre sí, es necesario crear una nueva *network* y asignársela mediante el flag `--network <red>` a cada contenedor.

### Contenedor para H2-Database

Para levantar la base de datos h2 se puede usar el siguiente comando:
```bash
# Construye la imagen
docker build . -f Dockerfile.database -t database-h2:latest
# Pone en marcha un contenedor
docker run -itp 8082:8082 --name db-service --network <red> database-h2:latest
```
Para entrar por la web, acceder a través de http://localhost:8082.
| Field | Value |
| :-----: |:-----:|
| URL JDBC | *jdbc:h2:ms_database* |
| User & Passwd  | \<empty> |

### Contenedor para Microservicio Backend/Frontend
Se recomienda utilizar los siguientes tags propuestos al construir las imágenes.
```bash
# Construir imagen
docker build <carpeta con Dockerfile> -t <microservicio>/<back,front>
# Poner en marcha contenedor
docker run -itp <host port>:<cont.port> --name <name> --network <red> <image>
```

## Despliegue con maven y/o archivo jar

- Microservicio [Gestión de Correctores](GestionCorrectores/README.corr.md)
- Microservicio [Evaluación de Examenes](EvaluacionExamenes/README.eval.md)

# Documentación
## Authors

- [@M4rdom](https://www.github.com/M4rdom)
- [@MAngelGC](https://www.github.com/MAngelGC)
- [@fcristallocagnoli](https://www.github.com/fcristallocagnoli)
- [@RocioLN15](https://github.com/RocioLN15)
- [@jorgeroma](https://github.com/jorgeroma)
