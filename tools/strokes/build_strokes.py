"""Regroupe les tracés (hanzi-writer-data) des caractères de l'appli dans assets/strokes.json.

Usage, depuis la racine du dépôt :
    python3 tools/strokes/build_strokes.py /chemin/vers/package
(`package` = dossier obtenu avec `npm pack hanzi-writer-data` puis `tar xzf`.)
"""
import json
import os
import sys

pkg = sys.argv[1]
text = open('app/src/main/java/com/zhuzi/app/data/Hsk1.kt', encoding='utf-8').read()
chars = sorted(set(c for c in text if '一' <= c <= '鿿'))
out, missing = {}, []
for c in chars:
    f = os.path.join(pkg, c + '.json')
    if not os.path.exists(f):
        missing.append(c)
        continue
    d = json.load(open(f, encoding='utf-8'))
    out[c] = {'s': d['strokes'], 'm': d['medians']}
json.dump(out, open('app/src/main/assets/strokes.json', 'w', encoding='utf-8'),
          ensure_ascii=False, separators=(',', ':'))
print(len(out), 'caractères ; sans tracé :', missing)
