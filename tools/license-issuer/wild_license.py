#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
WildClient — утилита владельца для выпуска и проверки license.json.

Назначение: подписать payload (Ed25519) тем приватным ключом, пара которого
вшита в клиент (`LicenseVerifier` → `-----BEGIN PUBLIC KEY----- MCowBQYDK2VwAyEA...`).

ВАЖНО
  * Без НАСТОЯЩЕГО приватного ключа владельца утилита бесполезна: Ed25519
    подпись проверить иначе нельзя, а публичный ключ клиента подделать нельзя.
  * Утилита ничего не патчит в клиенте: она только формирует файл лицензии
    ровно в том формате, который ожидает `ru.wild.security.LicenseVerifier`.
  * Приватный ключ читается из файла локально и никуда не отправляется.
    Не вставляйте приватный ключ в переписку/чат.

Формат license.json (то, что читает клиент):
    {
      "payload":   "<base64url от UTF-8 JSON>",
      "signature": "<base64url от 64-байтной Ed25519-подписи payload>"
    }
  payload JSON:
    {
      "validUntil": <epoch-millis>,   # обязателен, должен быть > текущего времени
      "hwidHash":   "<sha256 hex>"    # обязателен, см. HardwareFingerprint
    }

Куда класть файл (порядок поиска в LicenseVerifier):
    -Dwild.license.path=<путь>  →  $WILD_LICENSE_PATH  →  %APPDATA%/WildClient/license.json
    →  ~/.wildclient/license.json

Команды:
    selftest                                        проверить реализацию Ed25519 (RFC 8032)
    genkey --out-dir DIR                            сгенерировать тестовую пару ключей
    issue --key priv.pem --hwid <значение> ...      выпустить license.json
    verify --file license.json [--pubkey ...]       проверить подпись/срок/hwid
    hwid                                            посчитать HWID-хэш этой машины

Примеры:
    python3 wild_license.py issue --key owner.key --hwid <hex> --days 30 --out license.json
    python3 wild_license.py issue --key owner.key --hwid <hex> --until 2026-12-31
    python3 wild_license.py verify --file license.json
