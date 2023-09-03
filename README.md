
# Proyecto Microservicios

Implementación de los microservicios de Gestión de Correctores y Evaluación de Exámenes para la asignatura de Sistemas de Información para Internet.
- GestionCorrectores: puede ejecutarse independientemente del otro microservicio.
- EvaluacionExamenes: depende del microservicio de correctores para su correcto funcionamiento.

## Puesta en marcha con Docker
### Docker compose
Para poner en marcha los dos microservicios al completo haremos uso de **[docker-compose](https://docs.docker.com/compose/gettingstarted/)**. Las imágenes se construirán automáticamente y se pondrán en ejecución 5 contenedores: correctores back y front, evalexamenes back y front, más una base de datos común a ambos microservicios.

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

---

En caso de querer correr los contenedores individualmente, cada carpeta contiene su correspondiente `Dockerfile`, a excepción de el la base de datos, que se encuentra en la raiz del proyecto.

### H2-Database

Para levantar la base de datos h2 se puede usar el siguiente comando:
```bash
# Construye la imagen
docker build . -f Dockerfile.database -t database-h2:latest
# Pone en marcha un contenedor
docker run -itp 8082:8082 --name db-service database-h2:latest
```
Para entrar por la web, acceder a través de http://localhost:8082.
| Field | Value |
| :-----: |:-----:|
| URL JDBC | *jdbc:h2:ms_database* |
| User & Passwd  | \<empty> |

### Microservicio Backend/Frontend
Se recomienda utilizar los siguientes tags propuestos al construir las imágenes.
```bash
# Construir imagen
docker build <carpeta con Dockerfile> -t <microservicio>/<back,front>
# Poner en marcha contenedor
docker run -itp <host port>:<cont.port> --name <name> <image>
```
En caso de querer que los contenedores se comuniquen entre sí, es conveniente crear una nueva *network* y asignársela mediante el flag `--network <red>` a cada contenedor.

# Gestión de Correctores
## Backend
### Ejecución mediante fichero jar:

En primer lugar ejecutamos `mvn clean package`.

Seguidamente, ejecutamos el fichero jar generado en la carpeta target, donde distinguimos dos tipos de ejecución
- Ejecución con la base de datos en memoria:
```java
java -jar -Dspring.profiles.active=dev target/ms_corrector-0.0.1-SNAPSHOT.jar
```
- Ejecución con persistencia (base de datos almacenada en carpeta *database*):
```java
java -jar -Dspring.profiles.active=prod target/ms_corrector-0.0.1-SNAPSHOT.jar
```
> **Note**
> En caso de no especificar el perfil de ejecución, se ejecutará por defecto en memoria (perfil `dev`).
- - -
### Ejecución mediante archivo por lotes:

El archivo por lotes `start.bat` hace la misma función que la ejecución mediante el fichero jar, pero de una forma más sencilla y automatizada. Para ejecutarlo, simplemente ejecutar `./start.bat` en la terminal.

Parámetros disponibles para el ejecutable:
- `-b`: ejecuta la operación en segundo plano
- `prod`: ejecuta la operación en modo producción (activa spring profile `prod`) (con persistencia de datos)
- `-skip`: para saltarse la fase de comprobación de tests

> **Note**
> Si no se le especifica el modo de ejecución, el perfil por defecto es `dev` (en memoria)

## Frontend
Teniendo ya el backend corriendo, situarse en la carpeta *frontend-correctores* y ejecutar `npm install` para instalar las dependencias necesarias.

Una vez instaladas, ejecutar `ng serve --open`. La aplicación se abrirá automáticamente en el navegador.

Independientemente del estado del backend, es posible ejecutar `ng test` para verificar las pruebas unitarias del frontend.

# Evaluación de Exámenes

## Backend

> **Warning**
> El microservicio de evalucion de examenes, tiene una **dependencia directa** con el de gestion de correctores. Por lo que para que el microservicio funcione correctamente, es necesario que el microservicio de correctores esté previamente en ejecución antes de lanzar a ejecutar este.

### Pasos para ejecutar mediante los ficheros jar:
Desde la carpeta del proyecto GestionCorrectores: 
```bash
mvn clean package; java -jar target/ms_corrector-0.0.1-SNAPSHOT.jar
```
Esperar a que se inicie el microservicio por completo.

Desde la carpeta del proyecto EvaluacionExamenes, y en otra terminal: 
```bash 
mvn clean package; java -jar target/ms_evalexamenes-0.0.1-SNAPSHOT.jar
```
De esta forma, estarán funcionando los dos microservicios.
## Frontend

Teniendo ya el backend de ambos microservicios corriendo, situarse en la carpeta *frontend-evalexamenes* y ejecutar `npm install` para instalar las dependencias necesarias.

Una vez instaladas, ejecutar `ng serve --open`. La aplicación se abrirá automáticamente en el navegador.

Independientemente del estado del backend, es posible ejecutar `ng test` para verificar las pruebas unitarias del frontend.
# Documentación
## Authors

- [@M4rdom](https://www.github.com/M4rdom)
- [@MAngelGC](https://www.github.com/MAngelGC)
- [@fcristallocagnoli](https://www.github.com/fcristallocagnoli)
- [@RocioLN15](https://github.com/RocioLN15)
- [@jorgeroma](https://github.com/jorgeroma)

## Comandos de utilidad JPA

Para ejecutar la clase principal de cada microservicio
```bash
mvn exec:java -Dexec.mainClass="sii.ms_corrector.Main"
mvn exec:java -Dexec.mainClass="sii.ms_evalexamenes.Main"
```
## Configuracion Via Web de h2
En caso de usar la base de datos en la web (para poder hacer consultas), disponemos de dos bases de datos disponibles:
- *jdbc:h2:tcp://localhost/./database/ms_evalexamenes*
- *jdbc:h2:tcp://localhost/./database/ms_corrector*