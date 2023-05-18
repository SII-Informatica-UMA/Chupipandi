echo Iniciando microservicio de gestion de correctores ...
:: set /p variable="Modo de ejecucion (dev: BD en memroria, prod: BD persistente): "
:: if "%variable%"=="prod" (
::     start cmd.exe @cmd /k "java -cp h2-2.1.210.jar org.h2.tools.Server -ifNotExists"
:: )
set bArg=false
set prodArg=false
set testArg=true
for %%a in (%*) do (
    if "%%a"=="prod" set prodArg=true
    if "%%a"=="-b" set bArg=true
    if "%%a"=="-skip" set testArg=false
)

if %testArg%==true (
    CALL mvn test
)

:: start cmd.exe @cmd /k "java -cp h2-2.1.210.jar org.h2.tools.Server -ifNotExists"
if %prodArg%==true (
    set variable=prod
) else (
    set variable=dev
)
if %bArg%==true (
    :: Proceso en segundo plano
    CALL mvn spring-boot:start -Dspring-boot.run.arguments="--spring.profiles.active=%variable%"
) else (
    CALL mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=%variable%"
)

timeout /t 10
:: CALL java -jar target/ms_corrector-0.0.1-SNAPSHOT.jar
PAUSE
