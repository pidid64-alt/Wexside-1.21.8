#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
WildClient — сервис выдачи и продления лицензий (сторона владельца).

Держит приватный ключ владельца и подписывает license.json по HTTP-запросу.
По замыслу запускается НА МАШИНЕ ВЛАДЕЛЬЦА (или на его сервере), а не у клиентов.

Модель прав (важно):
  * `/v1/issue`  — только с админ-токеном: выпуск лицензии на любой HWID (для ручной работы и интеграций);
  * `/v1/renew`  — без админ-токена, но ТОЛЬКО для HWID из allowlist,
                   и только со сроком, который уже разрешён в allowlist.
                   Сервис никогда не «довыдаёт» права сам: нет записи в allowlist → 403.
  * `/v1/license` — выдача ранее настроенной лицензии по HWID, только с админ-токеном.

Безопасность по умолчанию:
  * слушает только 127.0.0.1 (наружу — только явным `--host` и, желательно, с TLS);
  * не стартует без админ-токена (мин. 16 символов);
  * лимит размера запроса и частоты обращений с одного IP;
  * в логи не попадают ни тело запроса, ни токен;
  * окно выдачи ограничено `--max-days` (по умолчанию 400).

Запуск:
    python3 license_service.py --key owner-private.pem \\
        --allowlist allowlist.json --token <админ-токен>

Endpoints:
    GET  /healthz                — статус (публичный, без секретов)
    GET  /                       — человекочитаемая страница статуса
    GET  /v1/license?hwid=<hex>  — [админ] готовая лицензия для HWID
    POST /v1/issue               — [админ] {"hwid": "...", "days": 30} или {"until": "2026-12-31"}
    POST /v1/renew               — [клиент из allowlist] {"hwid": "..."}

Токен передаётся заголовком `Authorization: Bearer <токен>` или `X-Admin-Token`.
"""

from __future__ import annotations

import argparse
import hmac
import json
import os
import signal
import ssl
import sys
import threading
import time
from collections import defaultdict, deque
from http.server import BaseHTTPRequestHandler, ThreadingHTTPServer
from urllib.parse import parse_qs, urlparse

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))

import wild_license as wl  # noqa: E402

MAX_BODY_BYTES = 64 * 1024


class ServiceConfig:
    def __init__(self, **kwargs):
        self.key_path: str = kwargs.get("key_path", "")
        self.seed: bytes = kwargs.get("seed", b"")
        self.public_key: bytes = kwargs.get("public_key", b"")
        self.allowlist: wl.Allowlist = kwargs.get("allowlist") or wl.Allowlist(None)
        self.admin_token: str = kwargs.get("admin_token", "")
        self.max_days: float = kwargs.get("max_days", 400.0)
        self.rate_limit: int = kwargs.get("rate_limit", 60)
        self.rate_window: float = kwargs.get("rate_window", 60.0)
        self.require_allowlist_for_renew: bool = kwargs.get("require_allowlist_for_renew", True)
        self.default_renew_days: float = kwargs.get("default_renew_days", 30.0)


class WildLicenseServer(ThreadingHTTPServer):
    daemon_threads = True
    allow_reuse_address = True

    def __init__(self, address, handler, config: ServiceConfig):
        super().__init__(address, handler)
        self.config = config
        self.hits: dict[str, deque] = defaultdict(deque)
        self.hits_lock = threading.Lock()

    # -- rate limiting ------------------------------------------------------
    def allow_request(self, client_ip: str) -> bool:
        limit = self.config.rate_limit
        if limit <= 0:
            return True
        now = time.monotonic()
        with self.hits_lock:
            bucket = self.hits[client_ip]
            while bucket and now - bucket[0] > self.config.rate_window:
                bucket.popleft()
            if len(bucket) >= limit:
                return False
            bucket.append(now)
            return True


class Handler(BaseHTTPRequestHandler):
    server_version = "WildLicense/1.0"
    protocol_version = "HTTP/1.1"

    # -- утилиты ------------------------------------------------------------
    def log_message(self, fmt, *args):
        sys.stderr.write(
            "[%s] %s %s\n" % (time.strftime("%Y-%m-%d %H:%M:%S"), self.address_string(), fmt % args)
        )

    def _send(self, status: int, payload: dict) -> None:
        body = json.dumps(payload, ensure_ascii=False, indent=2).encode("utf-8")
        self.send_response(status)
        self.send_header("Content-Type", "application/json; charset=utf-8")
        self.send_header("Content-Length", str(len(body)))
        self.send_header("Cache-Control", "no-store")
        self.end_headers()
        self.wfile.write(body)

    def _send_html(self, status: int, html: str) -> None:
        body = html.encode("utf-8")
        self.send_response(status)
        self.send_header("Content-Type", "text/html; charset=utf-8")
        self.send_header("Content-Length", str(len(body)))
        self.send_header("Cache-Control", "no-store")
        self.end_headers()
        self.wfile.write(body)

    def _authorized(self) -> bool:
        token = self.server.config.admin_token
        if not token:
            return False
        header = self.headers.get("Authorization", "")
        candidate = ""
        if header.lower().startswith("bearer "):
            candidate = header[7:].strip()
        if not candidate:
            candidate = self.headers.get("X-Admin-Token", "").strip()
        return bool(candidate) and hmac.compare_digest(candidate, token)

    def _read_json(self) -> dict | None:
        try:
            length = int(self.headers.get("Content-Length", "0") or 0)
        except ValueError:
            return None
        if length <= 0:
            return {}
        if length > MAX_BODY_BYTES:
            return None
        raw = self.rfile.read(length)
        try:
            parsed = json.loads(raw.decode("utf-8"))
        except Exception:
            return {}
        return parsed if isinstance(parsed, dict) else {}

    # -- маршрутизация ------------------------------------------------------
    def do_GET(self):  # noqa: N802
        self._route("GET")

    def do_POST(self):  # noqa: N802
        self._route("POST")

    def _route(self, method: str) -> None:
        client_ip = self.client_address[0] if self.client_address else "?"
        if not self.server.allow_request(client_ip):
            self._send(429, {"error": "rate_limited", "detail": "слишком много запросов"})
            return

        path = urlparse(self.path).path.rstrip("/") or "/"

        if method == "GET" and path == "/healthz":
            config = self.server.config
            config.allowlist.reload()
            self._send(
                200,
                {
                    "ok": True,
                    "service": "wild-license",
                    "signingKey": config.public_key.hex(),
                    "allowlistEntries": len(config.allowlist),
                    "maxDays": config.max_days,
                    "time": int(time.time() * 1000),
                },
            )
            return

        if method == "GET" and path == "/":
            config = self.server.config
            config.allowlist.reload()
            html = f"""<!doctype html>
