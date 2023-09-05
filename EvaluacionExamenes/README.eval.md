## Backend
> **Warning**
> El microservicio de evalucion de examenes, tiene una **dependencia directa** con el de gestion de correctores. Por lo que para que el microservicio funcione correctamente, es necesario que el microservicio de correctores esté previamente en ejecución antes de lanzar a ejecutar este.

### Ejecución mediante fichero jar
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
### Installation
Teniendo ya el backend corriendo, situarse en la carpeta *frontend-correctores* y ejecutar `npm install` para instalar las dependencias necesarias.

Una vez instaladas, ejecutar `ng serve --open`. La aplicación se abrirá automáticamente en el navegador.

### Running Tests
Independientemente del estado del backend, es posible ejecutar `ng test --no-watch --no-progress --browsers=ChromeHeadless` para verificar las pruebas unitarias del frontend.

## Configuracion Via Web de h2
En caso de querer usar la base de datos en la web (e.j. para poder hacer consultas), la configuración es la siguiente:
- *jdbc:h2:tcp://localhost/./database/ms_evalexamenes*
    - User & Passwd: `sa`