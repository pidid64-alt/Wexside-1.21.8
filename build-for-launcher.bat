@echo off
echo === WildClient 1.21.8 - Сборка для лаунчера ===
call gradlew.bat remapJar --no-daemon
set JAR=build\libs\WildClient-1.21.8.jar
if exist %JAR% (
  echo.
  echo ГОТОВО: %JAR%
  echo.
  echo Что дальше:
  echo 1. Установи Fabric Loader 0.19.5 для 1.21.8
  echo 2. Скачай Fabric API 0.136.1+1.21.8
  echo 3. Скопируй этот jar в папку mods твоего лаунчера
  echo.
  echo Пример: copy %JAR% %%APPDATA%%\.minecraft\mods\
  echo.
  if not exist launcher-ready mkdir launcher-ready
  copy %JAR% launcher-ready\
  echo Также скопировано в: launcher-ready\WildClient-1.21.8.jar
) else (
  echo ОШИБКА: jar не найден!
  exit /b 1
)
pause
