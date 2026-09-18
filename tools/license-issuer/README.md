# Выпуск лицензий WildClient (сторона владельца)

Утилита формирует `license.json` ровно в том формате, который читает
`ru.wild.security.LicenseVerifier` в клиенте: подписанный Ed25519 payload
с датой окончания и привязкой к железу.

**Она ничего не патчит в клиенте** — только подписывает данные приватным ключом.

---

## Требования

* Python 3.8+ (только стандартная библиотека — `openssl` не нужен, работает и на Windows).
* **Приватный ключ владельца** (32-байтовый Ed25519 seed), пара к публичному ключу,
  вшитому в клиент:

  ```
  82abbd84eaf3e0940a9768b34259e98fe77c8e44fdf3c2d57d87d73ef2b2b766
  ```

Без этого ключа утилита не даёт ничего: Ed25519-подпись нельзя подделать,
а публичный ключ в клиенте зашит в код. Это проверено на практике — см.
«Проверка» ниже.

> Приватный ключ читается локально из файла и никуда не передаётся.
> Не вставляйте его в чаты, тикеты и не коммитьте в репозиторий.

---

## Быстрый старт

```bash
# самопроверка реализации Ed25519 (векторы RFC 8032) — должна быть «всё в порядке»
python3 wild_license.py selftest

# HWID конкретной машины (запускать у пользователя, который просит лицензию)
python3 wild_license.py hwid

# выпустить лицензию на 30 дней
python3 wild_license.py issue --key owner-private.pem --hwid <hwidHash> --days 30 --out license.json

# или до конкретной даты
python3 wild_license.py issue --key owner-private.pem --hwid <hwidHash> --until 2026-12-31 --out license.json

# проверить готовый файл
python3 wild_license.py verify --file license.json
```

`--hwid` принимает либо готовый `hwidHash` (64 hex), либо сырой отпечаток
(`UUID|ДИСК|ПЛАТА|CPU|HOSTNAME`) — тогда хэш считается автоматически.

---

## Пакетная выдача (`issue-batch`)

Для списка клиентов — одна команда, на выходе папка с лицензиями, CSV-отчёт и опционально zip.

```bash
python3 wild_license.py issue-batch --key owner-private.pem --input clients.csv \
        --days 30 --out-dir licenses --zip bundle.zip
```

`clients.csv` (заголовок опционален; `#` — комментарий; разделитель `,`, `;` или таб):

```csv
hwid,label,days
aaaa1111…1111,Иван,30
bbbb2222…2222,Пётр,90
ccc3333…3333,Мария,2026-12-31
```

* колонки распознаются по именам: `hwid` (обязательна), `label`, `days`, `until`;
* без заголовка порядок фиксированный: `hwid[,label[,days]]`;
* в колонке `days` можно писать дату (`2026-12-31`) — утилита поймёт;
* срок из строки перекрывает `--days`/`--until` по умолчанию;
* дубликаты HWID, пустые и битые строки не роняют процесс: попадают в отчёт со
  статусом `duplicate`/`error`, остальные лицензии выдаются;
* `--fail-on-error` заставит вернуть код 1, если были проблемные строки (удобно в CI/скриптах);
* в конце печатается проверка: примет ли выпущенную лицензию текущая сборка клиента.

Результат: `licenses/0001_Иван_aaaa1111aaaa.license.json`, `licenses/report.csv`, `bundle.zip`.

---

## Сервис выдачи и продления (`license_service.py`)

Держит приватный ключ владельца и подписывает лицензии по HTTP. Предназначен для
запуска **у владельца** (локально или на его сервере), а не у клиентов.

```bash
python3 license_service.py --key owner-private.pem --allowlist allowlist.json \
        --token "$WILD_LICENSE_ADMIN_TOKEN"
# WildLicense service → http://127.0.0.1:8787
```

### Endpoints

| Метод | Путь | Кто | Что делает |
|---|---|---|---|
| `GET` | `/healthz` | все | статус, ключ подписи, размер allowlist (секретов нет) |
| `GET` | `/` | все | человекочитаемая страница статуса |
| `GET` | `/v1/license?hwid=<hex>` | админ | готовая лицензия для HWID |
| `POST` | `/v1/issue` | админ | `{"hwid":"…","days":30}` или `{"hwid":"…","until":"2026-12-31"}` |
| `POST` | `/v1/renew` | клиент из allowlist | `{"hwid":"…"}` — продление на срок из allowlist |

