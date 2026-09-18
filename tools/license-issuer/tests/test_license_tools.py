#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Тесты утилит выпуска лицензий WildClient (stdlib-only, сеть — только localhost).

Запуск:
    cd tools/license-issuer && python3 -m unittest discover -s tests -v
"""

import json
import os
import sys
import tempfile
import threading
import time
import unittest
import urllib.error
import urllib.request

sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

import license_service as ls  # noqa: E402
import wild_license as wl  # noqa: E402

TEST_SEED = bytes(range(32))
ADMIN_TOKEN = "test-admin-token-0123456789"
HWID_A = "a" * 64
HWID_B = "b" * 64
HWID_UNKNOWN = "c" * 64


def http(method, url, body=None, token=None, raw_body=None, timeout=5.0):
    data = None
    headers = {}
    if raw_body is not None:
        data = raw_body
        headers["Content-Type"] = "application/json"
    elif body is not None:
        data = json.dumps(body).encode("utf-8")
        headers["Content-Type"] = "application/json"
    if token:
        headers["Authorization"] = f"Bearer {token}"
    request = urllib.request.Request(url, data=data, headers=headers, method=method)
    try:
        with urllib.request.urlopen(request, timeout=timeout) as response:
            payload = response.read().decode("utf-8")
            return response.status, json.loads(payload) if payload else {}
    except urllib.error.HTTPError as error:
        payload = error.read().decode("utf-8")
        return error.code, json.loads(payload) if payload else {}


class ServerFixture:
    def __init__(self, *, allowlist_entries=None, rate_limit=60, max_days=400.0, no_renew=False):
        self.tmp = tempfile.TemporaryDirectory()
        allowlist_path = os.path.join(self.tmp.name, "allowlist.json")
        allowlist = wl.Allowlist(allowlist_path)
        for hwid, valid_until, label in allowlist_entries or []:
            allowlist.add(hwid, valid_until, label=label)

        self.config = ls.ServiceConfig(
            seed=TEST_SEED,
            public_key=wl.public_key(TEST_SEED),
            allowlist=allowlist,
            admin_token=ADMIN_TOKEN,
            max_days=max_days,
            rate_limit=rate_limit,
            rate_window=60.0,
            require_allowlist_for_renew=not no_renew,
            default_renew_days=30.0,
        )
        self.server = ls.create_server(self.config, "127.0.0.1", 0)
        self.port = self.server.server_address[1]
        self.thread = threading.Thread(target=self.server.serve_forever, kwargs={"poll_interval": 0.1}, daemon=True)
        self.thread.start()

    @property
    def base(self):
        return f"http://127.0.0.1:{self.port}"

    def close(self):
        self.server.shutdown()
        self.server.server_close()
        self.thread.join(timeout=5)
        self.tmp.cleanup()

    def __enter__(self):
        return self

    def __exit__(self, *exc):
        self.close()


class Ed25519Tests(unittest.TestCase):
    def test_rfc8032_vectors(self):
        for seed_hex, pk_hex, msg_hex, sig_hex in wl.RFC8032_VECTORS:
            seed = bytes.fromhex(seed_hex)
            message = bytes.fromhex(msg_hex)
            self.assertEqual(wl.public_key(seed).hex(), pk_hex)
            self.assertEqual(wl.sign(message, seed).hex(), sig_hex)
            self.assertTrue(wl.verify(bytes.fromhex(sig_hex), message, bytes.fromhex(pk_hex)))

    def test_tampered_payload_rejected(self):
        signature = wl.sign(b"payload", TEST_SEED)
        self.assertFalse(wl.verify(signature, b"payload!", wl.public_key(TEST_SEED)))

    def test_foreign_key_rejected_by_client(self):
        doc, _, _ = wl.build_license(TEST_SEED, int(time.time() * 1000) + 86400000, HWID_A)
        self.assertFalse(wl.accepted_by_client(doc))


class BatchTests(unittest.TestCase):
    def setUp(self):
        self.tmp = tempfile.TemporaryDirectory()
        self.addCleanup(self.tmp.cleanup)

    def _input(self, text, name="clients.csv"):
        path = os.path.join(self.tmp.name, name)
        with open(path, "w", encoding="utf-8") as handle:
            handle.write(text)
        return path

    def test_parse_with_header_and_plain_list(self):
        rows = wl.parse_client_list("# комментарий\nhwid,label,days\n" + HWID_A + ",Иван,10\n")
        self.assertEqual(rows, [{"line": 1, "hwid": HWID_A, "label": "Иван", "days": "10"}])

        rows = wl.parse_client_list(f"{HWID_A}\n{HWID_B},Пётр\n")
        self.assertEqual(rows[0]["hwid"], HWID_A)
        self.assertEqual(rows[1]["label"], "Пётр")

    def test_batch_issues_files_report_and_skips_duplicates(self):
        source = self._input(
            "hwid,label,days\n"
            f"{HWID_A},Иван,15\n"
            f"{HWID_A},Дубль,15\n"
            "не-hwid-нормализуется,БезДефисов,5\n"
        )
        out_dir = os.path.join(self.tmp.name, "licenses")
        zip_path = os.path.join(self.tmp.name, "bundle.zip")
        args = type(
            "Args",
            (),
            {
                "key": None,
                "input": source,
                "days": 30.0,
                "until": None,
                "out_dir": out_dir,
                "report": None,
                "zip": zip_path,
                "fail_on_error": False,
            },
        )()
        key_path = os.path.join(self.tmp.name, "test.key")
        with open(key_path, "w", encoding="utf-8") as handle:
            handle.write(wl.seed_to_pkcs8_pem(TEST_SEED))
        args.key = key_path

        code = wl.cmd_issue_batch(args)
        self.assertEqual(code, 0)

        report_path = os.path.join(out_dir, "report.csv")
        with open(report_path, "r", encoding="utf-8") as handle:
            report = handle.read()
        self.assertIn("duplicate", report)

        files = [name for name in sorted(os.listdir(out_dir)) if name.endswith(".license.json")]
        self.assertEqual(len(files), 2)
        self.assertTrue(os.path.exists(zip_path))

        payloads = []
        for name in files:
            with open(os.path.join(out_dir, name), "r", encoding="utf-8") as handle:
                payloads.append(json.load(handle))
        for doc in payloads:
            self.assertTrue(wl.verify(wl._unb64url(doc["signature"]), wl._unb64url(doc["payload"]), wl.public_key(TEST_SEED)))
        days = sorted(
            round((json.loads(wl._unb64url(doc["payload"]).decode())["validUntil"] - time.time() * 1000) / 86400000)
            for doc in payloads
        )
        self.assertEqual(days, [5, 15])


class AllowlistTests(unittest.TestCase):
    def test_add_remove_list(self):
        with tempfile.TemporaryDirectory() as tmp:
            path = os.path.join(tmp, "allow.json")
            allow = wl.Allowlist(path)
            future = int(time.time() * 1000) + 30 * 86400000
            allow.add(HWID_A, future, label="Иван")
            allow.add(HWID_B, future, label="Пётр")

            reloaded = wl.Allowlist(path)
            self.assertEqual(len(reloaded), 2)
            self.assertEqual(reloaded.get(HWID_A)["label"], "Иван")
            self.assertTrue(reloaded.remove(HWID_B))
            self.assertIsNone(wl.Allowlist(path).get(HWID_B))
            self.assertFalse(reloaded.remove(HWID_B))


class ServiceTests(unittest.TestCase):
    def test_healthz_is_public_and_reports_signing_key(self):
        with ServerFixture() as fixture:
            status, body = http("GET", fixture.base + "/healthz")
            self.assertEqual(status, 200)
            self.assertTrue(body["ok"])
            self.assertEqual(body["signingKey"], wl.public_key(TEST_SEED).hex())

    def test_issue_requires_token(self):
        with ServerFixture() as fixture:
            status, body = http("POST", fixture.base + "/v1/issue", {"hwid": HWID_A, "days": 5})
            self.assertEqual(status, 401)
            self.assertEqual(body["error"], "unauthorized")

    def test_issue_with_token_returns_valid_license(self):
        with ServerFixture() as fixture:
            status, body = http("POST", fixture.base + "/v1/issue", {"hwid": HWID_A, "days": 7}, token=ADMIN_TOKEN)
            self.assertEqual(status, 200)
            self.assertEqual(body["hwidHash"], HWID_A)
            self.assertTrue(wl.accepted_by_client(body["license"], wl.public_key(TEST_SEED)))
            payload = json.loads(wl._unb64url(body["license"]["payload"]).decode())
            self.assertAlmostEqual((payload["validUntil"] - time.time() * 1000) / 86400000, 7, delta=0.01)

    def test_issue_rejects_bad_hwid_and_long_term(self):
        with ServerFixture(max_days=10) as fixture:
            status, _ = http("POST", fixture.base + "/v1/issue", {"hwid": "не-hwid"}, token=ADMIN_TOKEN)
            self.assertEqual(status, 400)
            status, body = http("POST", fixture.base + "/v1/issue", {"hwid": HWID_A, "days": 3650}, token=ADMIN_TOKEN)
            self.assertEqual(status, 400)
            self.assertEqual(body["error"], "too_long")

    def test_license_endpoint_with_token(self):
        with ServerFixture() as fixture:
            status, body = http("GET", fixture.base + f"/v1/license?hwid={HWID_A}", token=ADMIN_TOKEN)
            self.assertEqual(status, 200)
            self.assertTrue(wl.accepted_by_client(body["license"], wl.public_key(TEST_SEED)))
            status, _ = http("GET", fixture.base + f"/v1/license?hwid={HWID_A}")
            self.assertEqual(status, 401)

    def test_renew_only_for_allowlisted_hwid(self):
        expiry = int(time.time() * 1000) + 5 * 86400000
        with ServerFixture(allowlist_entries=[(HWID_A, expiry, "Иван")]) as fixture:
            status, body = http("POST", fixture.base + "/v1/renew", {"hwid": HWID_A})
            self.assertEqual(status, 200)
            self.assertEqual(body["validUntil"], expiry)
            self.assertFalse(body["admin"])
            self.assertTrue(wl.accepted_by_client(body["license"], wl.public_key(TEST_SEED)))

            status, body = http("POST", fixture.base + "/v1/renew", {"hwid": HWID_UNKNOWN})
            self.assertEqual(status, 403)
            self.assertEqual(body["error"], "not_allowed")

    def test_renew_rejected_when_subscription_expired(self):
        expiry = int(time.time() * 1000) - 1000
        with ServerFixture(allowlist_entries=[(HWID_A, expiry, "Иван")]) as fixture:
            status, body = http("POST", fixture.base + "/v1/renew", {"hwid": HWID_A})
            self.assertEqual(status, 403)
            self.assertEqual(body["error"], "subscription_expired")

    def test_renew_can_be_disabled(self):
        expiry = int(time.time() * 1000) + 86400000
        with ServerFixture(allowlist_entries=[(HWID_A, expiry, "Иван")], no_renew=True) as fixture:
            status, body = http("POST", fixture.base + "/v1/renew", {"hwid": HWID_A})
            self.assertEqual(status, 403)
            self.assertEqual(body["error"], "renew_disabled")

    def test_rate_limit_and_body_limit(self):
        with ServerFixture(rate_limit=3) as fixture:
            codes = [
                http("GET", fixture.base + "/healthz")[0] for _ in range(4)
            ]
            self.assertEqual(codes[:3], [200, 200, 200])
            self.assertEqual(codes[3], 429)

        with ServerFixture(rate_limit=0) as fixture:
            status, body = http("POST", fixture.base + "/v1/issue", token=ADMIN_TOKEN, raw_body=b"x" * (ls.MAX_BODY_BYTES + 1))
            self.assertEqual(status, 413)
            self.assertEqual(body["error"], "too_large")

    def test_unknown_route(self):
        with ServerFixture() as fixture:
            status, body = http("GET", fixture.base + "/nope")
            self.assertEqual(status, 404)


if __name__ == "__main__":
    unittest.main(verbosity=2)