"""

from __future__ import annotations

import argparse
import base64
import csv
import datetime as _dt
import hashlib
import json
import os
import platform
import re
import subprocess
import sys
import time
import zipfile

# ---------------------------------------------------------------------------
# Публичный ключ, вшитый в клиент (ru.wild.security.BuildSignature / LicenseVerifier)
# ---------------------------------------------------------------------------
CLIENT_PUBLIC_KEY_PEM = (
    "-----BEGIN PUBLIC KEY-----\n"
    "MCowBQYDK2VwAyEAgqu9hOrz4JQKl2izQlnpj+d8jkT988LVfYfXPvKyt2Y=\n"
    "-----END PUBLIC KEY-----\n"
)
CLIENT_PUBLIC_KEY_HEX = "82abbd84eaf3e0940a9768b34259e98fe77c8e44fdf3c2d57d87d73ef2b2b766"

PKCS8_ED25519_PREFIX = bytes.fromhex("302e020100300506032b657004220420")
SPKI_ED25519_PREFIX = bytes.fromhex("302a300506032b6570032100")

DEFAULT_GRACE_MS = 24 * 60 * 60 * 1000  # wild.guard.localExpiryGraceMs


# ---------------------------------------------------------------------------
# Ed25519 (RFC 8032), чистая реализация на stdlib — чтобы не требовать openssl
# ---------------------------------------------------------------------------
_P = 2**255 - 19
_L = 2**252 + 27742317777372353535851937790883648493
_D = (-121665 * pow(121666, _P - 2, _P)) % _P
_I = pow(2, (_P - 1) // 4, _P)


def _sha512(data: bytes) -> bytes:
    return hashlib.sha512(data).digest()


def _xrecover(y: int) -> int:
    xx = (y * y - 1) * pow(_D * y * y + 1, _P - 2, _P) % _P
    x = pow(xx, (_P + 3) // 8, _P)
    if (x * x - xx) % _P != 0:
        x = x * _I % _P
    if (x * x - xx) % _P != 0:
        raise ValueError("точка не лежит на кривой Ed25519")
    if x % 2 != 0:
        x = _P - x
    return x


_BY = (4 * pow(5, _P - 2, _P)) % _P
_BX = _xrecover(_BY)
_B = (_BX % _P, _BY % _P, 1, (_BX * _BY) % _P)  # extended: (X, Y, Z, T)
_IDENTITY = (0, 1, 1, 0)


def _add(p, q):
    x1, y1, z1, t1 = p
    x2, y2, z2, t2 = q
    a = (y1 - x1) * (y2 - x2) % _P
    b = (y1 + x1) * (y2 + x2) % _P
    c = t1 * 2 * _D * t2 % _P
    d = z1 * 2 * z2 % _P
    e = b - a
    f = d - c
    g = d + c
    h = b + a
    return (e * f % _P, g * h % _P, f * g % _P, e * h % _P)


def _double(p):
    x1, y1, z1, _ = p
    a = x1 * x1 % _P
    b = y1 * y1 % _P
    c = 2 * z1 * z1 % _P
    d = -a % _P
    e = ((x1 + y1) ** 2 - a - b) % _P
    g = (d + b) % _P
    f = (g - c) % _P
    h = (d - b) % _P
    return (e * f % _P, g * h % _P, f * g % _P, e * h % _P)


def _scalar_mul(point, scalar: int):
    result = _IDENTITY
    addend = point
    while scalar > 0:
        if scalar & 1:
            result = _add(result, addend)
        addend = _double(addend)
        scalar >>= 1
    return result


def _encode_point(point) -> bytes:
    x, y, z, _ = point
    zi = pow(z, _P - 2, _P)
    x = x * zi % _P
    y = y * zi % _P
    return (y | ((x & 1) << 255)).to_bytes(32, "little")


def _decode_point(data: bytes):
    if len(data) != 32:
        raise ValueError("точка должна быть 32 байта")
    raw = int.from_bytes(data, "little")
    sign = raw >> 255
    y = raw & ((1 << 255) - 1)
    if y >= _P:
        raise ValueError("некорректная координата y")
    x = _xrecover(y)
    if (x & 1) != sign:
        x = _P - x
    return (x, y, 1, x * y % _P)


def _secret_scalar(seed: bytes) -> tuple[int, bytes]:
    h = _sha512(seed)
    a = int.from_bytes(h[:32], "little")
    a &= (1 << 254) - 8
    a |= 1 << 254
    return a, h[32:]


def public_key(seed: bytes) -> bytes:
    if len(seed) != 32:
        raise ValueError("seed должен быть 32 байта")
    a, _ = _secret_scalar(seed)
    return _encode_point(_scalar_mul(_B, a))


def sign(message: bytes, seed: bytes) -> bytes:
    if len(seed) != 32:
        raise ValueError("seed должен быть 32 байта")
    a, prefix = _secret_scalar(seed)
    pk = public_key(seed)
    r = int.from_bytes(_sha512(prefix + message), "little") % _L
    r_encoded = _encode_point(_scalar_mul(_B, r))
    k = int.from_bytes(_sha512(r_encoded + pk + message), "little") % _L
    s = (r + k * a) % _L
    return r_encoded + s.to_bytes(32, "little")


def verify(signature: bytes, message: bytes, pk: bytes) -> bool:
    if len(signature) != 64 or len(pk) != 32:
        return False
    try:
        r_point = _decode_point(signature[:32])
        a_point = _decode_point(pk)
    except ValueError:
        return False
    s = int.from_bytes(signature[32:], "little")
    if s >= _L:
        return False
    k = int.from_bytes(_sha512(signature[:32] + pk + message), "little") % _L
    return _encode_point(_scalar_mul(_B, s)) == _encode_point(_add(r_point, _scalar_mul(a_point, k)))


# ---------------------------------------------------------------------------
# PEM / base64url
# ---------------------------------------------------------------------------
def _b64url(data: bytes) -> str:
    return base64.urlsafe_b64encode(data).decode("ascii")


def _unb64url(text: str) -> bytes:
    return base64.urlsafe_b64decode(text.strip() + "=" * (-len(text.strip()) % 4))


def _pem(kind: str, der: bytes) -> str:
    body = base64.b64encode(der).decode("ascii")
    lines = [body[i : i + 64] for i in range(0, len(body), 64)]
    return f"-----BEGIN {kind}-----\n" + "\n".join(lines) + f"\n-----END {kind}-----\n"


def _pem_body(text: str) -> bytes:
    body = re.sub(r"-----[^-]+-----", "", text)
    return base64.b64decode(re.sub(r"\s+", "", body))


def seed_to_pkcs8_pem(seed: bytes) -> str:
    return _pem("PRIVATE KEY", PKCS8_ED25519_PREFIX + seed)


def pub_to_spki_pem(pk: bytes) -> str:
    return _pem("PUBLIC KEY", SPKI_ED25519_PREFIX + pk)


def load_seed(path: str) -> bytes:
    """Принимает PKCS#8 PEM, SPKI PEM, сырые 32 байта, hex или base64."""
    with open(path, "rb") as handle:
        raw = handle.read()
    text = raw.decode("utf-8", "ignore")
    if "BEGIN" in text:
        der = _pem_body(text)
        return der[-32:]
    if len(raw) == 32:
        return raw
    text = text.strip()
    if re.fullmatch(r"[0-9a-fA-F]{64}", text):
        return bytes.fromhex(text)
    decoded = base64.b64decode(text, validate=False)
    if len(decoded) == 32:
        return decoded
    raise SystemExit(f"не удалось разобрать приватный ключ: {path} (нужно 32-байтовое Ed25519 seed)")


