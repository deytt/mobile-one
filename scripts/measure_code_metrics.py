#!/usr/bin/env python3
"""Mede compartilhamento de código e inventário de testes do mobile-one.

Uso:
  python3 scripts/measure_code_metrics.py

Critérios alinhados ao relatório técnico em docs/confluence/04-evidencias-e-metricas.md:
  - Conta .kt / .swift / .sq (produção). commonTest é reportado à parte.
  - "code" = linhas não-vazias sem comentários // e /* */.
  - "physical" = todas as linhas do arquivo (inclui comentários/blank).
"""

from __future__ import annotations

import re
import xml.etree.ElementTree as ET
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]

EXTS = {".kt", ".swift", ".sq", ".sql"}


def strip_line_comments(line: str) -> str:
    in_str = False
    out: list[str] = []
    i = 0
    while i < len(line):
        c = line[i]
        if c == '"' and (i == 0 or line[i - 1] != "\\"):
            in_str = not in_str
            out.append(c)
        elif not in_str and c == "/" and i + 1 < len(line) and line[i + 1] == "/":
            break
        else:
            out.append(c)
        i += 1
    return "".join(out)


def code_lines(text: str, sql: bool = False) -> int:
    if sql:
        n = 0
        for line in text.splitlines():
            if "--" in line:
                line = line.split("--", 1)[0]
            if line.strip():
                n += 1
        return n
    text = re.sub(r"/\*.*?\*/", "", text, flags=re.S)
    n = 0
    for line in text.splitlines():
        line = strip_line_comments(line)
        if line.strip():
            n += 1
    return n


def iter_sources(root: Path):
    if not root.exists():
        return
    for path in root.rglob("*"):
        if not path.is_file() or path.suffix.lower() not in EXTS:
            continue
        if any(p in {"build", ".git", ".gradle"} for p in path.parts):
            continue
        yield path


def summarize(root: Path) -> dict:
    files = list(iter_sources(root))
    physical = 0
    code = 0
    for path in files:
        text = path.read_text(encoding="utf-8", errors="ignore")
        physical += len(text.splitlines())
        code += code_lines(text, sql=path.suffix.lower() in {".sq", ".sql"})
    return {"files": len(files), "physical": physical, "code": code}


def parse_tests() -> dict | None:
    results = ROOT / "shared/build/test-results/testDebugUnitTest"
    if not results.exists():
        return None
    tests = failures = errors = skipped = 0
    files = list(results.glob("TEST-*.xml"))
    for path in files:
        suite = ET.parse(path).getroot()
        tests += int(suite.attrib.get("tests", 0))
        failures += int(suite.attrib.get("failures", 0))
        errors += int(suite.attrib.get("errors", 0))
        skipped += int(suite.attrib.get("skipped", 0))
    return {
        "files": len(files),
        "tests": tests,
        "failures": failures,
        "errors": errors,
        "skipped": skipped,
    }


def main() -> None:
    buckets = {
        "commonMain": ROOT / "shared/src/commonMain",
        "androidMain": ROOT / "shared/src/androidMain",
        "iosMain": ROOT / "shared/src/iosMain",
        "commonTest": ROOT / "shared/src/commonTest",
        "androidApp": ROOT / "androidApp/src",
        "iosApp": ROOT / "iosApp/iosApp",
    }
    stats = {name: summarize(path) for name, path in buckets.items()}

    cm = stats["commonMain"]
    am = stats["androidMain"]
    im = stats["iosMain"]
    aa = stats["androidApp"]
    ia = stats["iosApp"]
    ct = stats["commonTest"]

    shared_code = cm["code"] + am["code"] + im["code"]
    total_code = shared_code + aa["code"] + ia["code"]
    shared_phys = cm["physical"] + am["physical"] + im["physical"]
    total_phys = shared_phys + aa["physical"] + ia["physical"]

    usecases = list(
        (ROOT / "shared/src/commonMain/kotlin/com/mobileone/shared/domain/usecase").glob(
            "*.kt"
        )
    )
    usecase_tests = {
        p.stem.replace("Test", "")
        for p in (
            ROOT / "shared/src/commonTest/kotlin/com/mobileone/shared/domain/usecase"
        ).glob("*Test.kt")
    }
    usecases_tested = sum(1 for u in usecases if u.stem in usecase_tests)

    print("=== LOC (code = sem blank/comentários) ===")
    for name, s in stats.items():
        print(f"{name:12} files={s['files']:3}  code={s['code']:5}  physical={s['physical']:5}")

    print("\n=== Compartilhamento ===")
    print(f"shared module code:     {shared_code}")
    print(f"total produção code:    {total_code}")
    print(f"% módulo shared/total:  {shared_code / total_code * 100:.1f}%")
    print(f"% commonMain/total:     {cm['code'] / total_code * 100:.1f}%")
    print(f"% commonMain/shared:    {cm['code'] / shared_code * 100:.1f}%")
    print(f"shared module physical: {shared_phys}")
    print(f"total physical:         {total_phys}")
    print(f"% shared physical:      {shared_phys / total_phys * 100:.1f}%")

    print("\n=== Use cases ===")
    print(f"com teste dedicado: {usecases_tested}/{len(usecases)}")

    print("\n=== Testes (último :shared:testDebugUnitTest) ===")
    parsed = parse_tests()
    if parsed is None:
        print("Sem XML em shared/build/test-results — rode ./gradlew :shared:allTests")
    else:
        print(
            f"{parsed['tests']} testes · "
            f"{parsed['failures']} falhas · "
            f"{parsed['errors']} erros · "
            f"{parsed['files']} classes"
        )
        print(f"commonTest LOC code: {ct['code']}")


if __name__ == "__main__":
    main()
