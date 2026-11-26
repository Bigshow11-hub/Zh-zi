"""ANCIEN LOGO (version 1.8, remplacé par le panda 3D en 1.8.2 : voir tools/logo/panda3d). Ne plus lancer :
il réécrirait les icônes vectorielles à la place du panda.

Logo de Zhúzi : deux tiges de bambou modelées (cylindre ombré, nœuds, feuilles) sur un fond encre,
signées d'un petit sceau rouge 子. Produit les icônes Android (vecteurs) et un aperçu SVG.

Usage, depuis la racine du dépôt : python3 tools/logo/build_logo.py [dossier d'aperçu]
Tout est dessiné dans la grille 108 × 108 de l'icône adaptative (zone sûre : cercle de 66 au centre).
"""
import math, subprocess, sys, os

RES = 'app/src/main/res'
OUT = sys.argv[1] if len(sys.argv) > 1 else '/tmp'

def n(v):
    return ('%.2f' % v).rstrip('0').rstrip('.')

# ——— Couleurs ———
BG_TOP, BG_BOT = '#FF20233F', '#FF0E0F1E'
JADE_DARK, JADE, JADE_LIGHT, JADE_SHINE = '#FF0E5A3A', '#FF23935F', '#FF5CC98E', '#FFB4EFCB'
NODE = '#FF0B4A30'
LEAF_BASE, LEAF_TIP = '#FF2FAE6E', '#FF136B44'
SEAL = '#FFC8321E'

shapes = []  # (path, fill) ; fill = couleur ou ('lin', x1, y1, x2, y2, [(offset, color)])

def rect(x, y, w, h, r=0):
    if r == 0:
        return f'M{n(x)},{n(y)}h{n(w)}v{n(h)}h{n(-w)}Z'
    return (f'M{n(x + r)},{n(y)}h{n(w - 2 * r)}q{n(r)},0 {n(r)},{n(r)}v{n(h - 2 * r)}q0,{n(r)} {n(-r)},{n(r)}'
            f'h{n(-(w - 2 * r))}q{n(-r)},0 {n(-r)},{n(-r)}v{n(-(h - 2 * r))}q0,{n(-r)} {n(r)},{n(-r)}Z')

def stalk(x, top, bottom, w, nodes, cut=0.0):
    """Une tige : segments cylindriques (dégradé horizontal), nœuds en anneaux plus sombres.
    cut : hauteur de la coupe en biais au sommet (0 = sommet arrondi)."""
    cyl = ('lin', x, 0, x + w, 0, [(0, JADE_DARK), (0.22, JADE), (0.40, JADE_LIGHT), (0.52, JADE), (1, JADE_DARK)])
    ys = [top] + nodes + [bottom]
    for i, (a, b) in enumerate(zip(ys, ys[1:])):
        if i == 0 and cut > 0:
            # sommet coupé en biais : on voit l'intérieur creux de la tige
            r = w * 0.18
            seg = (f'M{n(x)},{n(a + cut)}L{n(x + w)},{n(a)}L{n(x + w)},{n(b - 0.9 - r)}q0,{n(r)} {n(-r)},{n(r)}'
                   f'h{n(-(w - 2 * r))}q{n(-r)},0 {n(-r)},{n(-r)}Z')
            shapes.append((seg, cyl))
            # l'ouverture : une ellipse inclinée, foncée, bordée de clair
            cx, cy = x + w / 2, a + cut / 2
            ang = math.atan2(-cut, w)
            ux, uy = math.cos(ang), math.sin(ang)
            vx, vy = -uy, ux
            hw, hh = w / 2 / math.cos(ang) * 0.86, w * 0.17
            def pt(t):
                return cx + ux * hw * math.cos(t) + vx * hh * math.sin(t), cy + uy * hw * math.cos(t) + vy * hh * math.sin(t)
            pts = [pt(k * math.pi / 8) for k in range(16)]
            d = f'M{n(pts[0][0])},{n(pts[0][1])}' + ''.join(f'L{n(px)},{n(py)}' for px, py in pts[1:]) + 'Z'
            shapes.append((d, '#FF0A3A26'))
            continue
        # segment légèrement resserré en haut et en bas, comme un vrai entre-nœud
        shapes.append((rect(x, a + 0.9, w, b - a - 1.8, w * 0.18), cyl))
        # reflet fin et vertical
        shapes.append((rect(x + w * 0.30, a + (cut + 2 if i == 0 and cut > 0 else 2.5), w * 0.09,
                            max(0.0, b - a - (cut + 4.5 if i == 0 and cut > 0 else 5)), w * 0.04), JADE_SHINE + '|0.55'))
    for y in nodes:
        # le nœud : un anneau un peu plus large, foncé, avec un liseré clair
        shapes.append((rect(x - 0.7, y - 1.1, w + 1.4, 2.2, 1.1),
                       ('lin', x, 0, x + w, 0, [(0, NODE), (0.45, JADE), (1, NODE)])))
        shapes.append((rect(x - 0.2, y + 0.7, w + 0.4, 0.5, 0.25), JADE_LIGHT + '|0.7'))