def load_public_key(value: str) -> bytes:
    if value is None:
        return bytes.fromhex(CLIENT_PUBLIC_KEY_HEX)
    if os.path.exists(value):
        with open(value, "rb") as handle:
            value = handle.read().decode("utf-8", "ignore")
    value = value.strip()
    if "BEGIN" in value:
        der = _pem_body(value)
        return der[-32:]
    if re.fullmatch(r"[0-9a-fA-F]{64}", value):
        return bytes.fromhex(value)
    decoded = base64.b64decode(value, validate=False)
    if len(decoded) == 32:
        return decoded
    raise SystemExit("не удалось разобрать публичный ключ (нужен SPKI PEM или 64 hex)")


# ---------------------------------------------------------------------------
# HWID — повторяет ru.wild.security.HardwareFingerprint
# ---------------------------------------------------------------------------
def _sha256_hex(text: str) -> str:
    return hashlib.sha256(text.encode("utf-8")).hexdigest()


def _is_windows() -> bool:
    return platform.system().lower().startswith("win")


def _run_lines(args: list[str], timeout: float = 1.5) -> list[str]:
    try:
        done = subprocess.run(
            args, capture_output=True, text=True, timeout=timeout, errors="ignore"
        )
        return (done.stdout or "").splitlines()
    except Exception:
        return []


def _wmic_query(table: str, field: str) -> str:
    if not _is_windows():
        return "UNKNOWN"
    seen_header = False
    for line in _run_lines(["wmic", table, "get", field]):
        line = line.strip()
        if not line:
            continue
        if not seen_header:
            seen_header = True
            continue
        return line
    return "UNKNOWN"


def _hostname() -> str:
    if _is_windows():
        value = os.environ.get("COMPUTERNAME")
        if value and value.strip():
            return value.strip()
    value = os.environ.get("HOSTNAME")
    if value and value.strip():
        return value.strip()
    for line in _run_lines(["hostname"]):
        if line.strip():
            return line.strip()
    return "UNKNOWN"


def local_hwid() -> tuple[str, str]:
    """Возвращает (process(), compute()) — sha256 от короткого и полного отпечатка."""
    hostname = _hostname()
    short = f"{_wmic_query('csproduct', 'UUID')}|{hostname}"
    full = "|".join(
        [
            _wmic_query("csproduct", "UUID"),
            _wmic_query("diskdrive", "SerialNumber"),
            _wmic_query("baseboard", "SerialNumber"),
            _wmic_query("cpu", "ProcessorId"),
            hostname,
        ]
    )
    return _sha256_hex(short), _sha256_hex(full)


def normalize_hwid(value: str) -> str:
    """Клиент сравнивает sha256-hex. Принимаем и сырой отпечаток — хэшируем сами."""
    value = value.strip()
    if re.fullmatch(r"[0-9a-fA-F]{64}", value):
        return value.lower()
    return _sha256_hex(value)


# ---------------------------------------------------------------------------
# Время
# ---------------------------------------------------------------------------
def parse_until(value: str) -> int:
    if re.fullmatch(r"\d{10,}", value):
        number = int(value)
        return number * 1000 if number < 10**12 else number
    text = value.strip()
    if text.endswith("Z"):
        text = text[:-1] + "+00:00"
    try:
        moment = _dt.datetime.fromisoformat(text)
    except ValueError:
        raise SystemExit(f"не удалось разобрать дату: {value} (ожидается YYYY-MM-DD или epoch-ms)")
    if moment.tzinfo is None:
        moment = moment.replace(tzinfo=_dt.timezone.utc)
    return int(moment.timestamp() * 1000)


def fmt_epoch_ms(value: int) -> str:
    moment = _dt.datetime.fromtimestamp(value / 1000, _dt.timezone.utc)
    return f"{moment:%Y-%m-%d %H:%M:%S} UTC ({value})"


# ---------------------------------------------------------------------------
# Команды
# ---------------------------------------------------------------------------
RFC8032_VECTORS = [
    (
        "9d61b19deffd5a60ba844af492ec2cc44449c5697b326919703bac031cae7f60",
        "d75a980182b10ab7d54bfed3c964073a0ee172f3daa62325af021a68f707511a",
        "",
        "e5564300c360ac729086e2cc806e828a84877f1eb8e5d974d873e06522490155"
        "5fb8821590a33bacc61e39701cf9b46bd25bf5f0595bbe24655141438e7a100b",
    ),
    (
        "4ccd089b28ff96da9db6c346ec114e0f5b8a319f35aba624da8cf6ed4fb8a6fb",
        "3d4017c3e843895a92b70aa74d1b7ebc9c982ccf2ec4968cc0cd55f12af4660c",
        "72",
        "92a009a9f0d4cab8720e820b5f642540a2b27b5416503f8fb3762223ebdb69da"
        "085ac1e43e15996e458f3613d0f11d8c387b2eaeb4302aeeb00d291612bb0c00",
    ),
    (
        "c5aa8df43f9f837bedb7442f31dcb7b166d38535076f094b85ce3a2e0b4458f7",
        "fc51cd8e6218a1a38da47ed00230f0580816ed13ba3303ac5deb911548908025",
        "af82",
        "6291d657deec24024827e69c3abe01a30ce548a284743a445e3680d7db5ac3ac"
        "18ff9b538d16f290ae67f760984dc6594a7c15e9716ed28dc027beceea1ec40a",
    ),
]


