#!/usr/bin/env python3
"""Validate the built APK and derive GitHub Release assets from Gradle output."""
from __future__ import annotations

import hashlib
import json
import os
from pathlib import Path
import re
import shutil
import zipfile

REPO = Path(__file__).resolve().parents[2]
OUTPUT = REPO / "android/app/build/outputs/apk/debug"
METADATA = OUTPUT / "output-metadata.json"


def prepare() -> tuple[str, str]:
    metadata = json.loads(METADATA.read_text(encoding="utf-8"))
    if metadata.get("applicationId") != "com.plan.app" or metadata.get("variantName") != "debug":
        raise ValueError("Unexpected Android APK application/variant")
    elements = metadata.get("elements", [])
    if len(elements) != 1:
        raise ValueError("Expected exactly one debug APK")
    version = elements[0].get("versionName", "")
    if not re.fullmatch(r"[0-9]+\.[0-9]+\.[0-9]+", version):
        raise ValueError(f"Invalid release version: {version!r}")

    source = OUTPUT / elements[0]["outputFile"]
    if not source.is_file() or source.stat().st_size == 0:
        raise ValueError(f"Missing or empty APK: {source}")
    with zipfile.ZipFile(source) as archive:
        broken = archive.testzip()
        if broken is not None:
            raise ValueError(f"Corrupt APK entry: {broken}")

    asset = f"Plan-v{version}-debug.apk"
    dist = REPO / "dist"
    dist.mkdir(exist_ok=True)
    target = dist / asset
    shutil.copy2(source, target)
    digest = hashlib.sha256(target.read_bytes()).hexdigest()
    (dist / f"{asset}.sha256").write_text(f"{digest}  {asset}\n", encoding="utf-8")
    environment = os.getenv("GITHUB_ENV")
    if environment:
        with open(environment, "a", encoding="utf-8") as output:
            output.write(f"RELEASE_VERSION={version}\nRELEASE_APK={asset}\n")

    print(f"Verified {asset} SHA-256={digest}")
    return version, asset


if __name__ == "__main__":
    prepare()
