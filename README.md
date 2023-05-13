
# Sistemas Información para Internet

Implementación microservicios de Gestión de Correctores y Evaluación de Exámenes.
## Necesario para el microservicio de evaluación de exámenes
Para probar este microservicio, es necesario tener activo el microservicio de gestión de correctores, ya que un método del primero depende del segundo:
1. Desde la carpeta del proyecto GestionCorrectores: 
```bash
mvn clean package; java -jar target/ms_corrector-0.0.1-SNAPSHOT.jar
```
2. Esperar a que se inicie el microservicio por completo.
3. Desde la carpeta del proyecto EvaluacionExamenes: 
```bash 
mvn clean package; java -jar target/ms_evalexamenes-0.0.1-SNAPSHOT.jar
```
De esta forma, estarán funcionando los dos microservicios.
- - -
## Microservicio de Gestión de Correctores
### Ejecutar mediante archivo `.jar`

> **Note**
> Se presupone para tanto para la ejecución de cualquiera de los siguientes comandos, se está en la raíz del proyecto GestionCorrectores.
1. Ejecutar `mvn clean package`
2. Disntinguimos dos tipos de ejecución
    - Ejecución en memoria (por defecto si no se especifica nada):
    ```java
    java -jar -Dspring.profiles.active=dev target/ms_corrector-0.0.1-SNAPSHOT.jar
    ```
    - Ejecución con persistencia:
    ```java
    java -jar -Dspring.profiles.active=prod target/ms_corrector-0.0.1-SNAPSHOT.jar
    ```

### Ejecutar mediante `start.bat` y `stop.bat`
1. Ejecutar `./start.bat`. Parámetros disponibles:
    - `-b`: ejecuta la operación en segundo plano
    - `prod`: ejecuta la operación en modo producción (activa spring profile `prod`) (con persistencia de datos)
    - `-skip`: para saltarse la fase de comprobación de tests
> **Note**
> Si no se le especifica el modo de ejecución, el perfil por defecto es `dev` (en memoria)
2. Si se ha ejecutado en segundo plano, para parar el microservicio ejecutar `./stop.bat`, en caso contrario, simplemente mandar una señal para matar proceso (`Ctrl + C`).
- - -
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

Para levantar la base de datos h2:
- Copiar archivo jar en directorio de trabajo
```bash
# Ruta del jar: "~/.m2/repository/com/h2database/h2/2.1.210/h2-2.1.210.jar"
```
## Configuracion Via Web de h2
En caso de usar la base de datos en la web (para poder hacer consultas), disponemos de dos bases de datos disponibles:
- *jdbc:h2:tcp://localhost/./database/ms_evalexamenes*
- *jdbc:h2:tcp://localhost/./database/ms_corrector*