def cmd_selftest(_args) -> int:
    failures = 0
    for index, (seed_hex, pk_hex, msg_hex, sig_hex) in enumerate(RFC8032_VECTORS, 1):
        seed = bytes.fromhex(seed_hex)
        message = bytes.fromhex(msg_hex)
        expected_pk = bytes.fromhex(pk_hex)
        expected_sig = bytes.fromhex(sig_hex)
        got_pk = public_key(seed)
        got_sig = sign(message, seed)
        ok_pk = got_pk == expected_pk
        ok_sig = got_sig == expected_sig
        ok_verify = verify(expected_sig, message, expected_pk)
        ok = ok_pk and ok_sig and ok_verify
        failures += 0 if ok else 1
        print(f"  RFC 8032 TEST {index}: {'OK' if ok else 'FAIL'}")

    seed = bytes.fromhex("11" * 32)
    pk = public_key(seed)
    message = b'{"validUntil":1,"hwidHash":"aa"}'
    signature = sign(message, seed)
    roundtrip = verify(signature, message, pk)
    tampered = not verify(signature, message + b" ", pk)
    print(f"  roundtrip подпись/проверка: {'OK' if roundtrip else 'FAIL'}")
    print(f"  изменённый payload отклонён: {'OK' if tampered else 'FAIL'}")
    failures += 0 if roundtrip else 1
    failures += 0 if tampered else 1

    wrong_key = public_key(bytes.fromhex("22" * 32))
    rejected = not verify(signature, message, wrong_key)
    print(f"  чужая пара ключей отклонена: {'OK' if rejected else 'FAIL'}")
    failures += 0 if rejected else 1

    print("selftest:", "всё в порядке" if failures == 0 else f"{failures} ошибок")
    return 0 if failures == 0 else 1


def cmd_genkey(args) -> int:
    os.makedirs(args.out_dir, exist_ok=True)
    seed = os.urandom(32)
    pk = public_key(seed)
    private_path = os.path.join(args.out_dir, "test-private.pem")
    public_path = os.path.join(args.out_dir, "test-public.pem")
    with open(private_path, "w", encoding="utf-8") as handle:
        handle.write(seed_to_pkcs8_pem(seed))
    with open(public_path, "w", encoding="utf-8") as handle:
        handle.write(pub_to_spki_pem(pk))
    print(f"приватный ключ: {private_path}")
    print(f"публичный ключ: {public_path}")
    print(f"публичный ключ (hex): {pk.hex()}")
    print()
    print("ВНИМАНИЕ: это тестовая пара. Публичный ключ клиента вшит в код —"
          " клиент примет только подпись НАСТОЯЩЕГО приватного ключа владельца.")
    return 0


def cmd_issue(args) -> int:
    seed = load_seed(args.key)
    hwid = normalize_hwid(args.hwid)

    now_ms = int(time.time() * 1000)
    valid_until = resolve_valid_until(now_ms, args.days, args.until)
    if valid_until <= now_ms:
        raise SystemExit(
            f"validUntil в прошлом: {fmt_epoch_ms(valid_until)} (сейчас {fmt_epoch_ms(now_ms)})"
        )

    claims = {}
    for claim in args.claim or []:
        if "=" not in claim:
            raise SystemExit(f"--claim ожидает key=value, получено: {claim}")
        key, _, value = claim.partition("=")
        claims[key.strip()] = value

    license_doc, _, signature = build_license(seed, valid_until, hwid, claims)
    with open(args.out, "w", encoding="utf-8") as handle:
        handle.write(dumps_license(license_doc))

    client_ok = verify(signature, _unb64url(license_doc["payload"]), CLIENT_PUBLIC_KEY)
    print(f"лицензия записана: {args.out}")
    print(f"срок действия:    {fmt_epoch_ms(valid_until)}")
    print(f"hwidHash:         {hwid}")
    print(f"подпись от ключа клиента (то, что проверит клиент): "
          f"{'ДА — клиент примет' if client_ok else 'НЕТ — клиент отклонит (ключ не от владельца)'}")
    print()
    print("Не забудьте продлить привязку: срок жизни константы в сборке клиента"
          f" и grace-окно ({DEFAULT_GRACE_MS // 3600000} ч) ограничивают проверку во времени.")
    return 0


