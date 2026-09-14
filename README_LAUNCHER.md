# WildClient 1.21.8 — Запуск через лаунчер

Мод можно запускать через **любой лаунчер** (PrismLauncher, TL, Modrinth App, ванильный с
Fabric), а не только через `./gradlew runClient`.

---

## Команды

```bash
./gradlew launcherJar                      # собрать мод для лаунчера
./gradlew buildForLauncher                 # то же + инструкция в консоли
./gradlew installToMods -PmodsDir=/path/to/mods   # собрать и сразу скопировать в mods
./gradlew launcherJar -PincludeMcef=true   # дополнительно вшить MCEF (браузер)
./gradlew runClient                        # dev-запуск (как раньше)
./gradlew build                            # полная сборка (launcherJar запустится сам)
```

Результат: **`build/libs/WildClient-1.21.8.jar`** — это уже готовый мод для папки `mods/`.

Промежуточный `build/libs/WildClient-1.21.8-base.jar` — это то, что делает Loom
(наш код + maven-библиотеки). В `mods/` его класть **не нужно**.

Готовые скрипты: `./build-for-launcher.sh` (Linux/macOS/Git-Bash) и `build-for-launcher.bat` (Windows).

---

## Как устроена упаковка (Jar-in-Jar)

Итоговый jar собирают **два** шага:

| Что | Кто упаковывает | Откуда |
|---|---|---|
| Java-WebSocket, json, reflections, javassist, netty-codec-socks, netty-handler-proxy, jlayer, mp3spi, tritonus-share | Loom, `include(...)` → таск `:processIncludeJars` | mavenCentral |
| Baritone (`baritone-meteor`), nether-pathfinder | таск **`:launcherJar`** (наш) | `libs/*.jar` |

`:launcherJar` берёт `WildClient-1.21.8-base.jar`, кладёт локальные моды в `META-INF/jars/`
и дописывает их в секцию `jars` файла `fabric.mod.json`.

### Почему локальные моды нельзя просто `include(files(...))`

Loom для Jar-in-Jar обязан знать **координаты модуля** (`group:name:version`) — по ним он
формирует имя файла и метаданные. У `files('libs/....jar')` координат нет, поэтому таск
`:processIncludeJars` падал так:

```
> Attempted to nest artifact baritone-1.21.8-named.jar which is not a module
  component and has no capabilities.
```

Отсюда и разделение: maven-зависимости — через `include`, локальные jar-ы — через `:launcherJar`.

### Какой Baritone вшивается и почему

Вшивается **`libs/baritone-1.21.8-named.jar`**, а не `baritone-1.21.8-SNAPSHOT.jar`:

- исходники клиента обращаются к обфусцированным членам этой сборки —
  `Ternary.a / Ternary.b / Ternary.c` и `MovementHelper.a(BlockState)`
  (см. `src/main/java/org/wild/mixin/MovementHelperMixin.java`).
  В `-SNAPSHOT` поля называются `YES/MAYBE/NO` — компиляция с ним не проходит;
- `-named` собран в mojmap, и в проде эти имена совпадают с официальным
  `minecraft-1.21.8.jar`, а его refmap уже переведён в intermediary
  (`net.minecraft.client.multiplayer.ClientChunkCache$Storage → class_631$class_3681`),
  то есть миксины Baritone применяются и в лаунчере;
- `-SNAPSHOT` (intermediary) **нельзя** класть в `runClient`: в dev-окружении классы
  Minecraft имеют yarn-имена, и все миксины Baritone отвалятся.

Дополнительно `:launcherJar` вычищает из Baritone его собственный
`META-INF/jars/nether-pathfinder-1.4.1.jar` и кладёт nether-pathfinder рядом, как отдельный
JiJ-мод: Fabric Loader читает вложенные jar-ы **только одного уровня**, поэтому
nether-pathfinder внутри Baritone в лаунчере просто не загрузился бы
(в `-named` он к тому же не был прописан в `jars`). Побочный плюс — минус ~1 МБ дубля.

---

## Установка в лаунчер

