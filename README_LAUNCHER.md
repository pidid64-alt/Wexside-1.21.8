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

Промежуточный `build/libs/WildClient-1.21.8-base.jar` — это выход `remapJar`
(только наш код и ресурсы). В `mods/` его класть **не нужно**.

Готовые скрипты: `./build-for-launcher.sh` (Linux/macOS/Git-Bash) и `build-for-launcher.bat` (Windows).

---

## Как устроена упаковка (Jar-in-Jar)

Всё упаковывает **один** наш таск `:launcherJar`. Loom'овский `include()` не используется вообще:

| Что | Откуда |
|---|---|
| Java-WebSocket, json, reflections, javassist, netty-codec-socks, netty-handler-proxy, jlayer, mp3spi, tritonus-share | конфигурация `jijLibs` (mavenCentral, `transitive = false`) |
| Baritone (`baritone-meteor`), nether-pathfinder | `libs/*.jar` |
| MCEF (только с `-PincludeMcef=true`) | `libs/*.jar` |

`:launcherJar` берёт `WildClient-1.21.8-base.jar` (это выход `remapJar` — наш код и ресурсы),
кладёт каждый из jar-ов в `META-INF/jars/`, дописывает их в секцию `jars` файла
`fabric.mod.json` и пишет `WildClient-1.21.8.jar`. Попутно:

- у maven-библиотек нет своего `fabric.mod.json`, поэтому он **генерируется**
  (`id`/`version` берутся из настоящих maven-координат, не из имени файла) —
  иначе Fabric Loader такой вложенный jar просто проигнорирует;
- из Baritone **вырезается** его собственный `META-INF/jars/nether-pathfinder-1.4.1.jar`,
  а nether-pathfinder кладётся рядом отдельным JiJ-модом: Fabric Loader читает вложенные
  jar-ы **только одного уровня**, и в `-named` этот jar к тому же не был прописан в `jars`
  → в лаунчере классы `dev.babbaj.pathfinder` не загрузились бы. Заодно минус ~1 МБ дубля;
- из `build/libs/` удаляются устаревшие `WildClient-*.jar` от прошлых конфигураций,
  чтобы случайно не положить в `mods/` старьё.

### Почему не `include(...)`

Loom для Jar-in-Jar требует от зависимости **координаты модуля** (`group:name:version`)
и валидный **semver**. Оба условия здесь не выполняются:

```
> Attempted to nest artifact baritone-1.21.8-named.jar which is not a module
  component and has no capabilities.
```
— у `include(files('libs/....jar'))` координат нет вовсе, таск `:processIncludeJars` падает;

```
(0.3.7.4) is not valid semver for dependency com.googlecode.soundlibs:tritonus-share:0.3.7.4
(1.9.5.4) is not valid semver for dependency com.googlecode.soundlibs:mp3spi:1.9.5.4
(1.0.1.4) is not valid semver for dependency com.googlecode.soundlibs:jlayer:1.0.1.4
(20230618) is not valid semver for dependency org.json:json:20230618
```
— у этих библиотек версии четырехсегментные/календарные, Loom их бракует.

Поэтому `include` убран совсем, а его работу делает `:launcherJar`. Набор библиотек при этом
ровно тот же: `jijLibs` объявлен с `transitive = false`, так что внутрь не попадают
`netty-common`/`netty-buffer` (их и так даёт Minecraft) и `slf4j-api` (его даёт fabric-loader).

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

**Чёрный экран после «Настройки» или «Подключиться»**
→ пересобери `./gradlew launcherJar` и замени `WildClient-1.21.8.jar` в `mods/`.
Исправлен повторный bind временного framebuffer главного меню после отсоединения его
текстуры. При выходе из raw-отрисовки теперь восстанавливается также кэш состояния
Minecraft; фон и статус подключения, фоны загрузки территории и прогресса рисуются
через очередь `DrawContext` (Minecraft 1.21.8), а не прямыми GL-вызовами до GUI-прохода.

Проверка после сборки (нужен запуск игры):

- Главное меню → «Настройки» → настройки графики → «Готово» → главное меню;
  повторить несколько раз. Текст и кнопки должны оставаться видимыми и нажиматься.