def cmd_verify(args) -> int:
    with open(args.file, "r", encoding="utf-8") as handle:
        doc = json.load(handle)
    payload_bytes = _unb64url(doc["payload"])
    signature = _unb64url(doc["signature"])
    pk = load_public_key(args.pubkey)

    payload = json.loads(payload_bytes.decode("utf-8"))
    now_ms = args.at if args.at else int(time.time() * 1000)

    print(f"файл:            {args.file}")
    print(f"публичный ключ:  {pk.hex()}")
    print(f"payload:         {payload_bytes.decode('utf-8')}")

    signature_ok = verify(signature, payload_bytes, pk)
    valid_until = int(payload.get("validUntil", 0))
    hwid = str(payload.get("hwidHash", ""))
    print(f"подпись:         {'ВАЛИДНА' if signature_ok else 'НЕВАЛИДНА'}")
    print(f"validUntil:      {fmt_epoch_ms(valid_until) if valid_until else 'отсутствует'}")
    print(f"ещё действует:   {'да' if valid_until > now_ms else 'нет'}")
    print(f"hwidHash:        {hwid or 'отсутствует'}")
    local_short, local_full = local_hwid()
    if hwid:
        if hwid.lower() in (local_short, local_full):
            print("привязка hwid:   совпадает с этой машиной")
        else:
            print("привязка hwid:   НЕ совпадает с этой машиной (или запрос wmic недоступен)")
    print(f"текущее время:   {fmt_epoch_ms(now_ms)}")
    return 0 if signature_ok else 1


# ---------------------------------------------------------------------------
# Ядро выпуска лицензий (используется CLI, пакетной выдачей и сервисом)
# ---------------------------------------------------------------------------
CLIENT_PUBLIC_KEY = bytes.fromhex(CLIENT_PUBLIC_KEY_HEX)


def resolve_valid_until(now_ms: int, days: float | None = None, until: str | None = None) -> int:
    """Считает validUntil: либо из --days, либо из --until (дата/ISO/epoch-ms)."""
    if until:
        return parse_until(str(until))
    if days is None:
        raise SystemExit("нужно указать срок: --days или --until")
    return now_ms + int(round(float(days) * 86400000))


def build_license(seed: bytes, valid_until: int, hwid_hash: str, claims: dict | None = None):
    """Собирает документ лицензии. Возвращает (doc, payload_bytes, signature)."""
    payload = {"validUntil": int(valid_until), "hwidHash": hwid_hash}
    if claims:
        payload.update(claims)

    payload_bytes = json.dumps(payload, ensure_ascii=False, separators=(",", ":")).encode("utf-8")
    signature = sign(payload_bytes, seed)
    if not verify(signature, payload_bytes, public_key(seed)):
        raise RuntimeError("внутренняя ошибка: подпись не проходит самопроверку")
    return {"payload": _b64url(payload_bytes), "signature": _b64url(signature)}, payload_bytes, signature


def dumps_license(doc: dict) -> str:
    return json.dumps(doc, ensure_ascii=False, indent=2) + "\n"


def accepted_by_client(doc: dict, public_key_bytes: bytes | None = None) -> bool:
    """Проверит ли выпущенную лицензию клиент.

    По умолчанию используется публичный ключ, вшитый в текущую сборку клиента;
    можно передать свой (например, тестовый) — для проверок в тестах.
    """
    payload_bytes = _unb64url(doc["payload"])
    return verify(_unb64url(doc["signature"]), payload_bytes, public_key_bytes or CLIENT_PUBLIC_KEY)


# ---------------------------------------------------------------------------
# Allowlist — кому сервис продления имеет право выдавать лицензию
# ---------------------------------------------------------------------------
ALLOWLIST_VERSION = 1


