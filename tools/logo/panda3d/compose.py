"""Icônes Android à partir du rendu 3D du panda (render.png, 1024 × 1024, fond transparent).

Usage, depuis la racine du dépôt : python3 tools/logo/panda3d/compose.py [dossier d'aperçu]
- ic_launcher_foreground.png (icône adaptative, 108 dp) pour chaque densité ;
- ic_launcher_monochrome.png et ic_notification.png : la silhouette blanche du panda,
  avec les taches des yeux et le nez évidés pour qu'on le reconnaisse.
"""
import os, sys
from PIL import Image, ImageDraw

HERE = os.path.dirname(os.path.abspath(__file__))
RES = 'app/src/main/res'
OUT = sys.argv[1] if len(sys.argv) > 1 else '/tmp'
DENS = {'mdpi': 1, 'hdpi': 1.5, 'xhdpi': 2, 'xxhdpi': 3, 'xxxhdpi': 4}

src = Image.open(os.path.join(HERE, 'render.png')).convert('RGBA')

# Le panda seul (sans l'ombre très claire au sol) : boîte des pixels bien opaques
alpha = src.getchannel('A')
solid = alpha.point(lambda a: 255 if a > 140 else 0)
box = solid.getbbox()
fig = src.crop(box)

def foreground(px):
    """Le panda centré dans le carré de 108 dp, à 60 dp de haut (zone sûre : cercle de 66 dp)."""
    canvas = Image.new('RGBA', (px, px), (0, 0, 0, 0))
    h = round(px * 60 / 108)
    w = round(fig.width * h / fig.height)
    f = fig.resize((w, h), Image.LANCZOS)
    canvas.alpha_composite(f, ((px - w) // 2, round(px * 55 / 108) - h // 2))
    return canvas

def silhouette(img):
    """Silhouette blanche ; les parties noires dans la tête (taches, nez, bouche) deviennent transparentes."""
    rgb = img.convert('RGB')
    a = img.getchannel('A')
    w, h = img.size
    # la tête : boîte des pixels presque blancs dans la moitié haute
    head = Image.new('L', (w, h), 0)
    px, hd = rgb.load(), head.load()
    for y in range(h // 2 + h // 10):
        for x in range(w):
            r, g, b = px[x, y]
            if min(r, g, b) > 205 and max(r, g, b) - min(r, g, b) < 30 and a.getpixel((x, y)) > 200:
                hd[x, y] = 255
    hb = head.getbbox()
    out = Image.new('RGBA', (w, h), (255, 255, 255, 0))
    op = out.load()
    for y in range(h):
        for x in range(w):
            al = a.getpixel((x, y))
            if al < 140:
                continue
            r, g, b = px[x, y]
            dark = max(r, g, b) < 90
            inside = hb and hb[0] + (hb[2] - hb[0]) * 0.08 < x < hb[2] - (hb[2] - hb[0]) * 0.08 and \
                hb[1] + (hb[3] - hb[1]) * 0.25 < y < hb[3]
            if dark and inside:
                continue
            op[x, y] = (255, 255, 255, 255)
    return out

big = foreground(432)
mono_big = silhouette(big)
for d, k in DENS.items():
    os.makedirs(f'{RES}/drawable-{d}', exist_ok=True)
    s = round(108 * k)
    big.resize((s, s), Image.LANCZOS).save(f'{RES}/drawable-{d}/ic_launcher_foreground.png', optimize=True)
    mono_big.resize((s, s), Image.LANCZOS).save(f'{RES}/drawable-{d}/ic_launcher_monochrome.png', optimize=True)
    # notification : 24 dp, le panda occupe la zone sûre (66 dp du centre de 108)
    n = round(24 * k)
    crop = mono_big.crop((round(432 * 21 / 108), round(432 * 21 / 108), round(432 * 87 / 108), round(432 * 87 / 108)))
    crop.resize((n, n), Image.LANCZOS).save(f'{RES}/drawable-{d}/ic_notification.png', optimize=True)

# Aperçu : sur le fond encre, masqué en rond et en carré arrondi, et la silhouette
os.makedirs(OUT, exist_ok=True)
bg = Image.new('RGBA', (432, 432))
for y in range(432):
    t = y / 432
    a0, b0 = (38, 42, 76), (14, 15, 30)
    bg.paste(tuple(int(a0[i] + (b0[i] - a0[i]) * t) for i in range(3)) + (255,), (0, y, 432, y + 1))
full = bg.copy(); full.alpha_composite(big)
vis = full.crop((72, 72, 360, 360))  # ce qu'un lanceur montre (72 dp sur 108)
sheet = Image.new('RGB', (288 * 3 + 80, 288 + 40), (233, 233, 239))
for i, shape in enumerate(['circle', 'squircle']):
    m = Image.new('L', (288, 288), 0)
    dr = ImageDraw.Draw(m)
    if shape == 'circle':
        dr.ellipse((0, 0, 287, 287), fill=255)
    else:
        dr.rounded_rectangle((0, 0, 287, 287), radius=64, fill=255)
    sheet.paste(vis, (20 + i * 308, 20), m)
mono_prev = Image.new('RGBA', (288, 288), (40, 40, 48, 255))
mono_prev.alpha_composite(mono_big.crop((72, 72, 360, 360)))
sheet.paste(mono_prev.convert('RGB'), (20 + 2 * 308, 20))
sheet.save(os.path.join(OUT, 'icone_panda.png'))
print('ok', box)
