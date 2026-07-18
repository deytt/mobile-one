#!/usr/bin/env python3
"""Gera ícones do app (Banco Principal) para iOS e Android.

iOS: PNG 1024×1024 RGB sem canal alpha (HIG / App Store Connect).
Android: adaptive icon (foreground + background) + mipmaps legados.
"""

from __future__ import annotations

from pathlib import Path

from PIL import Image, ImageDraw, ImageFont

# Tokens — Banco Principal (design system)
PRIMARY = (0x00, 0x3B, 0x6F)  # #003B6F
ON_PRIMARY = (0xFF, 0xFF, 0xFF)
# Equivalente opaco de branco @ 18% sobre primary (logo plate do BrandLogo)
PLATE = (
    int(PRIMARY[0] * 0.82 + 255 * 0.18),
    int(PRIMARY[1] * 0.82 + 255 * 0.18),
    int(PRIMARY[2] * 0.82 + 255 * 0.18),
)

ROOT = Path(__file__).resolve().parents[1]
IOS_ICONSET = ROOT / "iosApp/iosApp/Assets.xcassets/AppIcon.appiconset"
ANDROID_RES = ROOT / "androidApp/src/main/res"
WORKDIR = ROOT / "scripts/.icon-work"


def rounded_rect_mask(size: int, radius: float) -> Image.Image:
    mask = Image.new("L", (size, size), 0)
    draw = ImageDraw.Draw(mask)
    draw.rounded_rectangle((0, 0, size - 1, size - 1), radius=radius, fill=255)
    return mask


def load_font(size: int) -> ImageFont.FreeTypeFont | ImageFont.ImageFont:
    candidates = [
        "/System/Library/Fonts/Supplemental/Arial Bold.ttf",
        "/System/Library/Fonts/Supplemental/Arial.ttf",
        "/Library/Fonts/Arial Bold.ttf",
        "/System/Library/Fonts/Helvetica.ttc",
        "/System/Library/Fonts/SFNSRounded.ttf",
    ]
    for path in candidates:
        try:
            return ImageFont.truetype(path, size=size)
        except OSError:
            continue
    return ImageFont.load_default()


def draw_bp_mark(
    canvas: Image.Image,
    *,
    mark_size: int,
    include_plate: bool,
    plate_alpha_ok: bool,
) -> None:
    """Desenha o mark BP centrado (proporção do BrandLogo 64dp → 24sp)."""
    w, h = canvas.size
    cx, cy = w // 2, h // 2
    left = cx - mark_size // 2
    top = cy - mark_size // 2
    radius = mark_size * (12 / 64)  # borderRadiusDp = 12

    if include_plate:
        if plate_alpha_ok and canvas.mode == "RGBA":
            plate = Image.new("RGBA", (mark_size, mark_size), (0, 0, 0, 0))
            pd = ImageDraw.Draw(plate)
            # branco 18% — ok em Android foreground (alpha permitido)
            pd.rounded_rectangle(
                (0, 0, mark_size - 1, mark_size - 1),
                radius=radius,
                fill=(255, 255, 255, int(255 * 0.18)),
            )
            canvas.alpha_composite(plate, (left, top))
        else:
            # iOS / ícone opaco: plate pré-misturado, sem alpha
            plate = Image.new("RGB", (mark_size, mark_size), PLATE)
            mask = rounded_rect_mask(mark_size, radius)
            canvas.paste(plate, (left, top), mask)

    # Texto BP
    font_size = max(8, int(mark_size * 24 / 64))
    font = load_font(font_size)
    draw = ImageDraw.Draw(canvas)
    text = "BP"
    bbox = draw.textbbox((0, 0), text, font=font)
    tw, th = bbox[2] - bbox[0], bbox[3] - bbox[1]
    # Ajuste óptico vertical (baseline)
    tx = cx - tw // 2 - bbox[0]
    ty = cy - th // 2 - bbox[1] - int(font_size * 0.04)
    draw.text((tx, ty), text, font=font, fill=ON_PRIMARY)


def make_ios_icon(size: int = 1024) -> Image.Image:
    """App Store / Xcode: RGB, sem alpha, full-bleed, sem cantos arredondados."""
    img = Image.new("RGB", (size, size), PRIMARY)
    # Safe zone ~80%: mark ocupa ~62% do canvas (legível sob máscara iOS)
    mark = int(size * 0.62)
    draw_bp_mark(img, mark_size=mark, include_plate=True, plate_alpha_ok=False)
    return img


def make_android_background(size: int = 1080) -> Image.Image:
    """Camada background do adaptive icon — cor sólida primary."""
    return Image.new("RGB", (size, size), PRIMARY)


def make_android_foreground(size: int = 1080) -> Image.Image:
    """Camada foreground — transparente nas bordas; safe zone ~66% central."""
    img = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    # Keyline Android: conteúdo importante dentro de ~66dp de 108dp ≈ 61%
    mark = int(size * 0.52)
    draw_bp_mark(img, mark_size=mark, include_plate=True, plate_alpha_ok=True)
    return img