class Allowlist:
    """Список клиентов с их правами: сервис продлевает только эти HWID.

    Формат файла:
        {"version": 1, "generatedAt": <epoch-ms>, "entries": [
            {"hwid": "<sha256 hex>", "label": "Иван", "validUntil": <epoch-ms>, "note": ""}
        ]}
    """

    def __init__(self, path: str | None):
        self.path = path
        self._entries: dict[str, dict] = {}
        self._mtime: float | None = None
        if path and os.path.exists(path):
            self.reload(force=True)

    # -- чтение/запись ------------------------------------------------------
    def reload(self, force: bool = False) -> dict[str, dict]:
        if not self.path or not os.path.exists(self.path):
            self._entries = {}
            self._mtime = None
            return self._entries
        mtime = os.path.getmtime(self.path)
        if not force and self._mtime == mtime:
            return self._entries
        with open(self.path, "r", encoding="utf-8") as handle:
            doc = json.load(handle)
        entries: dict[str, dict] = {}
        for raw in doc.get("entries", []):
            hwid = normalize_hwid(str(raw.get("hwid", "")))
            if not hwid:
                continue
            entries[hwid] = {
                "hwid": hwid,
                "label": str(raw.get("label", "")),
                "validUntil": int(raw.get("validUntil", 0) or 0),
                "note": str(raw.get("note", "")),
            }
        self._entries = entries
        self._mtime = mtime
        return self._entries

    def save(self) -> None:
        if not self.path:
            raise SystemExit("не задан путь к allowlist (--file)")
        doc = {
            "version": ALLOWLIST_VERSION,
            "generatedAt": int(time.time() * 1000),
            "entries": sorted(self._entries.values(), key=lambda item: (item["label"], item["hwid"])),
        }
        tmp = f"{self.path}.tmp"
        with open(tmp, "w", encoding="utf-8") as handle:
            handle.write(json.dumps(doc, ensure_ascii=False, indent=2) + "\n")
        os.replace(tmp, self.path)
        self.reload(force=True)

    # -- операции -----------------------------------------------------------
    def add(self, hwid: str, valid_until: int, label: str = "", note: str = "") -> dict:
        normalized = normalize_hwid(hwid)
        self.reload()
        entry = {"hwid": normalized, "label": label, "validUntil": int(valid_until), "note": note}
        self._entries[normalized] = entry
        self.save()
        return entry

    def remove(self, hwid: str) -> bool:
        normalized = normalize_hwid(hwid)
        self.reload()
        if normalized in self._entries:
            del self._entries[normalized]
            self.save()
            return True
        return False

    def get(self, hwid: str) -> dict | None:
        self.reload()
        return self._entries.get(normalize_hwid(hwid))

    def __len__(self) -> int:
        self.reload()
        return len(self._entries)


# ---------------------------------------------------------------------------
# Пакетная выдача
# ---------------------------------------------------------------------------
def parse_client_list(text: str) -> list[dict]:
    """Разбирает список клиентов: `hwid[,label[,days|until]]`, `#` — комментарий.

    Поддерживается CSV с заголовком, где есть колонка `hwid`; остальные колонки
    с известными именами (label, days, until) подхватываются по имени.
    """
    rows: list[dict] = []
    lines = [line for line in text.splitlines() if line.strip() and not line.lstrip().startswith("#")]
    if not lines:
        return rows

    delimiter = max((",", ";", "\t"), key=lambda ch: lines[0].count(ch))
    if lines[0].count(delimiter) == 0:
        delimiter = ","

    reader = csv.reader(lines, delimiter=delimiter)
    parsed = [row for row in reader if any(cell.strip() for cell in row)]
    if not parsed:
        return rows

    header = [cell.strip().lower() for cell in parsed[0]]
    has_header = "hwid" in header
    columns = header if has_header else ["hwid", "label", "days"]
    body = parsed[1:] if has_header else parsed

    for index, row in enumerate(body, 1):
        item: dict = {"line": index}
        for position, cell in enumerate(row):
            if position >= len(columns):
                break
            key = columns[position]
            value = cell.strip()
            if value:
                item[key] = value
        # дата, попавшая в колонку days (частый случай в списках клиентов), — это until
        maybe_date = item.get("days", "")
        if maybe_date and not _is_number(maybe_date) and _looks_like_date(maybe_date):
            item.pop("days")
            item.setdefault("until", maybe_date)
        rows.append(item)
    return rows


def _is_number(value: str) -> bool:
    try:
        float(value)
        return True
    except (TypeError, ValueError):
        return False


def _looks_like_date(value: str) -> bool:
    value = value.strip()
    if re.fullmatch(r"\d{10,}", value):
        return True
    return bool(re.match(r"^\d{4}-\d{2}-\d{2}", value))


