
echo Iniciando microservicio de gestion de correctores ...

:: De momento solo consigo que funcione si se ejecuta todo en el mismo CALL. Ademas, siempre ejecuta los tests
CALL mvn clean test spring-boot:start -f ./GestionCorrectores/pom.xml && mvn clean test spring-boot:start -f ./EvaluacionExamenes/pom.xml && echo Pulse una tecla para finalizar ambos microservicios && PAUSE && echo Deteniendo microservicio gestion de correctores ... && mvn spring-boot:stop -f ./GestionCorrectores/pom.xml && echo Deteniendo microservicio evaluacion de examenes ... && mvn spring-boot:stop -f ./EvaluacionExamenes/pom.xml