def leaf(bx, by, angle, length, width):
    """Une feuille lancéolée qui part d'un nœud ; dégradé de la base vers la pointe, nervure claire."""
    a = math.radians(angle)
    ux, uy = math.cos(a), math.sin(a)
    px, py = -uy, ux
    tx, ty = bx + ux * length, by + uy * length
    c1 = (bx + ux * length * 0.35 + px * width, by + uy * length * 0.35 + py * width)
    c2 = (bx + ux * length * 0.45 - px * width * 0.55, by + uy * length * 0.45 - py * width * 0.55)
    p = (f'M{n(bx)},{n(by)}Q{n(c1[0])},{n(c1[1])} {n(tx)},{n(ty)}'
         f'Q{n(c2[0])},{n(c2[1])} {n(bx)},{n(by)}Z')
    shapes.append((p, ('lin', bx, by, tx, ty, [(0, LEAF_BASE), (1, LEAF_TIP)])))
    # nervure
    mx, my = bx + ux * length * 0.82, by + uy * length * 0.82
    q = (bx + ux * length * 0.4 + px * width * 0.18, by + uy * length * 0.4 + py * width * 0.18)
    shapes.append((f'M{n(bx)},{n(by)}Q{n(q[0])},{n(q[1])} {n(mx)},{n(my)}'
                   f'Q{n(q[0] + px * 0.5)},{n(q[1] + py * 0.5)} {n(bx)},{n(by)}Z', JADE_SHINE + '|0.45'))

# ——— La composition (dans la zone sûre 21–87) ———
leaf(38, 58, 205, 17, 5.2)                     # feuille derrière, à gauche
stalk(37, 23, 90, 12.5, [40, 57, 74], cut=4.5)  # tige principale, coupée en biais
stalk(59, 33, 90, 8.5, [48, 63, 77], cut=3)     # tige secondaire, plus fine
leaf(49.5, 40, -36, 24, 6.2)                   # bouquet de feuilles au premier nœud
leaf(49.5, 40.8, -6, 19, 5.0)
leaf(49.5, 41.5, 20, 13, 3.8)
leaf(67.5, 48, -64, 18, 4.8)
leaf(67.5, 48.6, -26, 15, 4.0)

# Sceau rouge 子, signature en bas à droite
seal_x, seal_y, seal_s = 69.5, 65, 9.5
shapes.append((rect(seal_x, seal_y, seal_s, seal_s, 1.6), SEAL))
zi = subprocess.run(['python3', 'tools/font/glyph_path.py', '子', str(seal_x + seal_s / 2), str(seal_y + seal_s / 2), '7',
                     'app/src/main/res/font/wenkai.ttf'], capture_output=True, text=True).stdout.strip()
shapes.append((zi, '#FFFFFFFF'))

# ——— Sorties ———
def argb(c):
    """'#AARRGGBB|alpha' → (couleur Android, opacité)."""
    if '|' in c:
        c, op = c.split('|')
        return c, float(op)
    return c, 1.0

def svg_color(c):
    c, op = argb(c)
    return '#' + c[3:], op