def cmd_issue_batch(args) -> int:
    seed = load_seed(args.key)
    if args.input == "-":
        text = sys.stdin.read()
    else:
        with open(args.input, "r", encoding="utf-8") as handle:
            text = handle.read()

    rows = parse_client_list(text)
    if not rows:
        raise SystemExit("список клиентов пуст")

    os.makedirs(args.out_dir, exist_ok=True)
    report_path = args.report
    zip_handle = zipfile.ZipFile(args.zip, "w", zipfile.ZIP_DEFLATED) if args.zip else None

    report: list[dict] = []
    seen: set[str] = set()
    issued = 0
    now_ms = int(time.time() * 1000)

    try:
        for row in rows:
            line_no = row["line"]
            raw_hwid = row.get("hwid", "")
            label = row.get("label", "")
            record = {
                "line": line_no,
                "hwid": "",
                "label": label,
                "status": "error",
                "validUntil": "",
                "file": "",
                "message": "",
            }

            if not raw_hwid:
                record["message"] = "нет колонки hwid"
                report.append(record)
                continue

            hwid = normalize_hwid(raw_hwid)
            record["hwid"] = hwid
            if hwid in seen:
                record["status"] = "duplicate"
                record["message"] = "HWID уже встречался в списке"
                report.append(record)
                continue

            try:
                row_until = row.get("until")
                row_days = row.get("days")
                if row_until or row_days:
                    # срок из строки списка имеет приоритет над значениями по умолчанию
                    valid_until = resolve_valid_until(
                        now_ms, float(row_days) if row_days else None, row_until
                    )
                else:
                    valid_until = resolve_valid_until(now_ms, args.days, args.until)
            except (SystemExit, ValueError) as error:
                record["message"] = f"некорректный срок: {error}"
                report.append(record)
                continue

            if valid_until <= now_ms:
                record["message"] = f"срок в прошлом: {fmt_epoch_ms(valid_until)}"
                report.append(record)
                continue

            doc, _, _ = build_license(seed, valid_until, hwid)
            # \w сохраняет кириллицу — имена файлов остаются читаемыми
            safe_label = re.sub(r"[^\w.-]+", "-", label, flags=re.UNICODE).strip("-") or "client"
            file_name = f"{line_no:04d}_{safe_label}_{hwid[:12]}.license.json"
            file_path = os.path.join(args.out_dir, file_name)
            text_doc = dumps_license(doc)
            with open(file_path, "w", encoding="utf-8") as handle:
                handle.write(text_doc)
            if zip_handle:
                zip_handle.writestr(file_name, text_doc)

            seen.add(hwid)
            issued += 1
            record.update(
                {
                    "status": "ok",
                    "validUntil": valid_until,
                    "file": file_path,
                    "message": "выдана",
                }
            )
            report.append(record)
    finally:
        if zip_handle:
            zip_handle.close()

    report_path = report_path or os.path.join(args.out_dir, "report.csv")
    with open(report_path, "w", encoding="utf-8", newline="") as handle:
        writer = csv.DictWriter(
            handle, fieldnames=["line", "hwid", "label", "status", "validUntil", "file", "message"]
        )
        writer.writeheader()
        writer.writerows(report)

    failed = [item for item in report if item["status"] != "ok"]
    client_compatible = None
    first_ok = next((item for item in report if item["status"] == "ok"), None)
    if first_ok:
        with open(first_ok["file"], "r", encoding="utf-8") as handle:
            client_compatible = accepted_by_client(json.load(handle))

    print(f"выдано лицензий: {issued} из {len(report)}")
    for item in failed:
        print(f"  строка {item['line']}: {item['status']} — {item['message']} (hwid={item['hwid'][:16]}...)")
    print(f"отчёт:  {report_path}")
    print(f"папка:  {args.out_dir}")
    if args.zip:
        print(f"архив:  {args.zip}")
    if client_compatible is not None:
        print(
            "проверка вшитым ключом клиента: "
            + ("ДАННЫЕ" if client_compatible else "ОТКЛОНЕНО — ключ не от владельца клиента")
        )

    if args.fail_on_error and failed:
        return 1
    if issued == 0:
        return 1
    return 0


# ---------------------------------------------------------------------------
# Allowlist: CLI
# ---------------------------------------------------------------------------
def cmd_allowlist(args) -> int:
    allow = Allowlist(args.file)

    if args.action == "add":
        valid_until = resolve_valid_until(int(time.time() * 1000), args.days, args.until)
        entry = allow.add(args.hwid, valid_until, label=args.label or "", note=args.note or "")
        print(f"добавлено: {entry['hwid']}")
        print(f"подпись:   {entry['label'] or 'без имени'}")
        print(f"действует: {fmt_epoch_ms(entry['validUntil'])}")
        print(f"файл:      {args.file}")
        return 0

    if args.action == "remove":
        removed = allow.remove(args.hwid)
        print(("удалено: " if removed else "не найдено: ") + normalize_hwid(args.hwid))
        print(f"файл: {args.file}")
        return 0 if removed else 1

    entries = sorted(allow.reload(force=True).values(), key=lambda item: (item["label"], item["hwid"]))
    if not entries:
        print(f"allowlist пуст: {args.file}")
        return 0

    now_ms = int(time.time() * 1000)
    print(f"{'HWID':<66} {'ДО КОГДА':<21} {'СТАТУС':<10} ИМЯ")
    for entry in entries:
        moment = _dt.datetime.fromtimestamp(entry["validUntil"] / 1000, _dt.timezone.utc)
        state = "активен" if entry["validUntil"] > now_ms else "истёк"
        print(f"{entry['hwid']:<66} {moment:%Y-%m-%d %H:%M:%S}  {state:<10} {entry['label']}")
    print(f"\nвсего: {len(entries)} — {args.file}")
    return 0


