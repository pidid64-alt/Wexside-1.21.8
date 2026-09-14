WildClient 1.21.8 - Запуск через лаунчер

1. Собери jar:
   Linux / Git-Bash: ./build-for-launcher.sh
   Windows:          build-for-launcher.bat
   Или вручную:      ./gradlew launcherJar

2. Возьми файл build/libs/WildClient-1.21.8.jar
   (WildClient-1.21.8-base.jar - это промежуточный, в mods его класть НЕ надо)

3. Установи Fabric Loader 0.19.5 для 1.21.8:
   - PrismLauncher: создай инстанс 1.21.8 -> Fabric 0.19.5
   - Официальный лаунчер: запусти fabric-installer.jar (https://fabricmc.net/use/)

4. Скачай Fabric API 0.136.1+1.21.8 с https://modrinth.com/mod/fabric-api

5. Положи в mods/:
   - WildClient-1.21.8.jar
   - fabric-api-0.136.1+1.21.8.jar

6. Запускай через лаунчер! (Java 21)

Baritone и nether-pathfinder уже внутри WildClient jar - отдельно класть не нужно.
MCEF опционально: положи mcef-fabric-2.1.6-1.21.4.jar в mods
или собери с ./gradlew launcherJar -PincludeMcef=true

Подробности: ../README_LAUNCHER.md
