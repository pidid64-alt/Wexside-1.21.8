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
wild_license.py     утилита (stdlib-only)
README.md           этот файл
```

Ключи, тестовые лицензии и `license*.json` в git не попадают (см. `.gitignore`).
