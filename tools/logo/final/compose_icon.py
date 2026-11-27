"""Icône Android à partir du logo final fourni par le propriétaire (zhuzi_logo.jpg, 1024 × 1024).

Usage, depuis la racine du dépôt : python3 tools/logo/final/compose_icon.py [dossier d'aperçu]
- avant-plan (drawable-*dpi/ic_launcher_foreground.png) : le logo sans son cadre doré, bords fondus,
  réduit pour tenir dans la zone visible des icônes rondes et carrées (panda, bambou et « zhúzi » entiers) ;
- fond (drawable/ic_launcher_background.xml) : dégradé bleu nuit du logo ;
- l'icône monochrome et celle de la notification restent la silhouette du panda.
"""
import os, sys
from PIL import Image, ImageDraw, ImageFilter

HERE = os.path.dirname(os.path.abspath(__file__))
RES = 'app/src/main/res'
OUT = sys.argv[1] if len(sys.argv) > 1 else '/tmp'
DENS = {'mdpi': 1, 'hdpi': 1.5, 'xhdpi': 2, 'xxhdpi': 3, 'xxxhdpi': 4}

src = Image.open(os.path.join(HERE, 'zhuzi_logo.jpg')).convert('RGB')
art = src.crop((52, 52, 972, 972))          # sans le cadre doré ni les coins blancs
n = art.width
# masque : carré très arrondi, bords fondus, pour se fondre dans le fond bleu nuit
mask = Image.new('L', (n, n), 0)
ImageDraw.Draw(mask).rounded_rectangle((40, 40, n - 40, n - 40), radius=n // 4, fill=255)
mask = mask.filter(ImageFilter.GaussianBlur(30))
art = art.convert('RGBA'); art.putalpha(mask)

ART_DP = 70   # taille du logo dans le carré de 108 dp de l'icône adaptative
def foreground(px):
    c = Image.new('RGBA', (px, px), (0, 0, 0, 0))
    s = round(px * ART_DP / 108)
    c.alpha_composite(art.resize((s, s), Image.LANCZOS), ((px - s) // 2, (px - s) // 2))
    return c

for d, k in DENS.items():
    os.makedirs(f'{RES}/drawable-{d}', exist_ok=True)
    s = round(108 * k)
    foreground(s).save(f'{RES}/drawable-{d}/ic_launcher_foreground.png', optimize=True)

open(f'{RES}/drawable/ic_launcher_background.xml', 'w', encoding='utf-8').write('''<?xml version="1.0" encoding="utf-8"?>
<!-- Fabriqué par tools/logo/final/compose_icon.py : fond bleu nuit du logo -->
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:aapt="http://schemas.android.com/aapt"
    android:width="108dp" android:height="108dp"
    android:viewportWidth="108" android:viewportHeight="108">
    <path android:pathData="M0,0h108v108h-108Z">
        <aapt:attr name="android:fillColor">
            <gradient android:type="radial" android:centerX="54" android:centerY="44" android:gradientRadius="76">
                <item android:offset="0" android:color="#FF1C4572"/>
                <item android:offset="0.55" android:color="#FF0B2448"/>
                <item android:offset="1" android:color="#FF020C22"/>
            </gradient>
        </aapt:attr>
    </path>
</vector>
''')

# Aperçu : fond + avant-plan, vu en rond et en carré arrondi (zone visible : 72 dp sur 108)
P = 432
bg = Image.new('RGBA', (P, P))
import math
for y in range(P):
    for x in range(P):
        r = math.hypot(x - P / 2, y - P * 44 / 108) / (P * 76 / 108)
        stops = [(0, (28, 69, 114)), (0.55, (11, 36, 72)), (1, (2, 12, 34))]
        r = min(r, 1)
        for (a, ca), (b, cb) in zip(stops, stops[1:]):
            if a <= r <= b:
                t = (r - a) / (b - a)
                bg.putpixel((x, y), tuple(int(ca[i] + (cb[i] - ca[i]) * t) for i in range(3)) + (255,))
                break
full = bg.copy(); full.alpha_composite(foreground(P))
vis = full.crop((72, 72, 360, 360))
sheet = Image.new('RGB', (288 * 2 + 60, 328), (233, 233, 239))
for i, shape in enumerate(['circle', 'squircle']):
    m = Image.new('L', (288, 288), 0)
    dr = ImageDraw.Draw(m)
    (dr.ellipse if shape == 'circle' else lambda b, fill: dr.rounded_rectangle(b, radius=64, fill=fill))((0, 0, 287, 287), fill=255)
    sheet.paste(vis, (20 + i * 308, 20), m)
os.makedirs(OUT, exist_ok=True)
sheet.save(os.path.join(OUT, 'icone_finale.png'))
print('ok')
