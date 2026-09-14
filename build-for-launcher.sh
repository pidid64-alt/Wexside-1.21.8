#!/bin/bash
set -e
echo "=== WildClient 1.21.8 - Сборка для лаунчера ==="
chmod +x gradlew
./gradlew launcherJar --no-daemon
JAR="build/libs/WildClient-1.21.8.jar"
if [ -f "$JAR" ]; then
  SIZE=$(du -h "$JAR" | cut -f1)
  echo ""
  echo "✓ ГОТОВО: $JAR ($SIZE)"
  echo ""
  echo "Что дальше:"
  echo "1. Установи Fabric Loader 0.19.5 для 1.21.8"
  echo "2. Скачай Fabric API 0.136.1+1.21.8"
  echo "3. Скопируй этот jar в папку mods твоего лаунчера"
  echo ""
  echo "Пример для PrismLauncher:"
  echo "  cp $JAR ~/PrismLauncher/instances/Wild-1.21.8/mods/"
  echo ""
  # Создаем папку launcher-ready для удобства
  mkdir -p launcher-ready
  cp "$JAR" launcher-ready/
  echo "Также скопировано в: launcher-ready/WildClient-1.21.8.jar"
  echo ""
  ls -lh launcher-ready/
else
  echo "ОШИБКА: jar не найден!"
  exit 1
fi
