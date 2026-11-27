"""Fabrique app/src/main/java/com/zhuzi/app/data/Chars.kt : une fiche par caractère de l'appli.

Usage, depuis la racine du dépôt : python3 tools/chars/build_chars.py <dictionary.txt de Make Me a Hanzi>
(https://raw.githubusercontent.com/skishore/makemeahanzi/master/dictionary.txt, licence Arphic).
Le sens et l'astuce viennent de tools/chars/glosses.txt, les noms des clés de tools/chars/radicals.txt.
"""
import json, re, sys

dic = {}
for line in open(sys.argv[1], encoding='utf-8'):
    j = json.loads(line)
    dic[j['character']] = j

src = ''.join(open(f'app/src/main/java/com/zhuzi/app/data/{n}.kt', encoding='utf-8').read() for n in ('Hsk1', 'Hsk2', 'Hsk3', 'Hsk3b'))
chars = []
for h in re.findall(r'w\("([^"]+)"', src):
    for c in h:
        if c not in chars:
            chars.append(c)

def table(path, n):
    out = {}
    for line in open(path, encoding='utf-8'):
        if line.startswith('#') or not line.strip():
            continue
        parts = line.rstrip('\n').split('|')
        assert len(parts) == n, line
        out[parts[0]] = parts[1:]
    return out

gloss = table('tools/chars/glosses.txt', 7)
rads = table('tools/chars/radicals.txt', 4)
strokes = json.load(open('app/src/main/assets/strokes.json', encoding='utf-8'))
# Les signes absents de la police pinceau (forme réduite d'une clé) sont remplacés ou laissés de côté
from fontTools.ttLib import TTFont
cmap = TTFont('app/src/main/res/font/wenkai.ttf').getBestCmap()
swap = {'⺼': '月', '⺮': '竹', '耂': '老', '⺀': '冫', '⺌': '小'}
pinyin_fix = {'呢': 'ne', '子': 'zǐ', '了': 'le', '长': 'cháng', '得': 'de', '吧': 'ba', '着': 'zhe'}
kinds = {'pictographic': 1, 'ideographic': 2, 'pictophonetic': 3}

def k(s):
    return '"' + s.replace('\\', '\\\\').replace('"', '\\"').replace('$', '\\$') + '"'

rows = []
# (la police est refaite après ce fichier : on garde aussi les signes que les données de l'appli utilisent déjà)
for c in chars:
    j = dic[c]
    g = gloss[c]
    r = swap.get(j['radical'], j['radical'])
    rn = rads[r]
    comps = ''.join(swap.get(x, x) for x in j['decomposition'] if not ('⿰' <= x <= '⿻') and x != '？' and x != c)
    comps = ''.join(x for x in comps if ord(x) in cmap)
    py = pinyin_fix.get(c, (j.get('pinyin') or [''])[0])
    kind = kinds.get((j.get('etymology') or {}).get('type'), 0)
    n = len(strokes[c]['m']) if c in strokes else 0
    rows.append(
        f"    m['{c}'] = CharInfo('{c}', {k(py)}, {n}, {kind}, {k(r)}, {k(comps)},\n"
        f"        T3({k(g[0])}, {k(g[1])}, {k(g[2])}),\n"
        f"        T3({k(g[3])}, {k(g[4])}, {k(g[5])}),\n"
        f"        T3({k(rn[0])}, {k(rn[1])}, {k(rn[2])}))"
    )

# Par paquets de 80 : une seule fonction trop longue dépasserait la limite de taille du code Java (64 Ko)
CHUNK = 80
parts = [rows[i:i + CHUNK] for i in range(0, len(rows), CHUNK)]
funcs = ''.join(
    f"\nprivate fun part{i}(m: MutableMap<Char, CharInfo>) {{\n" + "\n".join(p) + "\n}\n"
    for i, p in enumerate(parts)
)
calls = ' '.join(f'part{i}(m);' for i in range(len(parts)))

kt = f'''package com.zhuzi.app.data

// Fichier fabriqué par tools/chars/build_chars.py : ne pas modifier à la main.
// Décomposition, clé et type : Make Me a Hanzi (licence publique Arphic).

/**
 * Fiche d'un caractère : pinyin, nombre de traits, type (0 inconnu, 1 pictogramme, 2 idéogramme,
 * 3 idéo-phonogramme), clé, composants, sens, astuce pour le retenir, sens de la clé.
 */
data class CharInfo(
    val char: Char,
    val pinyin: String,
    val strokes: Int,
    val kind: Int,
    val radical: String,
    val components: String,
    val meaning: T3,
    val memo: T3,
    val radicalMeaning: T3,
)

/** Les {len(rows)} caractères de l'appli, dans l'ordre où ils apparaissent dans les leçons. */
val charInfos: Map<Char, CharInfo> by lazy {{
    val m = LinkedHashMap<Char, CharInfo>()
    {calls}
    m
}}
{funcs}'''
open('app/src/main/java/com/zhuzi/app/data/Chars.kt', 'w', encoding='utf-8').write(kt)
print(len(rows), 'caractères')