def make_legacy_launcher(size: int) -> Image.Image:
    """mipmap legado: ícone completo com fundo primary (RGB)."""
    img = Image.new("RGB", (size, size), PRIMARY)
    mark = int(size * 0.62)
    draw_bp_mark(img, mark_size=mark, include_plate=True, plate_alpha_ok=False)
    return img


def save_rgb_no_alpha(img: Image.Image, path: Path) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    rgb = img.convert("RGB")
    # Garante ausência de canal alpha no arquivo
    rgb.save(path, format="PNG", optimize=True)
    # Verificação
    check = Image.open(path)
    if check.mode in ("RGBA", "LA", "PA") or "A" in check.getbands():
        raise RuntimeError(f"Alpha detectado em {path} (mode={check.mode})")


def write_ios_contents_json(iconset: Path) -> None:
    (iconset / "Contents.json").write_text(
        """{
  "images" : [
    {
      "filename" : "AppIcon-1024.png",
      "idiom" : "universal",
      "platform" : "ios",
      "size" : "1024x1024"
    }
  ],
  "info" : {
    "author" : "xcode",
    "version" : 1
  }
}
""",
        encoding="utf-8",
    )


def write_android_adaptive_xml() -> None:
    anydpi = ANDROID_RES / "mipmap-anydpi-v26"
    anydpi.mkdir(parents=True, exist_ok=True)
    for name in ("ic_launcher.xml", "ic_launcher_round.xml"):
        (anydpi / name).write_text(
            """<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@color/ic_launcher_background"/>
    <foreground android:drawable="@drawable/ic_launcher_foreground"/>
</adaptive-icon>
""",
            encoding="utf-8",
        )


def write_android_color() -> None:
    values = ANDROID_RES / "values"
    values.mkdir(parents=True, exist_ok=True)
    colors = values / "colors.xml"
    content = """<?xml version="1.0" encoding="utf-8"?>
<resources>
    <color name="ic_launcher_background">#003B6F</color>
</resources>
"""
    if colors.exists():
        text = colors.read_text(encoding="utf-8")
        if "ic_launcher_background" not in text:
            text = text.replace(
                "</resources>",
                '    <color name="ic_launcher_background">#003B6F</color>\n</resources>',
            )
            colors.write_text(text, encoding="utf-8")
    else:
        colors.write_text(content, encoding="utf-8")


def write_android_foreground_png() -> None:
    """Foreground PNG em nodpi (108dp @ 4x = 432px) — alpha permitido no adaptive."""
    drawable = ANDROID_RES / "drawable-nodpi"
    drawable.mkdir(parents=True, exist_ok=True)
    fg = make_android_foreground(432)
    fg.save(drawable / "ic_launcher_foreground.png", format="PNG", optimize=True)


def write_mipmap_pngs() -> None:
    densities = {
        "mipmap-mdpi": 48,
        "mipmap-hdpi": 72,
        "mipmap-xhdpi": 96,
        "mipmap-xxhdpi": 144,
        "mipmap-xxxhdpi": 192,
    }
    for folder, size in densities.items():
        out_dir = ANDROID_RES / folder
        out_dir.mkdir(parents=True, exist_ok=True)
        icon = make_legacy_launcher(size)
        save_rgb_no_alpha(icon, out_dir / "ic_launcher.png")
        save_rgb_no_alpha(icon, out_dir / "ic_launcher_round.png")


def main() -> None:
    WORKDIR.mkdir(parents=True, exist_ok=True)

    # --- iOS ---
    IOS_ICONSET.mkdir(parents=True, exist_ok=True)
    ios = make_ios_icon(1024)
    ios_path = IOS_ICONSET / "AppIcon-1024.png"
    save_rgb_no_alpha(ios, ios_path)
    write_ios_contents_json(IOS_ICONSET)

    # Assets catalog root
    catalog = ROOT / "iosApp/iosApp/Assets.xcassets/Contents.json"
    if not catalog.exists():
        catalog.write_text(
            """{
  "info" : {
    "author" : "xcode",
    "version" : 1
  }
}
""",
            encoding="utf-8",
        )

    # Preview opcional
    save_rgb_no_alpha(ios, WORKDIR / "preview-ios-1024.png")

    # --- Android ---
    write_android_color()
    write_android_foreground_png()
    write_android_adaptive_xml()
    write_mipmap_pngs()

    # Previews Android
    save_rgb_no_alpha(make_android_background(432), WORKDIR / "preview-android-bg.png")
    fg = make_android_foreground(432)
    fg.save(WORKDIR / "preview-android-fg.png", format="PNG")

    print("OK")
    print(f"  iOS: {ios_path}")
    print(f"  Android adaptive + mipmaps em {ANDROID_RES}")
    print(f"  Previews: {WORKDIR}")
    print(f"  Plate color (opaque): #{PLATE[0]:02X}{PLATE[1]:02X}{PLATE[2]:02X}")


if __name__ == "__main__":
    main()
