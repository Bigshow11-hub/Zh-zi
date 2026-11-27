"""Liste tous les textes anglais de l'appli (le 2e de chaque trio fr/en/ru) -> tools/i18n/en_strings.json.
Les interpolations Kotlin ($x, ${...}) deviennent {0}, {1}… (même règle qu'à l'exécution pour les nombres)."""
import json, re, sys, pathlib

ROOT = pathlib.Path(__file__).resolve().parents[2] / "app/src/main/java"
LIT = r'"((?:[^"\\]|\\.)*)"'
SEP = r'\s*,\s*(?:\w+\s*=\s*)?'
TRIO = re.compile(LIT + SEP + LIT + SEP + LIT, re.S)
PAIR_EN = re.compile(r'en\s*=\s*' + LIT + r'\s+to\s+' + LIT)
CYR = re.compile('[Ѐ-ӿ]')

def unescape(s):
    return s.replace('\\"', '"').replace('\\n', '\n').replace('\\$', '$').replace("\\'", "'").replace('\\\\', '\\')

def template(s):
    n = [0]
    def rep(_):
        i = n[0]; n[0] += 1
        return '{%d}' % i
    s = re.sub(r'\$\{[^}]*\}|\$[A-Za-z_][A-Za-z0-9_.]*', rep, s)
    return s

out = {}
for f in sorted(ROOT.rglob('*.kt')):
    text = f.read_text(encoding='utf-8')
    pos = 0
    while True:
        m = TRIO.search(text, pos)
        if not m: break
        fr, en, ru = m.group(1), m.group(2), m.group(3)
        if CYR.search(ru) and not CYR.search(en):
            out.setdefault(template(unescape(en)), str(f.relative_to(ROOT)))
            pos = m.end()
        else:
            pos = m.start() + 1
    for m in PAIR_EN.finditer(text):
        out.setdefault(unescape(m.group(1)), str(f.relative_to(ROOT)))
        out.setdefault(unescape(m.group(2)), str(f.relative_to(ROOT)))

# Textes des outils (glosses, clés) : déjà dans Chars.kt via les trios ci-dessus.
json.dump(out, open(pathlib.Path(__file__).parent / 'en_strings.json', 'w', encoding='utf-8'), ensure_ascii=False, indent=0)
print(len(out), 'textes;', sum(len(k.split()) for k in out), 'mots')
