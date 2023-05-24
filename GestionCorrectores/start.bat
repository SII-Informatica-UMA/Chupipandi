echo Iniciando microservicio de gestion de correctores ...

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

if %prodArg%==true (
    set variable=prod
) else (
    set variable=dev
)
if %bArg%==true (
    :: Proceso en segundo plano
    CALL mvn spring-boot:start -Dspring-boot.run.arguments="--spring.profiles.active=%variable%"
    echo Microservicio activo. Pulse una tecla para finalizarlo.
    PAUSE
    echo Deteniendo microservicio gestion de correctores ...
    CALL mvn spring-boot:stop
) else (
    CALL mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=%variable%"
)