:: Iniciar el servicio Gestion de correctores
CALL mvn -f ./GestionCorrectores/pom.xml -Dmaven.test.skip=true clean package 
timeout /t 3
start cmd.exe @cmd /k "java -jar GestionCorrectores/target/ms_corrector-0.0.1-SNAPSHOT.jar"

:: Iniciar el servicio Evaluacion de examenes
CALL mvn -f ./EvaluacionExamenes/pom.xml -Dmaven.test.skip=true clean package
timeout /t 10
start cmd.exe @cmd /k "java -jar EvaluacionExamenes/target/ms_evalexamenes-0.0.1-SNAPSHOT.jar"

PAUSE
