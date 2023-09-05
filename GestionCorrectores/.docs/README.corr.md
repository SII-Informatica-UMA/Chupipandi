## Backend
### Ejecución mediante fichero jar

Para poner en marcha el backend
```bash
# Build project (es posible omitir los tests)
mvn clean package [-Dmaven.test.skip]
# Execute service
java -jar target/ms_corrector-0.0.1-SNAPSHOT.jar
```
A la hora de ejecutar el servicio, existen dos perfiles posibles de uso
```bash
# Perfil *dev*
# Ejecución con la base de datos en memoria
java -jar -Dspring.profiles.active=dev target/ms_corrector-0.0.1-SNAPSHOT.jar
# Perfil *prod*
# Ejecución con persistencia (base de datos almacenada en carpeta database):
java -jar -Dspring.profiles.active=prod target/ms_corrector-0.0.1-SNAPSHOT.jar
```
> **Note**
> De no especificar el perfil de ejecución, se ejecutará por defecto en memoria (perfil `dev`).
- - -
### Ejecucion mediante mvn spring-boot
Sencilla ejecución con `mvn spring-boot`, ventaja de que por defecto se salta los tests
```bash
mvn spring-boot:run [-Dspring-boot.run.arguments="--spring.profiles.active={dev,prod}"]
```
- - -
### Ejecución mediante archivo por lotes (Windows)

El archivo por lotes `start.bat` hace la misma función que la ejecución mediante el fichero jar, pero de una forma más sencilla y automatizada. Para ejecutarlo, simplemente ejecutar `./start.bat` en la terminal.

Parámetros disponibles para el ejecutable:
- `-b`: ejecuta la operación en segundo plano
- `prod`: ejecuta la operación en modo producción (activa spring profile `prod`) (con persistencia de datos)
- `-skip`: para saltarse la fase de comprobación de tests

> **Note**
> Si no se le especifica el modo de ejecución, el perfil por defecto es `dev` (en memoria)

## Frontend
### Installation
Teniendo ya el backend corriendo, situarse en la carpeta *frontend-correctores* y ejecutar `npm install` para instalar las dependencias necesarias.

Una vez instaladas, ejecutar `ng serve --open`. La aplicación se abrirá automáticamente en el navegador.

### Running Tests
Independientemente del estado del backend, es posible ejecutar `ng test --no-watch --no-progress --browsers=ChromeHeadless` para verificar las pruebas unitarias del frontend.

## Configuracion Via Web de h2
En caso de querer usar la base de datos en la web (e.j. para poder hacer consultas), la configuración es la siguiente:
- *jdbc:h2:tcp://localhost/./database/ms_corrector*
    - User & Passwd: `sa`