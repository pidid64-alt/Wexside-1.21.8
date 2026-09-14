WildClient 1.21.8 - Запуск через лаунчер

1. Собери jar:
   Linux: ./build-for-launcher.sh
   Windows: build-for-launcher.bat
   Или: ./gradlew remapJar

2. Возьми файл build/libs/WildClient-1.21.8.jar

3. Установи Fabric Loader 0.19.5 для 1.21.8:
   - PrismLauncher: Создай инстанс 1.21.8 Fabric 0.19.5
   - Официальный лаунчер: запусти fabric-installer.jar

4. Скачай Fabric API 0.136.1+1.21.8 с https://modrinth.com/mod/fabric-api

5. Положи в mods/:
   - WildClient-1.21.8.jar
   - fabric-api-0.136.1+1.21.8.jar

6. Запускай через лаунчер!

Baritone и nether-pathfinder уже внутри WildClient jar, отдельно не нужны.
MCEF опционально - если нужен браузер, положи mcef-fabric-2.1.6-1.21.4.jar в mods.

