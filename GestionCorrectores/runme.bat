CALL mvn clean test
:: set /p variable="Modo de ejecucion (dev: BD en memroria, prod: BD persistente): "
:: if "%variable%"=="prod" (
::     start cmd.exe @cmd /k "java -cp h2-2.1.210.jar org.h2.tools.Server -ifNotExists"
:: )
if "%1"=="prod" (
    set variable=%1
    start cmd.exe @cmd /k "java -cp h2-2.1.210.jar org.h2.tools.Server -ifNotExists"
) else (
    set variable=dev
)
if "%2"=="-b" (
    :: Proceso en segundo plano
    CALL mvn spring-boot:start -Dspring-boot.run.arguments="--spring.profiles.active=%variable%"
) else (
    CALL mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=%variable%"
)

timeout /t 10
:: CALL java -jar target/ms_corrector-0.0.1-SNAPSHOT.jar
PAUSE