<meta charset="utf-8">
<title>WildLicense service</title>
<style>body{{font:14px/1.5 system-ui,sans-serif;margin:40px;max-width:720px}}
code{{background:#f2f2f2;padding:2px 6px;border-radius:4px}}</style>
<h1>WildLicense service</h1>
<p>Сервис жив. Ключ подписи: <code>{config.public_key.hex()[:16]}…</code><br>
Клиентов в allowlist: <b>{len(config.allowlist)}</b> · лимит срока: {int(config.max_days)} дн.</p>
<ul>
  <li><code>GET /healthz</code> — статус</li>
  <li><code>GET /v1/license?hwid=…</code> — <b>админ</b></li>
  <li><code>POST /v1/issue</code> — <b>админ</b>, тело <code>{{"hwid":"…","days":30}}</code></li>
  <li><code>POST /v1/renew</code> — клиент из allowlist, тело <code>{{"hwid":"…"}}</code></li>
</ul>
<p>Токен: <code>Authorization: Bearer …</code> или <code>X-Admin-Token: …</code>.</p>
"""
            self._send_html(200, html)
            return

        if method == "GET" and path == "/v1/license":
            if not self._authorized():
                self._send(401, {"error": "unauthorized"})
                return
            query = parse_qs(urlparse(self.path).query)
            hwid_raw = (query.get("hwid") or [""])[0]
            if not hwid_raw:
                self._send(400, {"error": "bad_request", "detail": "нужен параметр hwid"})
                return
            self._issue_for(hwid_raw, days=self.server.config.default_renew_days, admin=True)
            return

        if method == "POST" and path == "/v1/issue":
            if not self._authorized():
                self._send(401, {"error": "unauthorized"})
                return
            body = self._read_json()
            if body is None:
                self._send(413, {"error": "too_large", "detail": f"тело больше {MAX_BODY_BYTES} байт"})
                return
            hwid_raw = str(body.get("hwid", "")).strip()
            if not hwid_raw:
                self._send(400, {"error": "bad_request", "detail": "нужно поле hwid"})
                return
            self._issue_for(hwid_raw, days=body.get("days"), until=body.get("until"), admin=True)
            return

        if method == "POST" and path == "/v1/renew":
            body = self._read_json()
            if body is None:
                self._send(413, {"error": "too_large", "detail": f"тело больше {MAX_BODY_BYTES} байт"})
                return
            hwid_raw = str(body.get("hwid", "")).strip()
            if not hwid_raw:
                self._send(400, {"error": "bad_request", "detail": "нужно поле hwid"})
                return
            config = self.server.config
            if not config.require_allowlist_for_renew:
                self._send(403, {"error": "renew_disabled", "detail": "самообслуживание выключено"})
                return
            entry = config.allowlist.get(hwid_raw)
            if not entry:
                self._send(
                    403,
                    {
                        "error": "not_allowed",
                        "detail": "HWID не найден в allowlist — сервис не выдаёт права, "
                                  "которых у клиента нет",
                    },
                )
                return
            now_ms = int(time.time() * 1000)
            if entry["validUntil"] <= now_ms:
                self._send(
                    403,
                    {
                        "error": "subscription_expired",
                        "detail": "подписка в allowlist истекла — нужно продление у владельца",
                        "validUntil": entry["validUntil"],
                    },
                )
                return
            self._issue_for(hwid_raw, valid_until=entry["validUntil"], admin=False, label=entry["label"])
            return

        self._send(404, {"error": "not_found", "path": path})

    # -- выпуск -------------------------------------------------------------
    def _issue_for(self, hwid_raw: str, days=None, until=None, valid_until=None, admin=False, label=""):
        config = self.server.config
        now_ms = int(time.time() * 1000)

        hwid = wl.normalize_hwid(hwid_raw)
        if not hwid or len(hwid) != 64:
            self._send(400, {"error": "bad_request", "detail": "некорректный hwid"})
            return

        if valid_until is None:
            try:
                valid_until = wl.resolve_valid_until(now_ms, days, until)
            except SystemExit as error:
                self._send(400, {"error": "bad_request", "detail": str(error)})
                return

        max_valid_until = now_ms + int(config.max_days * 86400000)
        if valid_until > max_valid_until:
            self._send(
                400,
                {
                    "error": "too_long",
                    "detail": f"срок больше лимита сервиса ({int(config.max_days)} дн.)",
                    "maxValidUntil": max_valid_until,
                },
            )
            return
        if valid_until <= now_ms:
            self._send(400, {"error": "bad_request", "detail": "срок уже истёк"})
            return

        doc, _, _ = wl.build_license(config.seed, valid_until, hwid)
        accepted = wl.accepted_by_client(doc)
        self._send(
            200,
            {
                "hwidHash": hwid,
                "label": label,
                "validUntil": valid_until,
                "issuedAt": now_ms,
                "admin": admin,
                "acceptedByClient": accepted,
                "license": doc,
            },
        )
        self.log_message(
            "issued hwid=%s… until=%d admin=%s clientOk=%s",
            hwid[:12],
            valid_until,
            admin,
            accepted,
        )


def create_server(config: ServiceConfig, host: str, port: int) -> WildLicenseServer:
    return WildLicenseServer((host, port), Handler, config)


def build_config(args) -> ServiceConfig:
    seed = wl.load_seed(args.key)
    public_key = wl.public_key(seed)
    token = args.token or os.environ.get("WILD_LICENSE_ADMIN_TOKEN", "")
    if args.token_file:
        with open(args.token_file, "r", encoding="utf-8") as handle:
            token = handle.read().strip()

    allowlist = wl.Allowlist(args.allowlist)
    return ServiceConfig(
        key_path=args.key,
        seed=seed,
        public_key=public_key,
        allowlist=allowlist,
        admin_token=token,
        max_days=args.max_days,
        rate_limit=args.rate_limit,
        rate_window=args.rate_window,
        require_allowlist_for_renew=not args.no_renew,
        default_renew_days=args.days,
    )


def main(argv=None) -> int:
    parser = argparse.ArgumentParser(
        prog="license_service.py",
        description="HTTP-сервис выдачи/продления лицензий WildClient (держит приватный ключ владельца)",
    )
    parser.add_argument("--key", required=True, help="приватный ключ владельца (PKCS#8 PEM / raw seed)")
    parser.add_argument("--allowlist", help="JSON со списком клиентов, которым разрешено продление")
    parser.add_argument("--token", help="админ-токен (или env WILD_LICENSE_ADMIN_TOKEN)")
    parser.add_argument("--token-file", help="файл с админ-токеном")
    parser.add_argument("--host", default="127.0.0.1", help="адрес прослушивания (по умолчанию только loopback)")
    parser.add_argument("--port", type=int, default=8787, help="порт (0 — случайный свободный)")
    parser.add_argument("--days", type=float, default=30.0, help="срок по умолчанию для /v1/issue и /v1/license")
    parser.add_argument("--max-days", type=float, default=400.0, help="максимальный срок, который сервис вообще выдаёт")
    parser.add_argument("--rate-limit", type=int, default=60, help="запросов с одного IP за окно (0 — без лимита)")
    parser.add_argument("--rate-window", type=float, default=60.0, help="окно лимита, секунды")
    parser.add_argument("--no-renew", action="store_true", help="запретить самообслуживание /v1/renew")
    parser.add_argument("--tls-cert", help="PEM-сертификат для HTTPS (вместе с --tls-key)")
    parser.add_argument("--tls-key", help="PEM-ключ для HTTPS")
    parser.add_argument("--allow-weak-token", action="store_true", help="разрешить токен короче 16 символов (не советуем)")
    args = parser.parse_args(argv)

    if not args.token and not args.token_file:
        print(
            "Ошибка: не задан админ-токен. Передайте --token/--token-file или env WILD_LICENSE_ADMIN_TOKEN.\n"
            "Сервис держит приватный ключ, поэтому без токена не стартует.",
            file=sys.stderr,
        )
        return 2

    config = build_config(args)

    if len(config.admin_token) < 16 and not args.allow_weak_token:
        print("Ошибка: админ-токен короче 16 символов — так нельзя (см. --allow-weak-token).", file=sys.stderr)
        return 2

    if config.public_key == wl.CLIENT_PUBLIC_KEY:
        print("Ключ совпадает с вшитым в клиент — подписи будут приниматься этой сборкой.", file=sys.stderr)
    else:
        print(
            "ВНИМАНИЕ: публичный ключ сервиса не совпадает с ключом, вшитым в клиент.\n"
            "         Клиент отклонит такие лицензии (ключ не от владельца этой сборки).",
            file=sys.stderr,
        )

    server = create_server(config, args.host, args.port)
    host, port = server.server_address[0], server.server_address[1]

    if host not in ("127.0.0.1", "::1", "localhost"):
        print(
            f"ВНИМАНИЕ: сервис слушает {host}:{port} (не loopback). Ключ подписи станет доступен по сети.\n"
            "          Используйте TLS (--tls-cert/--tls-key) и/или файрвол.",
            file=sys.stderr,
        )

    if args.tls_cert and args.tls_key:
        context = ssl.SSLContext(ssl.PROTOCOL_TLS_SERVER)
        context.load_cert_chain(args.tls_cert, args.tls_key)
        server.socket = context.wrap_socket(server.socket, server_side=True)

    scheme = "https" if args.tls_cert and args.tls_key else "http"
    print(f"WildLicense service → {scheme}://{host}:{port}")
    print(f"  ключ подписи:   {config.public_key.hex()[:16]}…")
    print(f"  клиентов:       {len(config.allowlist)} (allowlist={args.allowlist or 'не задан'})")
    print(f"  лимит срока:    {int(config.max_days)} дн.; запросов с IP: {config.rate_limit}/{int(config.rate_window)}с")
    print(f"  самообслуживание /v1/renew: {'выключено' if args.no_renew else 'только для allowlist'}")

    stopping = threading.Event()

    def _stop(_signum, _frame):
        if not stopping.is_set():
            stopping.set()
            threading.Thread(target=server.shutdown, daemon=True).start()

    for sig in (signal.SIGINT, signal.SIGTERM):
        try:
            signal.signal(sig, _stop)
        except (ValueError, OSError):
            pass

    try:
        server.serve_forever(poll_interval=0.5)
    finally:
        server.server_close()
    print("остановлено")
    return 0


if __name__ == "__main__":
    sys.exit(main())
