#!/usr/bin/env python3
"""Patch the server-list URL and replace the armeabi-v7a library in base.apk."""

from __future__ import annotations

import argparse
import hashlib
import struct
import sys
import zipfile
import zlib
from pathlib import Path


APK_LIBRARY_PATH = "lib/armeabi-v7a/libsampvoice.so"
OLD_SERVER_LIST_URL = (
    "https://www.dropbox.com/scl/fi/5g1u4oe6d6b390flvz3f3/servers.json"
    "?rlkey=wlgo6xvkpznmo6vqwi6r5skmu&st=unlvk44n&dl=1"
)
NEW_SERVER_LIST_URL = (
    "https://www.dropbox.com/scl/fi/unjvhzfg8ku8gr6ygti7w/servers.json"
    "?rlkey=rd5bxs1vg7xrn20sgvkl4mm35&st=47aygr92&dl=1"
)


def is_dex_entry(name: str) -> bool:
    return name == "classes.dex" or (
        name.startswith("classes") and name.endswith(".dex")
    )


def patch_server_list_url(dex_bytes: bytes) -> bytes:
    if not dex_bytes.startswith(b"dex\n") or len(dex_bytes) < 0x70:
        raise ValueError("The APK contains an invalid DEX file.")

    old_url = OLD_SERVER_LIST_URL.encode("ascii")
    new_url = NEW_SERVER_LIST_URL.encode("ascii")
    if len(old_url) != len(new_url):
        raise ValueError("The replacement server-list URL must have the same byte length.")

    matches = dex_bytes.count(old_url)
    if matches != 1:
        raise ValueError(
            f"Expected exactly one old server-list URL in DEX; found {matches}."
        )

    patched = bytearray(dex_bytes)
    url_offset = patched.find(old_url)
    patched[url_offset : url_offset + len(old_url)] = new_url

    # DEX stores its own SHA-1 signature and Adler-32 checksum in the header.
    patched[12:32] = hashlib.sha1(patched[32:]).digest()
    struct.pack_into("<I", patched, 8, zlib.adler32(patched[12:]) & 0xFFFFFFFF)
    return bytes(patched)


def has_valid_dex_integrity(dex_bytes: bytes) -> bool:
    if not dex_bytes.startswith(b"dex\n") or len(dex_bytes) < 0x70:
        return False

    expected_signature = hashlib.sha1(dex_bytes[32:]).digest()
    expected_checksum = zlib.adler32(dex_bytes[12:]) & 0xFFFFFFFF
    actual_checksum = struct.unpack_from("<I", dex_bytes, 8)[0]
    return dex_bytes[12:32] == expected_signature and actual_checksum == expected_checksum


def is_old_v1_signature(name: str) -> bool:
    upper_name = name.upper()
    prefix = "META-INF/"
    if not upper_name.startswith(prefix):
        return False

    leaf = upper_name[len(prefix) :]
    if "/" in leaf:
        return False
    return leaf == "MANIFEST.MF" or leaf.endswith((".SF", ".RSA", ".DSA", ".EC"))


def replace_library(apk_path: Path, library_path: Path, output_path: Path) -> None:
    if not apk_path.is_file():
        raise FileNotFoundError(f"APK not found: {apk_path}")
    if not library_path.is_file():
        raise FileNotFoundError(f"Built native library not found: {library_path}")
    if output_path.resolve() in {apk_path.resolve(), library_path.resolve()}:
        raise ValueError("Output must be a new file; the input APK is never modified.")

    library_bytes = library_path.read_bytes()
    old_library_info: zipfile.ZipInfo | None = None
    seen_target_count = 0
    output_path.parent.mkdir(parents=True, exist_ok=True)

    try:
        with zipfile.ZipFile(apk_path, "r") as source:
            if source.testzip() is not None:
                raise ValueError("The input APK contains a damaged ZIP entry.")

            entries = source.infolist()
            for info in entries:
                if info.filename == APK_LIBRARY_PATH:
                    old_library_info = info
                    seen_target_count += 1

            if seen_target_count != 1:
                raise ValueError(
                    f"Expected exactly one {APK_LIBRARY_PATH} entry in the APK; "
                    f"found {seen_target_count}."
                )
            if "AndroidManifest.xml" not in source.namelist():
                raise ValueError("The input ZIP is missing AndroidManifest.xml.")

            patched_dex_entries: dict[str, bytes] = {}
            old_url_matches = 0
            for info in entries:
                if not is_dex_entry(info.filename):
                    continue

                dex_bytes = source.read(info)
                old_url_matches += dex_bytes.count(OLD_SERVER_LIST_URL.encode("ascii"))
                if OLD_SERVER_LIST_URL.encode("ascii") in dex_bytes:
                    patched_dex_entries[info.filename] = patch_server_list_url(dex_bytes)

            if old_url_matches != 1:
                raise ValueError(
                    f"Expected exactly one old server-list URL across DEX files; "
                    f"found {old_url_matches}."
                )

            with zipfile.ZipFile(output_path, "w", allowZip64=True) as output:
                for info in entries:
                    if info.filename == APK_LIBRARY_PATH or is_old_v1_signature(info.filename):
                        continue
                    entry_bytes = patched_dex_entries.get(info.filename)
                    if entry_bytes is None:
                        entry_bytes = source.read(info)
                    output.writestr(info, entry_bytes)

                assert old_library_info is not None
                replacement_info = zipfile.ZipInfo(
                    APK_LIBRARY_PATH,
                    date_time=old_library_info.date_time,
                )
                replacement_info.compress_type = zipfile.ZIP_STORED
                replacement_info.external_attr = old_library_info.external_attr
                replacement_info.create_system = old_library_info.create_system
                output.writestr(replacement_info, library_bytes)

        with zipfile.ZipFile(output_path, "r") as result:
            if result.testzip() is not None:
                raise ValueError("The rebuilt unsigned APK failed ZIP integrity checks.")
            if result.read(APK_LIBRARY_PATH) != library_bytes:
                raise ValueError("The rebuilt APK does not contain the new native library.")
            if any(is_old_v1_signature(name) for name in result.namelist()):
                raise ValueError("Old APK v1 signature files were not fully removed.")
            for dex_name, expected_dex in patched_dex_entries.items():
                result_dex = result.read(dex_name)
                if result_dex != expected_dex or not has_valid_dex_integrity(result_dex):
                    raise ValueError(f"Server-list URL patch failed DEX verification: {dex_name}")
                if OLD_SERVER_LIST_URL.encode("ascii") in result_dex:
                    raise ValueError(f"Old server-list URL remains in {dex_name}.")
                if result_dex.count(NEW_SERVER_LIST_URL.encode("ascii")) != 1:
                    raise ValueError(f"New server-list URL was not found exactly once in {dex_name}.")
    except Exception:
        output_path.unlink(missing_ok=True)
        raise

    print(
        f"Updated the server-list URL and replaced {APK_LIBRARY_PATH} in a copy of "
        f"{apk_path.name}; the original APK was left unchanged."
    )


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--apk", type=Path, required=True)
    parser.add_argument("--library", type=Path, required=True)
    parser.add_argument("--output", type=Path, required=True)
    args = parser.parse_args()

    try:
        replace_library(args.apk, args.library, args.output)
    except (OSError, ValueError, zipfile.BadZipFile) as error:
        print(f"APK repackaging failed: {error}", file=sys.stderr)
        return 1
    return 0


if __name__ == "__main__":
    sys.exit(main())
