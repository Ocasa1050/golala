#!/usr/bin/env python3
"""Replace the armeabi-v7a sampvoice library in the checked-in base APK."""

from __future__ import annotations

import argparse
import sys
import zipfile
from pathlib import Path


APK_LIBRARY_PATH = "lib/armeabi-v7a/libsampvoice.so"


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

            with zipfile.ZipFile(output_path, "w", allowZip64=True) as output:
                for info in entries:
                    if info.filename == APK_LIBRARY_PATH or is_old_v1_signature(info.filename):
                        continue
                    output.writestr(info, source.read(info))

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
    except Exception:
        output_path.unlink(missing_ok=True)
        raise

    print(
        f"Replaced {APK_LIBRARY_PATH} in a copy of {apk_path.name}; "
        f"the original APK was left unchanged."
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
