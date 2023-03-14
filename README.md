
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
- O bien utilizar la ruta completa
```bash
# Ruta del jar: "~\.m2\repository\com\h2database\h2\2.1.210\h2-2.1.210.jar"
java -cp h2-2.1.210.jar org.h2.tools.Server -ifNotExists
```

Ejecutar el archivo Por lotes Para cada Servicio:
```bash
Ejecuta runme.bat
```