Ответ содержит саму лицензию:

```json
{
  "hwidHash": "…", "validUntil": 1798675200000, "admin": true,
  "acceptedByClient": true,
  "license": { "payload": "…", "signature": "…" }
}
```

Токен: `Authorization: Bearer <токен>` или `X-Admin-Token: <токен>`
(также берётся из env `WILD_LICENSE_ADMIN_TOKEN` или `--token-file`).

### Модель прав

* `/v1/renew` **не создаёт права**: если HWID нет в allowlist — `403 not_allowed`,
  если срок в allowlist истёк — `403 subscription_expired`.
  Сервис только подтверждает то, что владелец уже разрешил;
* `/v1/issue` и `/v1/license` требуют админ-токен — это инструмент владельца,
  а не клиентский endpoint;
* `--no-renew` полностью выключает самообслуживание, оставляя только админскую выдачу;
* `--max-days` (по умолчанию 400) ограничивает срок, который сервис выдаст даже с админ-токеном.

### Безопасность по умолчанию

* слушает **только `127.0.0.1`**; наружу — лишь явным `--host`, желательно с
  `--tls-cert/--tls-key` (при не-loopback адресе сервис печатает предупреждение);
* не стартует без админ-токена короче 16 символов;
* лимит тела запроса 64 КБ и лимит частоты обращений с одного IP (`--rate-limit`, `--rate-window`);
* токен сравнивается в постоянном времени (`hmac.compare_digest`), в логи не попадают
  ни токены, ни тела запросов;
* при старте сверяет свой публичный ключ с вшитым в клиент и предупреждает,
  если подписи этой сборкой приняты не будут.

### Allowlist

```bash
python3 wild_license.py allowlist --file allowlist.json add \
        --hwid <hash> --label "Иван" --days 90
python3 wild_license.py allowlist --file allowlist.json add \
        --hwid <hash> --label "Мария" --until 2026-12-31
python3 wild_license.py allowlist --file allowlist.json list
python3 wild_license.py allowlist --file allowlist.json remove --hwid <hash>
```

Формат смотрите в `allowlist.example.json`. Сервис перечитывает файл при изменении
(по mtime), поэтому перезапуск не нужен. Реальные `allowlist.json` содержат HWID
клиентов и в git не коммитятся.

### Пример: клиент забирает продление

```bash
curl -s -X POST https://license.example.com/v1/renew \
     -H 'Content-Type: application/json' \
     -d "{\"hwid\":\"$(python3 wild_license.py hwid | head -1 | awk '{print $NF}')\"}" \
  | python3 -c "import json,sys; print(json.load(sys.stdin)['license'])" > license.json
```

Для продакшена сервис стоит держать за reverse-proxy с TLS и лимитами
(nginx: `limit_req`, `client_max_body_size 64k`) либо под systemd:

```ini
[Unit]
Description=WildClient license service
After=network-online.target

[Service]
WorkingDirectory=/opt/wild-license
EnvironmentFile=/opt/wild-license/service.env   # WILD_LICENSE_ADMIN_TOKEN=…
ExecStart=/usr/bin/python3 license_service.py --key /opt/wild-license/owner-private.pem \
          --allowlist /opt/wild-license/allowlist.json --host 127.0.0.1 --port 8787
Restart=on-failure
User=wildlicense

[Install]
WantedBy=multi-user.target
```

---

## Формат

`license.json`:

```json
{
  "payload": "<base64url от UTF-8 JSON>",
  "signature": "<base64url от 64-байтной Ed25519-подписи payload>"
}
```

Раскодированный `payload`:

```json
{"validUntil": 1798675200000, "hwidHash": "0123...cdef"}
```

* `validUntil` — epoch-**milliseconds**, должен быть больше текущего доверенного времени
  (`TrustedTimeProvider`, NTP `time.windows.com`); иначе `LicenseVerifier` вернёт `false`;
