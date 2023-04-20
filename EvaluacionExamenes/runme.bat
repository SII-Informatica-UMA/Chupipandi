start cmd.exe @cmd /k "java -cp h2-2.1.210.jar org.h2.tools.Server -ifNotExists"
:: CALL mvn clean install -U
start cmd.exe @cmd /k "java -cp h2-2.1.210.jar org.h2.tools.Server -ifNotExists"
CALL mvn clean package
:: start cmd.exe @cmd /k "java -cp h2-2.1.210.jar org.h2.tools.Server "
timeout /t 10
:: CALL mvn exec:java -Dexec.mainClass="es.uma.informatica.jpa.demo.Main"
:: CALL mvn exec:java -Dexec.mainClass="sii.ms_corrector.Main"
::CALL mvn exec:java -Dexec.mainClass="sii.ms_evalexamenes.Main"
CALL java -jar target/ms_evalexamenes-0.0.1-SNAPSHOT.jar
PAUSE