- Список серверов → «Подключиться»: видны статус и кнопка отмены; отмена возвращает
  к списку. Проверить также прямое подключение и ошибку подключения к недоступному серверу.
- Успешное подключение: экран загрузки территории сменяется миром без зависшего
  чёрного экрана. Отключиться и снова открыть настройки.
- Повторить после изменения размера окна, переключения F11 и масштаба интерфейса.

Если проблема остаётся, приложи `logs/latest.log` из папки игры. Для подробной
диагностики можно временно добавить JVM-аргумент
`-Dwild.debug.screenRender.enable=true`, воспроизвести проблему и затем убрать аргумент
(он создаёт много записей в логе).

**`Attempted to nest artifact ... is not a module component`**
→ в `build.gradle` снова появился `include(files(...))`. Локальные jar-ы так включать нельзя.

**`... is not valid semver for dependency ...`**
→ снова появился Loom'овский `include(...)` для библиотек с четырехсегментной версией
(`jlayer:1.0.1.4`, `mp3spi:1.9.5.4`, `tritonus-share:0.3.7.4`, `json:20230618`).
Их пакует `:launcherJar`, а не Loom.

**В `build/libs/` несколько jar-ов и непонятно, какой класть в mods**
→ класть нужно только `WildClient-1.21.8.jar`. `:launcherJar` сам удаляет устаревшие
`WildClient-*.jar` (старые сборки со `baritone-1.21.8-SNAPSHOT` внутри в том числе),
но если сомневаешься — снеси `build/libs/` руками и собери заново.

**`java.io.IOException: Stream closed` внутри `:launcherJar`**
→ уже исправлено: чтение jar-ов переведено с `ZipInputStream` на `ZipFile`.
Если видишь это снова — значит ты на старом коммите, сделай `git pull`.

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

**Клиент пишет конфиги в `C:\WildClient` и падает на телефоне / на другом диске**
→ так было раньше: корень клиента был захардкожен. Теперь `C:\WildClient` не используется.
Порядок выбора корня (первый подходящий выигрывает):

```
1. -Dwild.root=...  или переменная окружения WILD_ROOT
2. <папка игры>/WildClient      <- по умолчанию
3. <домашняя папка>/WildClient
4. <temp>/WildClient
```

То есть по умолчанию всё лежит рядом с игрой — `<.minecraft>/WildClient/`, и на телефоне
(Pojav и т.п.) это рабочая, доступная для записи папка. Выбранный путь клиент печатает в лог:

```
[Client] root directory: /storage/emulated/0/.../WildClient
```

Содержимое старого `C:\WildClient` (и `~/WildClient`) один раз переносится в новый корень —
копированием, в фоновом потоке, ничего не удаляется. Признак переноса — файл `.wild-migrated`
в корне клиента.

Задать своё место можно JVM-аргументом в лаунчере:

```
-Dwild.root=D:\Games\WildClientData
```

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

Новая **maven**-библиотека — добавь её в два места (обе строки рядом в `build.gradle`):

```gradle
implementation 'group:artifact:version'   // для компиляции и runClient
jijLibs        'group:artifact:version'   // чтобы попала внутрь итогового jar
```

`jijLibs` объявлена с `transitive = false`, поэтому транзитивные зависимости внутрь не
попадут (так же, как раньше вёл себя Loom'овский `include`). Если транзитивка реально
нужна в рантайме — добавь её отдельной строкой.

Новый **локальный** мод из `libs/`:

```gradle
modImplementation files('libs/your-mod.jar')   // компиляция
modLocalRuntime   files('libs/your-mod.jar')   // runClient
```

и чтобы он попал внутрь итогового jar — добавь строку в блок `toNest` внутри
`tasks.register('launcherJar')`:

```gradle
toNest['your-mod.jar'] = file('libs/your-mod.jar')
```

Если у такого jar-а нет своего `fabric.mod.json`, таск сгенерирует его сам
(id/version возьмёт из имени файла); если есть — возьмёт существующий и только
почистит битые ссылки в секции `jars`.
