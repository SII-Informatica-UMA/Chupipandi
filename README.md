
# Proyecto Microservicios

Implementación de los microservicios de Gestión de Correctores y Evaluación de Exámenes para la asignatura de Sistemas de Información para Internet.
- GestionCorrectores: puede ejecutarse independientemente del otro microservicio.
- EvaluacionExamenes: depende del microservicio de correctores para su correcto funcionamiento.
### Ejecución global de los microservicios
Para probar ambos microservicios a la vez, puede ejecutarse directamente el archivo `runme.bat` que se encuentra en la raíz del proyecto.
# Gestión de Correctores
## Backend
### Ejecución mediante fichero jar:

En primer lugar ejecutamos `mvn clean package`.

Seguidamente, ejecutamos el fichero jar generado en la carpeta target, donde disntinguimos dos tipos de ejecución
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