1. Собери jar: `./gradlew launcherJar`
2. Сделай профиль **Minecraft 1.21.8 + Fabric Loader 0.19.5**
   (PrismLauncher: новый инстанс → 1.21.8 → Fabric 0.19.5;
   официальный лаунчер: `fabric-installer.jar` с https://fabricmc.net/use/)
3. Скачай **Fabric API 0.136.1+1.21.8** — https://modrinth.com/mod/fabric-api
4. Положи в `mods/`:

```
mods/
├── WildClient-1.21.8.jar          <- наш мод (Baritone + nether-pathfinder + либы внутри)
├── fabric-api-0.136.1+1.21.8.jar
└── mcef-fabric-2.1.6-1.21.4.jar   <- опционально, только если нужен браузер
```

Baritone и nether-pathfinder **отдельно класть не нужно** — они уже внутри.
MCEF по умолчанию не вшивается (большой + требует нативные либы); если нужен — либо
положи `libs/mcef-fabric-2.1.6-1.21.4.jar` рядом в `mods/`, либо собери с
`-PincludeMcef=true`.

Java должна быть **21**.

---

## Частые проблемы

**`Attempted to nest artifact ... is not a module component`**
→ в `build.gradle` снова появился `include(files(...))`. Локальные jar-ы так включать нельзя,
их пакует `:launcherJar`.

**`NoClassDefFoundError: org/java_websocket/...` / `Baritone not found`**
→ в `mods/` лежит старый jar. Пересобери `./gradlew launcherJar` и проверь, что внутри есть
`META-INF/jars/` (`unzip -l build/libs/WildClient-1.21.8.jar | grep META-INF/jars`).

**В логе сборки warning `Cannot find target for @Overwrite method in baritone.pathing.movement.MovementHelper`**
→ это **нормально**, сборку не ломает. `MovementHelperMixin` целится в обфусцированный
`MovementHelper.a(BlockState)`, которого нет в yarn-маппингах, поэтому Loom не может его
переименовать и оставляет как есть. Именно в таком виде он и попадает в прод, где Baritone
тоже лежит в mojmap — то есть в лаунчере миксин применяется.

**Краш на миксине Baritone / `ClassNotFoundException: baritone...`**
→ в `libs/` подменили сборку Baritone. Нужен именно `baritone-1.21.8-named.jar`
(см. раздел «Какой Baritone вшивается»).

**Работает в `runClient`, но не в лаунчере**
→ проверь Java 21, Fabric Loader 0.19.5, наличие Fabric API и что в `mods/` нет
второго (отдельного) Baritone — будет конфликт по id `baritone-meteor`.

**Диск C: забит (0 байт свободно)**
→ Gradle, Loom и все кэши живут в `C:\Users\<ты>\.gradle`. При нехватке места Loom портит
свой кэш (`ACQUIRED_PREVIOUS_OWNER_DISOWNED`, `class file for net.minecraft.class_2338 not
found`). Освободи место, потом `./gradlew --stop` и удали `.gradle/caches/fabric-loom`.
Перенести кэш на другой диск можно так:

```
GRADLE_USER_HOME=D:\gradle-home
```

(в Git-Bash: `export GRADLE_USER_HOME=/d/gradle-home` перед `./gradlew`).

---

## Для разработчиков

Новая **maven**-библиотека (упакуется автоматически):

```gradle
include(implementation('group:artifact:version'))
```

Новый **локальный** мод из `libs/` (для компиляции + runClient):

```gradle
modImplementation files('libs/your-mod.jar')
modLocalRuntime  files('libs/your-mod.jar')
```

и чтобы он попал внутрь итогового jar — добавь его в список `nestedLocalMods` в `build.gradle`:

```gradle
def nestedLocalMods = [
    [file: file('libs/baritone-1.21.8-named.jar'),   entry: 'baritone-1.21.8-named.jar'],
    [file: file('libs/nether-pathfinder-1.4.1.jar'), entry: 'nether-pathfinder-1.4.1.jar'],
    [file: file('libs/your-mod.jar'),                entry: 'your-mod.jar'],   // <- вот так
]
```
