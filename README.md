
# Sistemas Información para Internet

Implementación microservicios de Gestión de Correctores y Evaluación de Exámenes.

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
- Ejecuta por consola de comandos de Windows el archivo por lotes para cada servicio:
```cmd
runme.bat
```
## Configuracion Via Web de h2
Dos bases de datos disponibles:
- *jdbc:h2:tcp://localhost/./database/ms_evalexamenes*
- *jdbc:h2:tcp://localhost/./database/ms_corrector*

## Necesario para el microservicio de evaluación de exámenes
Para probar este microservicio, es necesario tener activo el microservicio de gestión de correctores, ya que un método del primero depende del segundo:
1. Desde la carpeta del proyecto: 
```bash
cd GestionCorrectores; mvn clean package; java -jar target/ms_corrector-0.0.1-SNAPSHOT.jar
```
3. Esperar a que se inicie el microservicio por completo.
4. Desde la carpeta del proyecto: 
```bash 
cd EvaluacionExamenes; mvn clean package; java -jar target/ms_evalexamenes-0.0.1-SNAPSHOT.jar
```
5. De esta forma, estarán funcionando los dos microservicios.
