@echo off
echo =======================================
echo Iniciando todos los microservicios BNK
echo =======================================
echo.

:: Cambia esta ruta por la ruta donde están tus carpetas
cd /d "C:\ruta\a\tus\microservicios"

:: Abrir cada proyecto en una nueva ventana de consola
start cmd /k "cd BNK-msaccount && mvn spring-boot:run"
start cmd /k "cd BNK-msclient && mvn spring-boot:run"
start cmd /k "cd BNK-msgateway && mvn spring-boot:run"
start cmd /k "cd BNK-mspayment && mvn spring-boot:run"
start cmd /k "cd BNK-mstransfer && mvn spring-boot:run"
start cmd /k "cd BNK-msloan && .\gradlew bootRun"

echo Todos los microservicios se están iniciando...
pause
