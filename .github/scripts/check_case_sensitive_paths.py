#!/usr/bin/env python3
"""Fail when tracked native sources refer to local paths with wrong casing."""

from __future__ import annotations

import re
import subprocess
import sys
from pathlib import Path


ROOT = Path(__file__).resolve().parents[2]
SOURCE_SUFFIXES = {".c", ".cc", ".cpp", ".h", ".hh", ".hpp"}
SEARCH_ROOTS = [
    ROOT,
    ROOT / "jni",
    ROOT / "jni" / "vendor",
    ROOT / "santrope-tea-gtasa",
]
SEARCH_ROOTS.extend(path for path in (ROOT / "jni" / "vendor").glob("*") if path.is_dir())


def tracked_paths() -> list[str]:
    result = subprocess.run(
        ["git", "-C", str(ROOT), "ls-files", "-z"],
        check=True,
        capture_output=True,
    )
    return [path.decode() for path in result.stdout.split(b"\0") if path]


def build_casefold_index(paths: list[str]) -> dict[str, list[str]]:
    index: dict[str, list[str]] = {}
    for path in paths:
        index.setdefault(path.casefold(), []).append(path)
    return index


def relative_key(path: Path) -> str | None:
    try:
        return path.resolve().relative_to(ROOT).as_posix()
    except ValueError:
        return None


def report_case_mismatch(
    requested: Path,
    casefold_index: dict[str, list[str]],
    errors: list[str],
    context: str,
) -> None:
    if requested.exists():
        return

    key = relative_key(requested)
    if key is None:
        return
    alternatives = casefold_index.get(key.casefold(), [])
    if alternatives:
        errors.append(
            f"{context}: requested '{key}', but repository contains "
            + ", ".join(f"'{alternative}'" for alternative in alternatives)
        )


def check_includes(paths: list[str], casefold_index: dict[str, list[str]]) -> list[str]:
    errors: list[str] = []
    include_pattern = re.compile(r'^\s*#\s*include\s*"([^"]+)"')

    for relative_path in paths:
        source = ROOT / relative_path
        if source.suffix.lower() not in SOURCE_SUFFIXES or not source.is_file():
            continue

        try:
            lines = source.read_text(encoding="utf-8", errors="replace").splitlines()
        except OSError:
            continue

        for line_number, line in enumerate(lines, start=1):
            match = include_pattern.match(line)
            if not match:
                continue

            include = match.group(1)
            candidates = [(source.parent / include).resolve()]
            candidates.extend((search_root / include).resolve() for search_root in SEARCH_ROOTS)
            if any(candidate.is_file() for candidate in candidates):
                continue

            for candidate in candidates:
                before = len(errors)
                report_case_mismatch(
                    candidate,
                    casefold_index,
                    errors,
                    f"{relative_path}:{line_number}",
                )
                if len(errors) > before:
                    break

    return errors


def check_android_makefiles(
    paths: list[str], casefold_index: dict[str, list[str]]
) -> list[str]:
    errors: list[str] = []
    local_path_pattern = re.compile(r"\$\(LOCAL_PATH\)/([^\s)]+)")
    source_file_pattern = re.compile(r"^\s*LOCAL_SRC_FILES\s*:=\s*([^\s$]+)")

    for relative_path in paths:
        if not relative_path.endswith(".mk"):
            continue

        makefile = ROOT / relative_path
        try:
            lines = makefile.read_text(encoding="utf-8", errors="replace").splitlines()
        except OSError:
            continue

        for line_number, line in enumerate(lines, start=1):
            for match in local_path_pattern.finditer(line):
                requested = match.group(1)
                wildcard_index = min(
                    (index for index in (requested.find("*"), requested.find("?")) if index >= 0),
                    default=-1,
                )
                if wildcard_index >= 0:
                    requested = requested[:wildcard_index].rstrip("/")
                if not requested:
                    continue

                candidate = (makefile.parent / requested).resolve()
                report_case_mismatch(
                    candidate,
                    casefold_index,
                    errors,
                    f"{relative_path}:{line_number}",
                )

            match = source_file_pattern.match(line)
            if match:
                candidate = (makefile.parent / match.group(1)).resolve()
                report_case_mismatch(
                    candidate,
                    casefold_index,
                    errors,
                    f"{relative_path}:{line_number}",
                )

    return errors


def main() -> int:
    paths = tracked_paths()
    casefold_index = build_casefold_index(paths)
    errors = check_includes(paths, casefold_index)
    errors.extend(check_android_makefiles(paths, casefold_index))

    if errors:
        print("Case-sensitive path errors detected:")
        for error in errors:
            print(f" - {error}")
        return 1

    print("Case-sensitive path check passed.")
    return 0


if __name__ == "__main__":
    sys.exit(main())