def cmd_hwid(_args) -> int:
    short, full = local_hwid()
    print(f"process() (UUID|HOSTNAME)                        → {short}")
    print(f"compute() (UUID|ДИСК|ПЛАТА|CPU|HOSTNAME)         → {full}")
    print()
    print("Запрос клиента к клиенту (hwidHash) обычно содержит одно из этих значений;")
    print("на не-Windows поля железа будут UNKNOWN — считайте на машине пользователя.")
    return 0


def build_parser() -> argparse.ArgumentParser:
    parser = argparse.ArgumentParser(
        prog="wild_license.py",
        description="Выпуск и проверка license.json для WildClient (сторона владельца)",
        formatter_class=argparse.RawDescriptionHelpFormatter,
        epilog=__doc__,
    )
    sub = parser.add_subparsers(dest="command", required=True)

    selftest = sub.add_parser("selftest", help="проверить реализацию Ed25519 (RFC 8032)")
    selftest.set_defaults(func=cmd_selftest)

    genkey = sub.add_parser("genkey", help="сгенерировать тестовую пару ключей")
    genkey.add_argument("--out-dir", default=".", help="куда положить ключи")
    genkey.set_defaults(func=cmd_genkey)

    issue = sub.add_parser("issue", help="выпустить license.json")
    issue.add_argument("--key", required=True, help="приватный ключ владельца (PKCS#8 PEM / raw seed)")
    issue.add_argument("--hwid", required=True, help="hwidHash клиента (64 hex) или сырой отпечаток")
    issue.add_argument("--days", type=float, default=30.0, help="срок от текущего момента (по умолчанию 30)")
    issue.add_argument("--until", help="дата окончания: YYYY-MM-DD, ISO-8601 или epoch-ms")
    issue.add_argument("--out", default="license.json", help="куда записать license.json")
    issue.add_argument("--claim", action="append", help="доп. поле payload: key=value (можно повторять)")
    issue.set_defaults(func=cmd_issue)

    batch = sub.add_parser("issue-batch", help="выпустить пакет лицензий по списку клиентов")
    batch.add_argument("--key", required=True, help="приватный ключ владельца")
    batch.add_argument("--input", required=True, help="файл списка (CSV/TXT) или - для stdin")
    batch.add_argument("--days", type=float, default=30.0, help="срок по умолчанию (можно переопределить в строке)")
    batch.add_argument("--until", help="срок по умолчанию датой (YYYY-MM-DD / ISO / epoch-ms)")
    batch.add_argument("--out-dir", default="licenses", help="куда складывать файлы лицензий")
    batch.add_argument("--report", help="CSV-отчёт (по умолчанию <out-dir>/report.csv)")
    batch.add_argument("--zip", help="дополнительно упаковать выданное в zip-архив")
    batch.add_argument("--fail-on-error", action="store_true", help="вернуть код 1, если были проблемные строки")
    batch.set_defaults(func=cmd_issue_batch)

    allow_common = argparse.ArgumentParser(add_help=False)
    allow_common.add_argument("--file", default="allowlist.json", help="файл allowlist (JSON)")

    allow = sub.add_parser(
        "allowlist",
        help="список клиентов, которым сервис продлевает лицензии",
        parents=[allow_common],
    )
    allow_sub = allow.add_subparsers(dest="action", required=True)

    allow_add = allow_sub.add_parser("add", help="добавить/обновить клиента", parents=[allow_common])
    allow_add.add_argument("--hwid", required=True, help="HWID клиента (64 hex или сырой отпечаток)")
    allow_add.add_argument("--label", help="имя/пометка клиента")
    allow_add.add_argument("--note", help="произвольная заметка")
    allow_add.add_argument("--days", type=float, help="срок от текущего момента")
    allow_add.add_argument("--until", help="срок датой (YYYY-MM-DD / ISO / epoch-ms)")

    allow_remove = allow_sub.add_parser("remove", help="убрать клиента", parents=[allow_common])
    allow_remove.add_argument("--hwid", required=True, help="HWID клиента")

    allow_sub.add_parser("list", help="показать список", parents=[allow_common])
    allow.set_defaults(func=cmd_allowlist)

    verify_cmd = sub.add_parser("verify", help="проверить license.json")
    verify_cmd.add_argument("--file", default="license.json", help="файл лицензии")
    verify_cmd.add_argument(
        "--pubkey",
        default=None,
        help="SPKI PEM / 64 hex / file (по умолчанию — ключ, вшитый в клиент)",
    )
    verify_cmd.add_argument("--at", type=int, help="проверить срок на момент epoch-ms")
    verify_cmd.set_defaults(func=cmd_verify)

    hwid = sub.add_parser("hwid", help="посчитать HWID-хэш этой машины")
    hwid.set_defaults(func=cmd_hwid)

    return parser


def main(argv=None) -> int:
    args = build_parser().parse_args(argv)
    return args.func(args)


if __name__ == "__main__":
    sys.exit(main())