def vd_path(p, fill):
    if isinstance(fill, tuple):
        _, x1, y1, x2, y2, stops = fill
        items = ''.join(f'<item android:offset="{o}" android:color="{c}"/>' for o, c in stops)
        return (f'    <path android:pathData="{p}">\n        <aapt:attr name="android:fillColor">\n'
                f'            <gradient android:type="linear" android:startX="{n(x1)}" android:startY="{n(y1)}" '
                f'android:endX="{n(x2)}" android:endY="{n(y2)}">{items}</gradient>\n'
                f'        </aapt:attr>\n    </path>\n')
    c, op = argb(fill)
    alpha = f' android:fillAlpha="{op}"' if op < 1 else ''
    return f'    <path android:fillColor="{c}"{alpha} android:pathData="{p}"/>\n'

HEAD = ('<?xml version="1.0" encoding="utf-8"?>\n<!-- Fabriqué par tools/logo/build_logo.py : {what} -->\n'
        '<vector xmlns:android="http://schemas.android.com/apk/res/android"\n'
        '    xmlns:aapt="http://schemas.android.com/aapt"\n'
        '    android:width="{w}dp" android:height="{w}dp"\n'
        '    android:viewportWidth="{vw}" android:viewportHeight="{vh}">\n')

def write(path, what, body, w=108, vw=108, vh=108, group=None):
    s = HEAD.format(what=what, w=w, vw=vw, vh=vh)
    if group:
        s += f'    <group {group}>\n' + body + '    </group>\n'
    else:
        s += body
    s += '</vector>\n'
    open(path, 'w', encoding='utf-8').write(s)

fg = ''.join(vd_path(p, f) for p, f in shapes)
write(f'{RES}/drawable/ic_launcher_foreground.xml', 'deux tiges de bambou et le sceau 子', fg)
bg_body = vd_path('M0,0h108v108h-108Z', ('lin', 0, 0, 0, 108, [(0, BG_TOP), (1, BG_BOT)]))
write(f'{RES}/drawable/ic_launcher_background.xml', 'fond encre', bg_body)
# Monochrome (icônes à thème d'Android 13) et notification : la silhouette, d'une seule couleur
silhouette = [p for p, f in shapes if not (isinstance(f, str) and ('|' in f or f == '#FFFFFFFF'))]
mono = ''.join(f'    <path android:fillColor="#FFFFFFFF" android:pathData="{p}"/>\n' for p in silhouette[:-1])
write(f'{RES}/drawable/ic_launcher_monochrome.xml', 'silhouette pour les icônes à thème', mono)
write(f'{RES}/drawable/ic_notification.xml', 'silhouette pour la notification', mono, w=24, vw=66, vh=66,
      group='android:translateX="-21" android:translateY="-21"')
# Marque seule (sans fond), pour l'appli
write(f'{RES}/drawable/ic_logo_mark.xml', 'la marque sans fond', fg)
write(f'{RES}/drawable-night/ic_logo_mark.xml', 'la marque sans fond', fg)

# Aperçu SVG : fond + avant-plan, masqué en cercle comme sur un téléphone
defs, body = [], []
for i, (p, f) in enumerate([('M0,0h108v108h-108Z', ('lin', 0, 0, 0, 108, [(0, BG_TOP), (1, BG_BOT)]))] + shapes):
    if isinstance(f, tuple):
        _, x1, y1, x2, y2, stops = f
        st = ''.join(f'<stop offset="{o}" stop-color="#{c[3:]}"/>' for o, c in stops)
        defs.append(f'<linearGradient id="g{i}" gradientUnits="userSpaceOnUse" x1="{n(x1)}" y1="{n(y1)}" x2="{n(x2)}" y2="{n(y2)}">{st}</linearGradient>')
        body.append(f'<path d="{p}" fill="url(#g{i})"/>')
    else:
        c, op = svg_color(f)
        body.append(f'<path d="{p}" fill="{c}" fill-opacity="{op}"/>')
svg = (f'<svg xmlns="http://www.w3.org/2000/svg" viewBox="18 18 72 72" width="432" height="432">'
       f'<defs>{"".join(defs)}<clipPath id="m"><circle cx="54" cy="54" r="54"/></clipPath></defs>'
       f'<g clip-path="url(#m)">{"".join(body)}</g></svg>')
os.makedirs(OUT, exist_ok=True)
open(os.path.join(OUT, 'logo.svg'), 'w', encoding='utf-8').write(svg)
print('ok', len(shapes), 'formes')
