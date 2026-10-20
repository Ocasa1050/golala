#!/usr/bin/env python3
"""Replace the armeabi-v7a native library in an APK copy."""

from __future__ import annotations

import argparse
import struct
import sys
import zipfile
from pathlib import Path


APK_LIBRARY_PATH = "lib/armeabi-v7a/libsampvoice.so"
ELF_MACHINE_ARM = 40


def validate_arm_library(library: bytes) -> None:
    if len(library) < 20 or library[:4] != b"\x7fELF":
        raise ValueError("The built native library is not a valid ELF file.")
    if library[4] != 1 or library[5] != 1:
        raise ValueError("The native library must be 32-bit, little-endian ELF.")
    machine = struct.unpack_from("<H", library, 18)[0]
    if machine != ELF_MACHINE_ARM:
        raise ValueError(
            f"The native library has ELF machine {machine}; expected ARM (40)."
        )


def is_old_signature_entry(name: str) -> bool:
    upper_name = name.upper()
    prefix = "META-INF/"
    if not upper_name.startswith(prefix):
        return False

    leaf = upper_name[len(prefix) :]
    if "/" in leaf:
        return False
    return leaf == "MANIFEST.MF" or leaf.endswith((".SF", ".RSA", ".DSA", ".EC"))


def replace_native_library(
    apk_path: Path, library_path: Path, output_path: Path
) -> None:
    if not apk_path.is_file():
        raise FileNotFoundError(f"Base APK not found: {apk_path}")
    if not library_path.is_file():
        raise FileNotFoundError(f"Built native library not found: {library_path}")
    if output_path.resolve() in {apk_path.resolve(), library_path.resolve()}:
        raise ValueError("Output must be a new file; input files are left unchanged.")

    library = library_path.read_bytes()
    validate_arm_library(library)
    output_path.parent.mkdir(parents=True, exist_ok=True)

    try:
        with zipfile.ZipFile(apk_path, "r") as source:
            if source.testzip() is not None:
                raise ValueError("The base APK contains a damaged ZIP entry.")
            entries = source.infolist()
            matches = [entry for entry in entries if entry.filename == APK_LIBRARY_PATH]
            if len(matches) != 1:
                raise ValueError(
                    f"Expected exactly one {APK_LIBRARY_PATH} entry; found {len(matches)}."
                )
            if "AndroidManifest.xml" not in source.namelist():
                raise ValueError("The base APK is missing AndroidManifest.xml.")

            original_library_info = matches[0]
            with zipfile.ZipFile(output_path, "w", allowZip64=True) as output:
                for entry in entries:
                    if entry.filename == APK_LIBRARY_PATH or is_old_signature_entry(
                        entry.filename
                    ):
                        continue
                    output.writestr(entry, source.read(entry))

                replacement = zipfile.ZipInfo(
                    APK_LIBRARY_PATH, date_time=original_library_info.date_time
                )
                replacement.compress_type = zipfile.ZIP_STORED
                replacement.external_attr = original_library_info.external_attr
                replacement.create_system = original_library_info.create_system
                output.writestr(replacement, library)

        with zipfile.ZipFile(output_path, "r") as rebuilt:
            if rebuilt.testzip() is not None:
                raise ValueError("The repackaged APK failed ZIP integrity checks.")
            if rebuilt.namelist().count(APK_LIBRARY_PATH) != 1:
                raise ValueError("The rebuilt APK has an unexpected native-library count.")
            if rebuilt.read(APK_LIBRARY_PATH) != library:
                raise ValueError("The rebuilt APK does not contain the new library.")
            if any(is_old_signature_entry(name) for name in rebuilt.namelist()):
                raise ValueError("Old APK v1 signature files were not removed.")
    except Exception:
        output_path.unlink(missing_ok=True)
        raise

    print(
        f"Replaced {APK_LIBRARY_PATH} in a copy of {apk_path.name}; "
        "the original APK was left unchanged."
    )


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--apk", type=Path, required=True)
    parser.add_argument("--library", type=Path, required=True)
    parser.add_argument("--output", type=Path, required=True)
    args = parser.parse_args()

    try:
        replace_native_library(args.apk, args.library, args.output)
    except (OSError, ValueError, zipfile.BadZipFile) as error:
        print(f"APK repackaging failed: {error}", file=sys.stderr)
        return 1
    return 0


if __name__ == "__main__":
    sys.exit(main())