* `hwidHash` — SHA-256 (hex) отпечатка железа, сравнение идёт со значением
  `HardwareFingerprint.process()` **или** `HardwareFingerprint.compute()`
  (обе схемы принимаются, регистр не важен);
* любые дополнительные поля через `--claim key=value` в payload попадут,
  но проверяются только два перечисленных.

### Куда положить файл

`LicenseVerifier` ищет лицензию в таком порядке:

1. `-Dwild.license.path=<путь>` (JVM-аргумент);
2. переменная окружения `WILD_LICENSE_PATH`;
3. `%APPDATA%/WildClient/license.json`;
4. `~/.wildclient/license.json`.

---

## HWID

`hwid` считает то же, что и клиент:

| Метод клиента | Что берётся |
|---|---|
| `HardwareFingerprint.process()` | `UUID(SMBIOS) + "\|" + HOSTNAME` |
| `HardwareFingerprint.compute()` | `UUID + "\|" + DISK + "\|" + BOARD + "\|" + CPU + "\|" + HOSTNAME` |

Далее — `sha256` от этой строки. На Windows поля читаются через `wmic`
(`csproduct UUID`, `diskdrive SerialNumber`, `baseboard SerialNumber`, `cpu ProcessorId`),
имя хоста — из `COMPUTERNAME`/`HOSTNAME`.

Вне Windows железные поля дадут `UNKNOWN`, поэтому HWID пользователя нужно
снимать на его машине, а не на сервере.

---

## Проверка работоспособности (что уже протестировано)

```bash
# 0. Автотесты: 16 тестов, включая негативные (без токена, чужой HWID, лимиты)
cd tools/license-issuer && python3 -m unittest discover -s tests -v

# 1. Реализация Ed25519 совпадает с RFC 8032
python3 wild_license.py selftest

# 2. Совместимость с OpenSSL (та же схема, что у Java Signature("Ed25519")) — в две стороны:
openssl genpkey -algorithm ed25519 -out ossl.key
openssl pkey -in ossl.key -pubout -out ossl.pub
openssl pkeyutl -sign -inkey ossl.key -rawin -in payload.bin -out ossl.sig   # проверяется wild_license.verify
openssl pkeyutl -verify -pubin -inkey ossl.pub -rawin -in payload.bin -sigfile mine.sig

# 3. Лицензия с ТЕСТОВЫМ ключом:
python3 wild_license.py genkey --out-dir keys
python3 wild_license.py issue --key keys/test-private.pem --hwid <hex> --days 30 --out license-test.json
# issue честно пишет: «подпись от ключа клиента: НЕТ — клиент отклонит»
python3 wild_license.py verify --file license-test.json    # подпись НЕВАЛИДНА (ключ клиента)
python3 wild_license.py verify --file license-test.json --pubkey keys/test-public.pem  # ВАЛИДНА (свой ключ)
```

---

## Ограничение, о котором важно знать

Лицензия и предохранитель `ru.wild.security.AccessGuard` — **разные механизмы**.
`AccessGuard` сравнивает доверенное время с константой, зашитой в конкретную сборку
(`1788525348375`), с grace-окном `wild.guard.localExpiryGraceMs` (по умолчанию 24 ч),
и при срабатывании завершает процесс через `EventDispatchBoundary` (`Runtime.halt(0)`).
`LicenseVerifier`/`license.json` в этой проверке не участвуют.

Поэтому выпуск и продление лицензий **не влияют** на окно guard-константы:
это сборочный параметр, а не свойство лицензии. Для клиентов, которые
собираются из ваших собственных исходников, окно задаётся при сборке релиза.

## Файлы

```
wild_license.py            CLI: selftest / genkey / issue / issue-batch / allowlist / verify / hwid
license_service.py         HTTP-сервис выдачи и продления (держит приватный ключ)
allowlist.example.json     шаблон allowlist для сервиса
tests/test_license_tools.py  автотесты (stdlib unittest, сеть — только localhost)
README.md                  этот файл
```

Приватные ключи, реальные `allowlist.json`, выданные лицензии и `licenses/`
в git не попадают (см. `.gitignore` в этом каталоге).
