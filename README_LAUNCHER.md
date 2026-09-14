# WildClient 1.21.8 — Запуск через лаунчер

Теперь мод можно запускать через **любой лаунчер**, а не только через `./gradlew runClient`.

## Что было исправлено

Раньше зависимости (`Java-WebSocket`, `json`, `reflections`, `javassist`, `netty-proxy`, `jlayer` и т.д.) лежали в `src/main/resources/META-INF/jars/` вручную и не прописывались в `fabric.mod.json`. Из-за этого:

- `./gradlew runClient` работал (там `modLocalRuntime`)
- а в лаунчере Fabric Loader не видел эти библиотеки → краш

**Сейчас:**

1. В `build.gradle` все библиотеки помечены как `include(implementation(...))` — Loom сам упакует их в `META-INF/jars/` внутри итогового jar и пропишет секцию `jars` в `fabric.mod.json` (Jar-in-Jar).
2. Локальные моды `baritone` и `nether-pathfinder` тоже упакованы внутрь через `include(files(...))` + `modImplementation`.
3. Удалены старые ручные `META-INF/jars/` и `MANIFEST.MF` из ресурсов.
4. Добавлены удобные таски `buildForLauncher` и `installToMods`.

Итоговый файл: `build/libs/WildClient-1.21.8.jar` — **это уже готовый мод для папки mods**.

---

## Быстрый старт

### 1. Собрать jar

**Linux / macOS:**
```bash
./gradlew buildForLauncher
```

**Windows:**
```bat
gradlew.bat buildForLauncher
```

Результат: `build/libs/WildClient-1.21.8.jar`

Или просто:
```bash
./gradlew remapJar
```

### 2. Подготовить профиль Fabric 1.21.8

Тебе нужен **Fabric Loader 0.19.5** для Minecraft 1.21.8.

#### Вариант A — PrismLauncher (рекомендуется)
1. Создай новый инстанс → версия 1.21.8 → Fabric Loader 0.19.5
2. В настройках инстанса → Mods → Скачай:
   - `Fabric API 0.136.1+1.21.8` с Modrinth
   - (опционально) `MCEF 2.1.6` если хочешь браузер
3. Скопируй `WildClient-1.21.8.jar` в папку `mods/` инстанса
4. Запускай

#### Вариант B — TL Legacy / Legacy Launcher / Официальный лаунчер
1. Скачай `fabric-installer` с https://fabricmc.net/use/
2. Запусти `fabric-installer.jar` → выбери 1.21.8 и Loader 0.19.5 → Install
3. В лаунчере появится профиль `fabric-loader-1.21.8-0.19.5`
4. Открой папку `.minecraft/mods` (или `%appdata%/.minecraft/mods`)
5. Положи туда:
   - `fabric-api-0.136.1+1.21.8.jar` (скачать с Modrinth/CurseForge)
   - `WildClient-1.21.8.jar`
6. Запускай профиль Fabric

#### Вариант C — Modrinth App
1. Создай профиль Fabric 1.21.8
2. Установи Fabric API
3. Перетащи WildClient jar в mods
4. Запускай

### 3. Структура mods для лаунчера

```
mods/
├── WildClient-1.21.8.jar        <- наш мод (внутри уже Baritone + все либы)
├── fabric-api-0.136.1+1.21.8.jar
└── (опционально) mcef-fabric-2.1.6-1.21.4.jar
```

**Важно:** `baritone-1.21.8-named.jar` и `nether-pathfinder` уже ВНУТРИ WildClient jar, отдельно класть не нужно!

Если хочешь MCEF (браузер/ютуб плеер), положи `mcef-fabric-2.1.6-1.21.4.jar` отдельно в mods — он большой и по умолчанию не встроен.

---

## Команды Gradle

```bash
./gradlew remapJar              # собрать jar для лаунчера
./gradlew buildForLauncher      # то же + инструкция в консоли
./gradlew installToMods -PmodsDir=/path/to/mods  # собрать и сразу скопировать
./gradlew runClient             # старый dev-запуск (все еще работает)
./gradlew build                 # полная сборка + sources jar
```

---

## Частые проблемы

**Краш `NoClassDefFoundError: org/java_websocket/...`**
→ Ты используешь старый jar до фикса. Пересобери через `remapJar`. Новый jar должен быть ~8-12 MB (внутри все либы).

**Краш `Baritone not found`**
→ В новом jar Baritone уже внутри. Если все равно краш — проверь что ты не удалил `include(files('libs/baritone...'))` из build.gradle.

**Не запускается в лаунчере, но работает через runClient**
→ Убедись что версия Java 21 (Fabric 1.21.8 требует Java 21). В PrismLauncher: Settings → Java → Java 21.

**MCEF не работает**
→ MCEF требует отдельный мод + нативные либы. Положи `mcef-fabric-2.1.6-1.21.4.jar` в mods и запускай.

---

## Для разработчиков

Если добавляешь новую библиотеку:

```gradle
// В build.gradle
include(implementation('group:artifact:version'))
implementation 'group:artifact:version'
```

Loom сам упакует ее.

Если добавляешь новый локальный мод jar:

```gradle
modImplementation files('libs/your-mod.jar')
modLocalRuntime files('libs/your-mod.jar')
include(files('libs/your-mod.jar'))
```

---

Готово! Теперь можно запускать через любой лаунчер.
